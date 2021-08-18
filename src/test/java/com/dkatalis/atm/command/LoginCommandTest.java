package com.dkatalis.atm.command;

import com.dkatalis.atm.command.LoginCommand;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.dkatalis.atm.AtmContext;
import com.dkatalis.atm.exceptions.AtmException;
import com.dkatalis.atm.exceptions.ErrorCode;

import java.io.PrintStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Oleg Cherednik
 * @since 10.08.2021
 */
@Test
public class LoginCommandTest {

    private LoginCommand command;

    @BeforeTest
    public void createCommand() {
        command = new LoginCommand("oleg");
    }

    public void shouldInvokeLoginOnContextWhenExecute() {
        AtmContext context = mock(AtmContext.class);
        boolean res = command.execute(context);

        assertThat(res).isTrue();
        verify(context, times(1)).loginUser(eq("oleg"));
        verifyNoMoreInteractions(context);
    }

    public void shouldRetrieveDescriptionWhenToString() {
        String str = command.toString();
        assertThat(str).isEqualTo("login oleg");
    }

    public void shouldThrowIncorrectArgumentsAmountExceptionWhenCommandArgumentsDoesNotHaveExactlyOneArgument() {
        for (String[] commandArguments : Arrays.asList(null, ArrayUtils.EMPTY_STRING_ARRAY, new String[] { "1", "2" }))
            assertThatThrownBy(() -> LoginCommand.validateCommandArguments(commandArguments))
                    .isExactlyInstanceOf(AtmException.class)
                    .hasMessage(ErrorCode.INCORRECT_ARGUMENT_AMOUNT.name());
    }

    public void shouldThrowIncorrectArgumentContentExceptionWhenUserLoginIsBlank() {
        for (String userLogin : Arrays.asList(null, "", "   "))
            assertThatThrownBy(() -> LoginCommand.validateCommandArguments(userLogin))
                    .isExactlyInstanceOf(AtmException.class)
                    .hasMessage(ErrorCode.INCORRECT_ARGUMENT_CONTENT.name());
    }

    public void shouldNotThrowExceptionWhenCommandArgumentsHasExactlyOneArgument() {
        assertThatCode(() -> LoginCommand.validateCommandArguments("oleg"))
                .doesNotThrowAnyException();
    }

    public void shouldRetrieveFirstArgumentAsUserLoginWhenGetUserLogin() {
        assertThatCode(() -> {
            String userLogin = LoginCommand.getUserLogin("oleg");
            assertThat(userLogin).isEqualTo("oleg");
        }).doesNotThrowAnyException();
    }

    public void shouldPrintHelp() {
        PrintStream out = mock(PrintStream.class);
        LoginCommand.printHelp(out);
        verify(out, times(1)).format(eq("%s [name] - logs in as this customer and creates the customer if not exist\n"),
                eq("login"));
        verifyNoMoreInteractions(out);
    }

}
