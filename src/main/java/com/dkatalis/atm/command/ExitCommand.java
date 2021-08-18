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
final class ExitCommand implements Command {

    public static final String NAME = "exit";

    protected ExitCommand() {
    }

    // ========== Command ==========

    @Override
    public boolean execute(AtmContext context) {
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
        out.format("%s - close the program\n", NAME);
    }

}
