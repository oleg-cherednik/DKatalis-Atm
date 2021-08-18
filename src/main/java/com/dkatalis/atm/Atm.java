package com.dkatalis.atm;

import org.apache.commons.lang3.StringUtils;
import com.dkatalis.atm.command.Command;
import com.dkatalis.atm.command.CommandFactory;
import com.dkatalis.atm.exceptions.AtmException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Scanner;

/**
 * @author Oleg Cherednik
 * @since 09.08.2021
 */
public class Atm implements AtmContext {

    private final PrintStream out;
    private final PrintStream err;
    private final Scanner scan;
    private final Map<String, UserAccount> userAccounts = new HashMap<>();

    private String currentUserLogin;

    public Atm(InputStream in, PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
        scan = new Scanner(in);
    }

    public void start() {
        out.println("Welcome to ATM simulation.");
        out.println("- command is case insensitive");
        out.println("- user login is case insensitive");
        out.println("- type 'help' for help notes and");
        out.println();

        while (true) {
            try {
                String prvUserLogin = currentUserLogin;
                Command command = getCommand();

                if (command == null)
                    break;

                if (command.execute(this))
                    printCommandMessage(prvUserLogin);
            } catch (AtmException e) {
                err.format("Error: %s\n", e.getMessage(this));
            } catch (Exception e) {
                err.format("Error: %s\n", e.getMessage());
            } finally {
                out.println();
            }
        }
    }

    private int getBalanceByUserLogin(String userLogin) {
        UserAccount userAccount = userAccounts.get(userLogin);
        return userAccount == null ? 0 : userAccount.balance;
    }

    private Command getCommand() {
        out.format("%s> ", getUserNameByLogin(currentUserLogin));
        String[] args = scan.nextLine().trim().split("\\s+");
        return CommandFactory.createCommand(currentUserLogin, args);
    }

    private String createNewOrGetExistedUserByLogin(String userName) {
        String userLogin = userName.toLowerCase();

        if (!userAccounts.containsKey(userLogin))
            userAccounts.put(userLogin, new UserAccount(userLogin, userName));

        return userLogin;
    }

    private void addDebt(String userLoginFrom, String userLoginTo, int amount) {
        if (amount > 0) {
            userAccounts.get(userLoginFrom).changeDebtTo(userLoginTo, amount);
            userAccounts.get(userLoginTo).changeDebtFrom(userLoginFrom, amount);
        }
    }

    private void changeBalanceByUserLogin(String userLogin, int amount) {
        userAccounts.get(userLogin).balance += amount;
    }

    private void printCommandMessage(String prvUserLogin) {
        boolean userExistsBefore = prvUserLogin != null;
        boolean userExistsAfter = currentUserLogin != null;

        if (userExistsBefore) {
            if (!userExistsAfter)
                out.format("Goodbye, %s!\n", getUserNameByLogin(prvUserLogin));
        } else {
            if (userExistsAfter)
                out.format("Hello, %s!\n", getUserNameByLogin(currentUserLogin));
        }

        if (userExistsAfter) {
            printBalanceByUserLogin(currentUserLogin);
            printDebtsByUserLogin(currentUserLogin);
        }
    }

    private void printBalanceByUserLogin(String userLogin) {
        out.format("Your balance is $%d\n", getBalanceByUserLogin(userLogin));
    }

    private void printDebtsByUserLogin(String userLoginFrom) {
        UserAccount userAccount = userAccounts.get(userLoginFrom);
        userAccount.printDebtsByUserLoginTo();
        userAccount.printDebtsByUserLoginFrom();
    }

    private void printTran(String userLoginFrom, String userLoginTo, int transferAmount) {
        if (userLoginFrom.equals(currentUserLogin) && transferAmount > 0)
            out.format("Transferred $%d to %s\n", transferAmount, getUserNameByLogin(userLoginTo));
    }

    // ========== AtmContext ==========

    @Override
    public void loginUser(String userLogin) {
        ValidationUtils.requireNotUserLoggedIn(currentUserLogin);
        currentUserLogin = createNewOrGetExistedUserByLogin(userLogin);
    }

    @Override
    public void logoutByUserLogin(String userLogin) {
        ValidationUtils.requireUserLoggedIn(currentUserLogin);
        ValidationUtils.requireCurrentUserMatchWithUserLogin(currentUserLogin, userLogin);
        currentUserLogin = null;
    }

    @Override
    public void depositForUserLogin(String userLogin, int amount) {
        if (amount <= 0)
            return;

        ValidationUtils.requireUserLoggedIn(currentUserLogin);
        ValidationUtils.requireUserLoginExists(userLogin, userAccounts.keySet());

        UserAccount userAccount = userAccounts.get(userLogin);
        int availableAmount = userAccount.payDebts(amount);
        changeBalanceByUserLogin(userLogin, availableAmount);
    }

    @Override
    public void withdrawForUserLogin(String userLogin, int amount) {
        if (amount <= 0)
            return;

        ValidationUtils.requireUserLoggedIn(currentUserLogin);
        ValidationUtils.requireUserLoginExists(userLogin, userAccounts.keySet());

        UserAccount userAccount = userAccounts.get(userLogin);
        int availableAmount = Math.min(userAccount.balance, amount);
        changeBalanceByUserLogin(userLogin, -availableAmount);
    }

