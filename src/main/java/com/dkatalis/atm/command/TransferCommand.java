package com.dkatalis.atm.command;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import com.dkatalis.atm.AtmContext;
import com.dkatalis.atm.exceptions.AtmException;
import com.dkatalis.atm.exceptions.ErrorCode;

import java.io.PrintStream;

/**
 * @author Oleg Cherednik
 * @since 09.08.2021
 */
final class TransferCommand implements Command {

    public static final String NAME = "transfer";

    private final String userLoginFrom;
    private final String userLoginTo;
    private final int amount;

    TransferCommand(String userLoginFrom, String userLoginTo, int amount) {
        this.userLoginFrom = userLoginFrom;
        this.userLoginTo = userLoginTo;
        this.amount = amount;
    }

    // ========== Command ==========

    @Override
    public boolean execute(AtmContext context) {
        context.transferBetweenUserLogin(userLoginFrom, userLoginTo, amount);
        return true;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return NAME + ' ' + userLoginFrom + ' ' + userLoginTo + ' ' + amount;
    }

    // ========== static ==========

    public static void validateCommandArguments(String... commandArguments) {
        if (ArrayUtils.getLength(commandArguments) != 2)
            throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_AMOUNT, 2);

        if (StringUtils.isBlank(getUserLoginTo(commandArguments)))
            throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_CONTENT,
                    "'userName' should not be blank");

        try {
            if (getTransferAmount(commandArguments) <= 0)
                throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_CONTENT,
                        "'transferAmount' should be above 0");
        } catch (NumberFormatException e) {
            throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_CONTENT,
                    "'transferAmount' should be an integer value");
        }
    }

    public static String getUserLoginTo(String... commandArguments) {
        return StringUtils.lowerCase(commandArguments[0]);
    }

    public static int getTransferAmount(String... commandArguments) {
        return Integer.parseInt(commandArguments[1]);
    }

    public static void printHelp(PrintStream out) {
        out.format("%s [target] [amount] - transfers this amount from the logged in" +
                " customer to the target customer\n", NAME);
    }

}
