package com.dkatalis.atm;

import com.dkatalis.atm.exceptions.AtmException;
import com.dkatalis.atm.exceptions.ErrorCode;

import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 10.08.2021
 */
public final class ValidationUtils {

    public static void requireNotUserLoggedIn(String currentUserLogin) {
        if (currentUserLogin != null)
            throw new AtmException(ErrorCode.USER_IS_LOGGED_IN, currentUserLogin);
    }

    public static void requireUserLoggedIn(String currentUserLogin) {
        if (currentUserLogin == null)
            throw new AtmException(ErrorCode.USER_IS_NOT_LOGGED_IN);
    }

    public static void requireCurrentUserMatchWithUserLogin(String currentUserLogin, String expectedUserLogin) {
        if (currentUserLogin == null || expectedUserLogin == null)
            return;
        if (!currentUserLogin.equalsIgnoreCase(expectedUserLogin))
            throw new AtmException(ErrorCode.CURRENT_USER_IS_NOT_EXPECTED, currentUserLogin, expectedUserLogin);
    }

    public static void requireUserLoginExists(String userLogin, Set<String> existedUserLogins) {
        if (!existedUserLogins.contains(userLogin))
            throw new AtmException(ErrorCode.USER_IS_NOT_EXIST, userLogin);
    }

    private ValidationUtils() {
    }

}
