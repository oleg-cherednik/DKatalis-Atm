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
final class LoginCommand implements Command {

    public static final String NAME = "login";

    private final String userLogin;

    public LoginCommand(String userLogin) {
        this.userLogin = userLogin;
    }

    // ========== Command ==========

    @Override
    public boolean execute(AtmContext context) {
        context.loginUser(userLogin);
        return true;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return NAME + ' ' + userLogin;
    }

    // ========== static ==========

    public static void validateCommandArguments(String... commandArguments) {
        if (ArrayUtils.getLength(commandArguments) != 1)
            throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_AMOUNT, 1);

        if (StringUtils.isBlank(getUserLogin(commandArguments)))
            throw new AtmException(ErrorCode.INCORRECT_ARGUMENT_CONTENT,
                    "'userLogin' should not be blank");
    }

    public static String getUserLogin(String... commandArguments) {
        return commandArguments[0];
    }

    public static void printHelp(PrintStream out) {
        out.format("%s [name] - logs in as this customer and creates the customer if not exist\n", NAME);
    }

}
