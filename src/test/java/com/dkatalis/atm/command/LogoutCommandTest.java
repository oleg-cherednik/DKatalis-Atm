package com.dkatalis.atm.command;

import com.dkatalis.atm.command.LogoutCommand;
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
public class LogoutCommandTest {

    private LogoutCommand command;

    @BeforeTest
    public void createCommand() {
        command = new LogoutCommand("oleg");
    }

    public void shouldInvokeLogoutOnContextWhenExecute() {
        AtmContext context = mock(AtmContext.class);
        boolean res = command.execute(context);

        assertThat(res).isTrue();
        verify(context, times(1)).logoutByUserLogin(eq("oleg"));
        verifyNoMoreInteractions(context);
    }

    public void shouldRetrieveDescriptionWhenToString() {
        String str = command.toString();
        assertThat(str).isEqualTo("logout oleg");
    }

    public void shouldThrowIncorrectArgumentsAmountExceptionWhenCommandArgumentsContainsArguments() {
        assertThatThrownBy(() -> LogoutCommand.validateCommandArguments("oleg"))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.INCORRECT_ARGUMENT_AMOUNT.name());
    }

    public void shouldNotThrowExceptionWhenCommandArgumentsHasNoArguments() {
        for (String[] commandArguments : Arrays.asList(null, ArrayUtils.EMPTY_STRING_ARRAY))
            assertThatCode(() -> LogoutCommand.validateCommandArguments(commandArguments))
                    .doesNotThrowAnyException();
    }

    public void shouldPrintHelp() {
        PrintStream out = mock(PrintStream.class);
        LogoutCommand.printHelp(out);
        verify(out, times(1)).format(eq("%s - logs out of the current customer\n"), eq("logout"));
        verifyNoMoreInteractions(out);
    }

}
