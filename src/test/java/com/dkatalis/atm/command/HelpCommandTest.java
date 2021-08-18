package com.dkatalis.atm.command;

import com.dkatalis.atm.command.HelpCommand;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.dkatalis.atm.AtmContext;
import com.dkatalis.atm.TestPrintStream;
import com.dkatalis.atm.exceptions.AtmException;
import com.dkatalis.atm.exceptions.ErrorCode;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Oleg Cherednik
 * @since 10.08.2021
 */
@Test
public class HelpCommandTest {

    private HelpCommand command;

    @BeforeTest
    public void createCommand() {
        command = new HelpCommand();
    }

    public void shouldInvokeOutOnContextWhenExecute() throws UnsupportedEncodingException {
        AtmContext context = mock(AtmContext.class);

        try (TestPrintStream out = new TestPrintStream()) {
            when(context.out()).thenReturn(out);

            boolean res = command.execute(context);
            assertThat(res).isFalse();

            String str = out.toString();
            assertThat(str).isEqualTo("login [name] - logs in as this customer and creates the customer if not exist\n" +
                    "logout - logs out of the current customer\n" +
                    "deposit [amount] - deposits this amount to the logged in customer\n" +
                    "withdraw [amount] - withdraws this amount from the logged in customer\n" +
                    "transfer [target] [amount] - transfers this amount from the logged in customer to the target customer\n" +
                    "exit - close the program\n" +
                    "help - print this help\n");

            verify(context, atLeast(1)).out();
            verifyNoMoreInteractions(context);
        }
    }

    public void shouldRetrieveDescriptionWhenToString() {
        String str = command.toString();
        assertThat(str).isEqualTo("help");
    }

    public void shouldThrowIncorrectArgumentsAmountExceptionWhenCommandArgumentsContainsArguments() {
        assertThatThrownBy(() -> HelpCommand.validateCommandArguments("oleg"))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.INCORRECT_ARGUMENT_AMOUNT.name());
    }

    public void shouldNotThrowExceptionWhenCommandArgumentsHasNoArguments() {
        for (String[] commandArguments : Arrays.asList(null, ArrayUtils.EMPTY_STRING_ARRAY))
            assertThatCode(() -> HelpCommand.validateCommandArguments(commandArguments))
                    .doesNotThrowAnyException();
    }

    public void shouldPrintHelp() {
        PrintStream out = mock(PrintStream.class);
        HelpCommand.printHelp(out);
        verify(out, times(1)).format(eq("%s - print this help\n"), eq("help"));
        verifyNoMoreInteractions(out);
    }

}
