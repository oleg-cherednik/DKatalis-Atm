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
final class HelpCommand implements Command {

    public static final String NAME = "help";

    // ========== Command ==========

    @Override
    public boolean execute(AtmContext context) {
        LoginCommand.printHelp(context.out());
        LogoutCommand.printHelp(context.out());
        DepositCommand.printHelp(context.out());
        WithdrawCommand.printHelp(context.out());
        TransferCommand.printHelp(context.out());
        ExitCommand.printHelp(context.out());
        HelpCommand.printHelp(context.out());
        return false;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return NAME;
    }

    // ========== static ==========

    public static void validateCommandArguments(String... commandArguments) {
        if (ArrayUtils.isNotEmpty(commandArguments))
            throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_AMOUNT, 0);
    }

    public static void printHelp(PrintStream out) {
        out.format("%s - print this help\n", NAME);
    }

}
