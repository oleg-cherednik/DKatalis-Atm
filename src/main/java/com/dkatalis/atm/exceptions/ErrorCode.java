package com.dkatalis.atm.exceptions;

import org.apache.commons.lang3.StringUtils;
import com.dkatalis.atm.AtmContext;

/**
 * @author Oleg Cherednik
 * @since 09.08.2021
 */
public enum ErrorCode {

    USER_IS_LOGGED_IN {
        @Override
        public String getMessage(AtmContext context, Object... args) {
            String userLogin = String.valueOf(args[0]);
            String userName = StringUtils.defaultIfBlank(context.getUserNameByLogin(userLogin), userLogin);
            return String.format("User '%s' is now logged in", userName);
        }
    },
    USER_IS_NOT_LOGGED_IN {
        @Override
        public String getMessage(AtmContext context, Object... args) {
            return "No user is logged in";
        }
    },
    USER_IS_NOT_EXIST {
        @Override
        public String getMessage(AtmContext context, Object... args) {
            String userLogin = String.valueOf(args[0]);
            String userName = StringUtils.defaultIfBlank(context.getUserNameByLogin(userLogin), userLogin);
            return String.format("User '%s' is not exist", userName);
        }
    },
    CURRENT_USER_IS_NOT_EXPECTED {
        @Override
        public String getMessage(AtmContext context, Object... args) {
            String currentUserLogin = String.valueOf(args[0]);
            String actualUserLogin = String.valueOf(args[1]);
            String currentUserName = StringUtils.defaultIfBlank(context.getUserNameByLogin(currentUserLogin), currentUserLogin);
            String actualUserName = StringUtils.defaultIfBlank(context.getUserNameByLogin(actualUserLogin), actualUserLogin);
            return String.format("Current user '%s' is not match with expected '%s'",
                    currentUserName, actualUserName);
        }
    },
    INCORRECT_ARGUMENT_AMOUNT {
        @Override
        public String getMessage(AtmContext context, Object... args) {
            int expectedArgumentsAmount = (int)args[0];
            return String.format("Expected exactly '%d' arguments", expectedArgumentsAmount);
        }
    },
    INCORRECT_ARGUMENT_CONTENT {
        @Override
        public String getMessage(AtmContext context, Object... args) {
            return String.valueOf(args[0]);
        }
    },
    UNKNOWN_COMMAND {
        @Override
        public String getMessage(AtmContext context, Object... args) {
            String commandName = String.valueOf(args[0]);
            return String.format("Command '%s' is not known; use 'help' command for help", commandName);
        }
    },
    EMPTY_INPUT {
        @Override
        public String getMessage(AtmContext context, Object... args) {
            return "Input is empty; please type a command";
        }
    };

    public abstract String getMessage(AtmContext context, Object... args);

}
