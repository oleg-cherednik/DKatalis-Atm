package com.dkatalis.atm.command;

import org.apache.commons.lang3.ArrayUtils;
import com.dkatalis.atm.AtmContext;
import com.dkatalis.atm.exceptions.AtmException;
import com.dkatalis.atm.exceptions.ErrorCode;

import java.io.PrintStream;

/**
 * @author Oleg Cherednik
 * @since 09.08.2021
 */
final class DepositCommand implements Command {

    public static final String NAME = "deposit";

    private final String userLogin;
    private final int amount;

    public DepositCommand(String userLogin, int amount) {
        this.userLogin = userLogin;
        this.amount = amount;
    }

    // ========== Command ==========

    @Override
    public boolean execute(AtmContext context) {
        context.depositForUserLogin(userLogin, amount);
        return true;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return NAME + ' ' + userLogin + ' ' + amount;
    }

    // ========== static ==========

    public static void validateCommandArguments(String... commandArguments) {
        if (ArrayUtils.getLength(commandArguments) != 1)
            throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_AMOUNT, 1);

        try {
            if (getDepositAmount(commandArguments) <= 0)
                throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_CONTENT,
                        "'depositAmount' should be above 0");
        } catch (NumberFormatException e) {
            throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_CONTENT,
                    "'depositAmount' should be an integer value");
        }
    }

    public static int getDepositAmount(String... commandArguments) {
        return Integer.parseInt(commandArguments[0]);
    }

    public static void printHelp(PrintStream out) {
        out.format("%s [amount] - deposits this amount to the logged in customer\n", NAME);
    }

}
