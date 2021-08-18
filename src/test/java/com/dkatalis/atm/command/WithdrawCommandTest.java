package com.dkatalis.atm.command;

import com.dkatalis.atm.command.WithdrawCommand;
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
public class WithdrawCommandTest {

    private WithdrawCommand command;

    @BeforeTest
    public void createCommand() {
        command = new WithdrawCommand("oleg", 666);
    }

    public void shouldInvokeWithdrawOnContextWhenExecute() {
        AtmContext context = mock(AtmContext.class);
        boolean res = command.execute(context);

        assertThat(res).isTrue();
        verify(context, times(1)).withdrawForUserLogin(eq("oleg"), eq(666));
        verifyNoMoreInteractions(context);
    }

    public void shouldRetrieveDescriptionWhenToString() {
        String str = command.toString();
        assertThat(str).isEqualTo("withdraw oleg 666");
    }

    public void shouldThrowIncorrectArgumentsAmountExceptionWhenCommandArgumentsDoesNotHaveExactlyOneArgument() {
        for (String[] commandArguments : Arrays.asList(null, ArrayUtils.EMPTY_STRING_ARRAY, new String[] { "1", "2" }))
            assertThatThrownBy(() -> WithdrawCommand.validateCommandArguments(commandArguments))
                    .isExactlyInstanceOf(AtmException.class)
                    .hasMessage(ErrorCode.INCORRECT_ARGUMENT_AMOUNT.name());
    }

    public void shouldNotThrowExceptionWhenCommandArgumentsHasExactlyOnePositiveNumber() {
        assertThatCode(() -> WithdrawCommand.validateCommandArguments("1"))
                .doesNotThrowAnyException();
    }

    public void shouldThrowIncorrectArgumentContentExceptionWhenWithdrawAmountIsNotPositive() {
        for (String[] commandArguments : Arrays.asList(new String[] { "-1" }, new String[] { "0" }))
            assertThatThrownBy(() -> WithdrawCommand.validateCommandArguments(commandArguments))
                    .isExactlyInstanceOf(AtmException.class)
                    .hasMessage(ErrorCode.INCORRECT_ARGUMENT_CONTENT.name());
    }

    public void shouldThrowIncorrectArgumentContentExceptionWhenWithdrawAmountIsNotANumber() {
        assertThatThrownBy(() -> WithdrawCommand.validateCommandArguments("one"))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.INCORRECT_ARGUMENT_CONTENT.name());
    }

    public void shouldRetrieveFirstArgumentAsIntegerWhenGetWithdrawAmount() {
        assertThatCode(() -> {
            int withdrawAmount = WithdrawCommand.getWithdrawAmount("666");
            assertThat(withdrawAmount).isEqualTo(666);
        }).doesNotThrowAnyException();
    }

    public void shouldPrintHelp() {
        PrintStream out = mock(PrintStream.class);
        WithdrawCommand.printHelp(out);
        verify(out, times(1)).format(eq("%s [amount] - withdraws this amount from the logged in customer\n"),
                eq("withdraw"));
        verifyNoMoreInteractions(out);
    }

}
