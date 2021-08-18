package com.dkatalis.atm;

import com.dkatalis.atm.ValidationUtils;
import org.testng.annotations.Test;
import com.dkatalis.atm.exceptions.AtmException;
import com.dkatalis.atm.exceptions.ErrorCode;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 10.08.2021
 */
@Test
public class ValidationUtilsTest {

    public void shouldThrowUserIsLoggedInExceptionWhenCurrentUserLoginIsNotNull() {
        assertThatThrownBy(() -> ValidationUtils.requireNotUserLoggedIn("oleg"))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.USER_IS_LOGGED_IN.name());
    }

    public void shouldNotThrowExceptionWhenCurrentUserIsNull() {
        assertThatCode(() -> ValidationUtils.requireNotUserLoggedIn(null))
                .doesNotThrowAnyException();
    }

    public void shouldThrowUserIsNotLoggedInExceptionWhenCurrentUserLoginIsNull() {
        assertThatThrownBy(() -> ValidationUtils.requireUserLoggedIn(null))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.USER_IS_NOT_LOGGED_IN.name());
    }

    public void shouldNotThrowExceptionWhenCurrentUserIsNotNull() {
        assertThatCode(() -> ValidationUtils.requireUserLoggedIn("oleg"))
                .doesNotThrowAnyException();
    }

    public void shouldNotThrowExceptionWhenEitherCurrentUserOrExpectedUserIsNull() {
        assertThatCode(() -> ValidationUtils.requireCurrentUserMatchWithUserLogin(null, "oleg"))
                .doesNotThrowAnyException();
        assertThatCode(() -> ValidationUtils.requireCurrentUserMatchWithUserLogin("oleg", null))
                .doesNotThrowAnyException();
        assertThatCode(() -> ValidationUtils.requireCurrentUserMatchWithUserLogin(null, null))
                .doesNotThrowAnyException();
    }

    public void shouldNotThrowExceptionWhenCurrentUserAndExpectedUserSame() {
        assertThatCode(() -> ValidationUtils.requireCurrentUserMatchWithUserLogin("oleg", "oleg"))
                .doesNotThrowAnyException();
    }

    public void shouldThrowCurrentUserIsNotExpectedWhenCurrentUserAndExpectedUserNotSame() {
        assertThatThrownBy(() -> ValidationUtils.requireCurrentUserMatchWithUserLogin("oleg", "anna"))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.CURRENT_USER_IS_NOT_EXPECTED.name());
    }

    public void shouldThrowUserIsNotExistExpectedWhenUserWasNotCreated() {
        assertThatThrownBy(() -> ValidationUtils.requireUserLoginExists("oleg", Collections.emptySet()))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.USER_IS_NOT_EXIST.name());

    }

    public void shouldNotThrowExceptionWhenUserExists() {
        assertThatCode(() -> ValidationUtils.requireUserLoginExists("oleg", Collections.singleton("oleg")))
                .doesNotThrowAnyException();
    }

}
