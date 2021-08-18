package com.dkatalis.atm.exceptions;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.dkatalis.atm.AtmContext;
import com.dkatalis.atm.exceptions.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oleg Cherednik
 * @since 10.08.2021
 */
@Test
public class ErrorCodeTest {

    private AtmContext context;

    @BeforeTest
    public void createContext() {
        context = mock(AtmContext.class);
        when(context.getUserNameByLogin(eq("oleg"))).thenReturn("Oleg");
    }

    public void shouldRetrieveMessageWhenUserIdLoggedIn() {
        String str = ErrorCode.USER_IS_LOGGED_IN.getMessage(context, "oleg");
        assertThat(str).isEqualTo("User 'Oleg' is now logged in");
    }

    public void shouldRetrieveMessageWhenUserIsNotLoggedIn() {
        String str = ErrorCode.USER_IS_NOT_LOGGED_IN.getMessage(context);
        assertThat(str).isEqualTo("No user is logged in");
    }

    public void shouldRetrieveMessageWhenCurrentUserIdNotExpected() {
        when(context.getUserNameByLogin(eq("anna"))).thenReturn("Anna");
        String str = ErrorCode.CURRENT_USER_IS_NOT_EXPECTED.getMessage(context, "oleg", "anna");
        assertThat(str).isEqualTo("Current user 'Oleg' is not match with expected 'Anna'");
    }

    public void shouldRetrieveMessageWhenIncorrectArgumentAmount() {
        String str = ErrorCode.INCORRECT_ARGUMENT_AMOUNT.getMessage(context, 1);
        assertThat(str).isEqualTo("Expected exactly '1' arguments");
    }

    public void shouldRetrieveMessageWhenIncorrectArgumentContent() {
        String str = ErrorCode.INCORRECT_ARGUMENT_CONTENT.getMessage(context, "aaa");
        assertThat(str).isEqualTo("aaa");
    }

    public void shouldRetrieveMessageWhenUnknownCommand() {
        String str = ErrorCode.UNKNOWN_COMMAND.getMessage(context, "aaa");
        assertThat(str).isEqualTo("Command 'aaa' is not known; use 'help' command for help");
    }

    public void shouldRetrieveMessageWhenEmptyInput() {
        String str = ErrorCode.EMPTY_INPUT.getMessage(context);
        assertThat(str).isEqualTo("Input is empty; please type a command");
    }

    public void shouldRetrieveMessageWhenUserIsNotExist() {
        when(context.getUserNameByLogin(eq("anna"))).thenReturn("");

        String str = ErrorCode.USER_IS_NOT_EXIST.getMessage(context, "anna");
        assertThat(str).isEqualTo("User 'anna' is not exist");
    }

}
