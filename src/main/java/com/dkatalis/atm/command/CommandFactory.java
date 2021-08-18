package com.dkatalis.atm.command;

import org.apache.commons.lang3.ArrayUtils;
import com.dkatalis.atm.exceptions.AtmException;
import com.dkatalis.atm.exceptions.ErrorCode;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author Oleg Cherednik
 * @since 09.08.2021
 */
public final class CommandFactory {

    public static Command createCommand(String currentUserLogin, String... args) {
        requireNotEmptyArguments(args);

        String commandName = args[0].toLowerCase(Locale.ROOT).trim();
        String[] commandArguments = getCommandArguments(args);

        if (commandName.equalsIgnoreCase(LoginCommand.NAME))
            return createLoginCommand(commandArguments);
        if (commandName.equalsIgnoreCase(LogoutCommand.NAME))
            return createLogoutCommand(currentUserLogin, commandArguments);
        if (commandName.equalsIgnoreCase(DepositCommand.NAME))
            return createDepositCommand(currentUserLogin, commandArguments);
        if (commandName.equalsIgnoreCase(WithdrawCommand.NAME))
            return createWithdrawCommand(currentUserLogin, commandArguments);
        if (commandName.equalsIgnoreCase(TransferCommand.NAME))
            return createTransferCommand(currentUserLogin, commandArguments);
        if (commandName.equalsIgnoreCase(HelpCommand.NAME))
            return createHelpCommand(commandArguments);
        if (commandName.equalsIgnoreCase(ExitCommand.NAME))
            return createExitCommand(commandArguments);

        throw new AtmException(ErrorCode.UNKNOWN_COMMAND, commandName);
    }

    private static void requireNotEmptyArguments(String... args) {
        if (ArrayUtils.isEmpty(args))
            throw new AtmException(ErrorCode.EMPTY_INPUT);
    }

    private static String[] getCommandArguments(String... args) {
        return args.length == 1 ? null : Arrays.copyOfRange(args, 1, args.length);
    }

    private static LoginCommand createLoginCommand(String... commandArguments) {
        LoginCommand.validateCommandArguments(commandArguments);
        String userLogin = LoginCommand.getUserLogin(commandArguments);
        return new LoginCommand(userLogin);
    }

    private static LogoutCommand createLogoutCommand(String currentUserLogin, String... commandArguments) {
        LogoutCommand.validateCommandArguments(commandArguments);
        return new LogoutCommand(currentUserLogin);
    }

    private static DepositCommand createDepositCommand(String currentUserLogin, String... commandArguments) {
        DepositCommand.validateCommandArguments(commandArguments);
        int depositAmount = DepositCommand.getDepositAmount(commandArguments);
        return new DepositCommand(currentUserLogin, depositAmount);
    }

    private static WithdrawCommand createWithdrawCommand(String currentUserLogin, String... commandArguments) {
        WithdrawCommand.validateCommandArguments(commandArguments);
        int depositAmount = WithdrawCommand.getWithdrawAmount(commandArguments);
        return new WithdrawCommand(currentUserLogin, depositAmount);
    }

    private static TransferCommand createTransferCommand(String currentUserLogin, String... commandArguments) {
        TransferCommand.validateCommandArguments(commandArguments);
        String userLoginTo = TransferCommand.getUserLoginTo(commandArguments);
        int transferAmount = TransferCommand.getTransferAmount(commandArguments);
        return new TransferCommand(currentUserLogin, userLoginTo, transferAmount);
    }

    private static HelpCommand createHelpCommand(String... commandArguments) {
        HelpCommand.validateCommandArguments(commandArguments);
        return new HelpCommand();
    }

    private static ExitCommand createExitCommand(String... commandArguments) {
        ExitCommand.validateCommandArguments(commandArguments);
        return null;
    }

    private CommandFactory() {
    }

}