    @Override
    public void transferBetweenUserLogin(String userLoginFrom, String userLoginTo, int amount) {
        ValidationUtils.requireUserLoggedIn(currentUserLogin);
        ValidationUtils.requireUserLoginExists(userLoginFrom, userAccounts.keySet());
        ValidationUtils.requireUserLoginExists(userLoginTo, userAccounts.keySet());

        UserAccount userAccountTo = userAccounts.get(userLoginTo);
        int balance = getBalanceByUserLogin(userLoginFrom);
        int transferAmount = userAccountTo.repayDebts(userLoginFrom, Math.min(balance, amount));

        if (balance == 0 || transferAmount > 0) {
            int debtAmount = Math.max(0, amount - transferAmount);

            changeBalanceByUserLogin(userLoginFrom, -transferAmount);
            addDebt(userLoginFrom, userLoginTo, debtAmount);
            depositForUserLogin(userLoginTo, transferAmount);

            printTran(userLoginFrom, userLoginTo, transferAmount);
        }
    }

    @Override
    public String getUserNameByLogin(String userLogin) {
        UserAccount userAccount = userAccounts.get(StringUtils.lowerCase(userLogin));
        return userAccount == null ? "" : userAccount.name;
    }

    @Override
    public PrintStream out() {
        return out;
    }

    // ========== UserAccount ==========

    private final class UserAccount {

        private final String login;
        private final String name;
        private final Map<String, Integer> debtsTo = new HashMap<>();
        private final Map<String, Integer> debtsFrom = new HashMap<>();
        private final Queue<String> payDebtToOrder = new LinkedList<>();

        private int balance;

        public UserAccount(String login, String name) {
            this.login = login;
            this.name = name;
        }

        public void changeDebtTo(String userLoginTo, int amount) {
            if (debtsTo.containsKey(userLoginTo))
                debtsTo.put(userLoginTo, debtsTo.get(userLoginTo) + amount);
            else {
                debtsTo.put(userLoginTo, amount);
                payDebtToOrder.add(userLoginTo);
            }
        }

        public void changeDebtFrom(String userLoginFrom, int amount) {
            debtsFrom.compute(userLoginFrom, (key, debtAmount) ->
                    Optional.ofNullable(debtAmount).orElse(0) + amount);
        }

        public int getDebtByUserLoginTo(String userLoginTo) {
            return debtsTo.getOrDefault(userLoginTo, 0);
        }

        public void removePayedDebtByUserLoginTo(String userLoginTo) {
            if (debtsTo.containsKey(userLoginTo) && debtsTo.get(userLoginTo) == 0) {
                debtsTo.remove(userLoginTo);
                payDebtToOrder.remove(userLoginTo);
            }
        }

        public void removePayedDebtByUserLoginFrom(String userLoginFrom) {
            if (debtsFrom.containsKey(userLoginFrom) && debtsFrom.get(userLoginFrom) == 0)
                debtsFrom.remove(userLoginFrom);
        }

        private void printDebtsByUserLoginTo() {
            if (!payDebtToOrder.isEmpty())
                new ArrayList<>(payDebtToOrder).forEach(userLoginTo ->
                        out.format("Owed $%d to %s\n", debtsTo.get(userLoginTo), getUserNameByLogin(userLoginTo)));
        }

        private void printDebtsByUserLoginFrom() {
            debtsFrom.forEach((userLoginFrom, amount) ->
                    out.format("Owed $%d from %s\n", amount, getUserNameByLogin(userLoginFrom)));
        }

        public int payDebts(int availableAmount) {
            while (!payDebtToOrder.isEmpty() && availableAmount > 0) {
                String userLoginTo = payDebtToOrder.element();
                UserAccount userAccountTo = userAccounts.get(userLoginTo);

                int debtAmount = getDebtByUserLoginTo(userLoginTo);
                int ableToPay = Math.min(availableAmount, debtAmount);

                changeDebtTo(userLoginTo, -ableToPay);
                userAccountTo.changeDebtFrom(login, -ableToPay);

                depositForUserLogin(userLoginTo, ableToPay);
                printTran(login, userLoginTo, ableToPay);

                removePayedDebtByUserLoginTo(userLoginTo);
                userAccountTo.removePayedDebtByUserLoginFrom(login);

                availableAmount -= ableToPay;
            }

            return availableAmount;
        }

        public int repayDebts(String userLoginTo, int availableAmount) {
            UserAccount userAccountFrom = this;
            UserAccount userAccountTo = userAccounts.get(userLoginTo);

            if (userAccountTo.debtsFrom.containsKey(userAccountFrom.login)) {
                int debtAmount = userAccountFrom.getDebtByUserLoginTo(userLoginTo);
                int ableToPay = Math.min(availableAmount, debtAmount);

                userAccountFrom.changeDebtTo(userLoginTo, -ableToPay);
                userAccountTo.changeDebtFrom(userAccountFrom.login, -ableToPay);

                userAccountFrom.removePayedDebtByUserLoginTo(userLoginTo);
                userAccountTo.removePayedDebtByUserLoginFrom(userAccountFrom.login);

                availableAmount -= ableToPay;
            }

            return availableAmount;
        }

        // ========== Object ==========


        @Override
        public String toString() {
            return login + ' ' + balance;
        }

    }

    // ========== main ==========

    public static void main(String[] args) {
        new Atm(System.in, System.out, System.err).start();
    }

}
