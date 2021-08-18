package com.dkatalis.atm.command;

import com.dkatalis.atm.command.TransferCommand;
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
public class TransferCommandTest {

    private TransferCommand command;

    @BeforeTest
    public void createCommand() {
        command = new TransferCommand("oleg", "anna", 666);
    }

    public void shouldInvokeTransferOnContextWhenExecute() {
        AtmContext context = mock(AtmContext.class);
        boolean res = command.execute(context);

        assertThat(res).isTrue();
        verify(context, times(1)).transferBetweenUserLogin(eq("oleg"), eq("anna"), eq(666));
        verifyNoMoreInteractions(context);
    }

    public void shouldRetrieveDescriptionWhenToString() {
        String str = command.toString();
        assertThat(str).isEqualTo("transfer oleg anna 666");
    }

    public void shouldThrowIncorrectArgumentsAmountExceptionWhenCommandArgumentsDoesNotHaveExactlyTwoArguments() {
        for (String[] commandArguments : Arrays.asList(null, ArrayUtils.EMPTY_STRING_ARRAY,
                new String[] { "oleg" }, new String[] { "oleg", "anna", "alexey" }))
            assertThatThrownBy(() -> TransferCommand.validateCommandArguments(commandArguments))
                    .isExactlyInstanceOf(AtmException.class)
                    .hasMessage(ErrorCode.INCORRECT_ARGUMENT_AMOUNT.name());
    }

    public void shouldNotThrowExceptionWhenCommandArgumentsHasExactlyTwoArguments() {
        assertThatCode(() -> TransferCommand.validateCommandArguments("oleg", "666"))
                .doesNotThrowAnyException();
    }

    public void shouldThrowIncorrectArgumentContentExceptionWhenTransferAmountIsNotPositive() {
        for (String[] commandArguments : Arrays.asList(new String[] { "oleg", "-1" }, new String[] { "oleg", "0" }))
            assertThatThrownBy(() -> TransferCommand.validateCommandArguments(commandArguments))
                    .isExactlyInstanceOf(AtmException.class)
                    .hasMessage(ErrorCode.INCORRECT_ARGUMENT_CONTENT.name());
    }

    public void shouldThrowIncorrectArgumentContentExceptionWhenUserLoginIsBlank() {
        for (String userLogin : Arrays.asList(null, "", "   "))
            assertThatThrownBy(() -> TransferCommand.validateCommandArguments(userLogin, "666"))
                    .isExactlyInstanceOf(AtmException.class)
                    .hasMessage(ErrorCode.INCORRECT_ARGUMENT_CONTENT.name());
    }

    public void shouldThrowIncorrectArgumentContentExceptionWhenTransferAmountIsNotANumber() {
        assertThatThrownBy(() -> TransferCommand.validateCommandArguments("oleg", "one"))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.INCORRECT_ARGUMENT_CONTENT.name());
    }

    public void shouldRetrieveFirstArgumentAsUserLoginToWhenGetUserLoginTo() {
        assertThatCode(() -> {
            String userLoginTo = TransferCommand.getUserLoginTo("oleg", "666");
            assertThat(userLoginTo).isEqualTo("oleg");
        }).doesNotThrowAnyException();
    }

    public void shouldRetrieveSecondArgumentAsIntegerWhenGetTransferAmount() {
        assertThatCode(() -> {
            int transferAmount = TransferCommand.getTransferAmount("oleg", "666");
            assertThat(transferAmount).isEqualTo(666);
        }).doesNotThrowAnyException();
    }

    public void shouldPrintHelp() {
        PrintStream out = mock(PrintStream.class);
        TransferCommand.printHelp(out);
        verify(out, times(1)).format(eq("%s [target] [amount] - transfers this amount from the logged in" +
                " customer to the target customer\n"), eq("transfer"));
        verifyNoMoreInteractions(out);
    }

}
