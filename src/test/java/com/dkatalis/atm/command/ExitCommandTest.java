package com.dkatalis.atm.command;

import com.dkatalis.atm.command.ExitCommand;
import org.apache.commons.lang3.ArrayUtils;
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
public class ExitCommandTest {

    public void shouldNotInvokeContextWhenExecute() {
        AtmContext context = mock(AtmContext.class);
        boolean res = new ExitCommand().execute(context);

        assertThat(res).isFalse();
        verifyNoMoreInteractions(context);
    }

    public void shouldThrowIncorrectArgumentsAmountExceptionWhenCommandArgumentsContainsArguments() {
        assertThatThrownBy(() -> ExitCommand.validateCommandArguments("oleg"))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.INCORRECT_ARGUMENT_AMOUNT.name());
    }

    public void shouldNotThrowExceptionWhenCommandArgumentsHasNoArguments() {
        for (String[] commandArguments : Arrays.asList(null, ArrayUtils.EMPTY_STRING_ARRAY))
            assertThatCode(() -> ExitCommand.validateCommandArguments(commandArguments))
                    .doesNotThrowAnyException();
    }

    public void shouldRetrieveDescriptionWhenToString() {
        String str = new ExitCommand().toString();
        assertThat(str).isEqualTo("exit");
    }

    public void shouldPrintHelp() {
        PrintStream out = mock(PrintStream.class);
        ExitCommand.printHelp(out);
        verify(out, times(1)).format(eq("%s - close the program\n"), eq("exit"));
        verifyNoMoreInteractions(out);
    }

}
