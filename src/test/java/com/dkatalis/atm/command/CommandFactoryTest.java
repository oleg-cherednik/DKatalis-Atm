package com.dkatalis.atm.command;

import org.testng.annotations.Test;
import com.dkatalis.atm.command.Command;
import com.dkatalis.atm.command.CommandFactory;
import com.dkatalis.atm.command.DepositCommand;
import com.dkatalis.atm.command.ExitCommand;
import com.dkatalis.atm.command.HelpCommand;
import com.dkatalis.atm.command.LoginCommand;
import com.dkatalis.atm.command.LogoutCommand;
import com.dkatalis.atm.command.TransferCommand;
import com.dkatalis.atm.command.WithdrawCommand;
import com.dkatalis.atm.exceptions.AtmException;
import com.dkatalis.atm.exceptions.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 10.08.2021
 */
@Test
public class CommandFactoryTest {

    public void shouldThrowEmptyInputExceptionWhenCreateCommandWithEmptyName() {
        assertThatThrownBy(() -> CommandFactory.createCommand("oleg"))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.EMPTY_INPUT.name());
    }

    public void shouldRetrieveLoginCommand() {
        Command command = CommandFactory.createCommand(null, LoginCommand.NAME, "oleg");
        assertThat(command).isExactlyInstanceOf(LoginCommand.class);
        assertThat(command.toString()).isEqualTo(LoginCommand.NAME + " oleg");
    }

    public void shouldRetrieveLogoutCommand() {
        Command command = CommandFactory.createCommand("oleg", LogoutCommand.NAME);
        assertThat(command).isExactlyInstanceOf(LogoutCommand.class);
        assertThat(command.toString()).isEqualTo(LogoutCommand.NAME + " oleg");
    }

    public void shouldRetrieveDepositCommand() {
        Command command = CommandFactory.createCommand("oleg", DepositCommand.NAME, "666");
        assertThat(command).isExactlyInstanceOf(DepositCommand.class);
        assertThat(command.toString()).isEqualTo(DepositCommand.NAME + " oleg 666");
    }

    public void shouldRetrieveWithdrawCommand() {
        Command command = CommandFactory.createCommand("oleg", WithdrawCommand.NAME, "666");
        assertThat(command).isExactlyInstanceOf(WithdrawCommand.class);
        assertThat(command.toString()).isEqualTo(WithdrawCommand.NAME + " oleg 666");
    }

    public void shouldRetrieveTransferCommand() {
        Command command = CommandFactory.createCommand("oleg", TransferCommand.NAME, "anna", "666");
        assertThat(command).isExactlyInstanceOf(TransferCommand.class);
        assertThat(command.toString()).isEqualTo(TransferCommand.NAME + " oleg anna 666");
    }

    public void shouldRetrieveHelpCommand() {
        Command command = CommandFactory.createCommand(null, HelpCommand.NAME);
        assertThat(command).isExactlyInstanceOf(HelpCommand.class);
        assertThat(command.toString()).isEqualTo(HelpCommand.NAME);
    }

    public void shouldRetrieveNullWhenExitCommand() {
        Command command = CommandFactory.createCommand(null, ExitCommand.NAME);
        assertThat(command).isNull();
    }

    public void shouldThrowUnknownCommandExceptionWhenUnknownCommandName() {
        assertThatThrownBy(() -> CommandFactory.createCommand("oleg", "unknown_command"))
                .isExactlyInstanceOf(AtmException.class)
                .hasMessage(ErrorCode.UNKNOWN_COMMAND.name());
    }

}
