package com.dkatalis.atm;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 10.08.2021
 */
public class AtmTest {

    @Test
    public void scenarioTwoUsersOneHasDebtAndTransfersBetween() throws IOException {
        List<String> commands = Arrays.asList(
                "login Alice",
                "deposit 100",
                "logout",
                "login Bob",
                "deposit 80",
                "transfer Alice 50",
                "transfer Alice 100",
                "deposit 30",
                "logout",
                "login Alice",
                "transfer Bob 30",
                "logout",
                "login Bob",
                "deposit 100",
                "withdraw 90",
                "logout",
                "login Alice",
                "withdraw 220",
                "logout",
                "exit");

        List<String> expectedLines = Arrays.asList(
                "Hello, Alice!",
                "Your balance is $0",
                "",
                "Your balance is $100",
                "",
                "Goodbye, Alice!",
                "",
                "Hello, Bob!",
                "Your balance is $0",
                "",
                "Your balance is $80",
                "",
                "Transferred $50 to Alice",
                "Your balance is $30",
                "",
                "Transferred $30 to Alice",
                "Your balance is $0",
                "Owed $70 to Alice",
                "",
                "Transferred $30 to Alice",
                "Your balance is $0",
                "Owed $40 to Alice",
                "",
                "Goodbye, Bob!",
                "",
                "Hello, Alice!",
                "Your balance is $210",
                "Owed $40 from Bob",
                "",
                "Your balance is $210",
                "Owed $10 from Bob",
                "",
                "Goodbye, Alice!",
                "",
                "Hello, Bob!",
                "Your balance is $0",
                "Owed $10 to Alice",
                "",
                "Transferred $10 to Alice",
                "Your balance is $90",
                "",
                "Your balance is $0",
                "",
                "Goodbye, Bob!",
                "",
                "Hello, Alice!",
                "Your balance is $220",
                "",
                "Your balance is $0",
                "",
                "Goodbye, Alice!");

        doTest(commands, expectedLines);
    }

    @Test
    public void scenarioThreeUsersRoundDebtAndOneUserHasDepositAndKeepItWithNoOtherDebts() throws IOException {
        List<String> commands = Arrays.asList(
                "login Alice",
                "logout",
                "login Bob",
                "logout",
                "login John",
                "logout",
                "login Alice",
                "transfer Bob 20",
                "logout",
                "login Bob",
                "transfer John 20",
                "logout",
                "login John",
                "transfer Alice 20",
                "logout",
                "login Alice",
                "deposit 20",
                "logout",
                "login Bob",
                "logout",
                "login John",
                "logout",
                "exit");

        List<String> expectedLines = Arrays.asList(
                "Hello, Alice!",
                "Your balance is $0",
                "",
                "Goodbye, Alice!",
                "",
                "Hello, Bob!",
                "Your balance is $0",
                "",
                "Goodbye, Bob!",
                "",
                "Hello, John!",
                "Your balance is $0",
                "",
                "Goodbye, John!",
                "",
                "Hello, Alice!",
                "Your balance is $0",
                "",
                "Your balance is $0",
                "Owed $20 to Bob",
                "",
                "Goodbye, Alice!",
                "",
                "Hello, Bob!",
                "Your balance is $0",
                "Owed $20 from Alice",
                "",
                "Your balance is $0",
                "Owed $20 to John",
                "Owed $20 from Alice",
                "",
                "Goodbye, Bob!",
                "",
                "Hello, John!",
                "Your balance is $0",
                "Owed $20 from Bob",
                "",
                "Your balance is $0",
                "Owed $20 to Alice",
                "Owed $20 from Bob",
                "",
                "Goodbye, John!",
                "",
                "Hello, Alice!",
                "Your balance is $0",
                "Owed $20 to Bob",
                "Owed $20 from John",
                "",
                "Transferred $20 to Bob",
                "Your balance is $20",
                "",
                "Goodbye, Alice!",
                "",
                "Hello, Bob!",
                "Your balance is $0",
                "",
                "Goodbye, Bob!",
                "",
                "Hello, John!",
                "Your balance is $0",
                "",
                "Goodbye, John!");

        doTest(commands, expectedLines);
    }

    private void doTest(List<String> commands, List<String> expectedLines) throws IOException {
        byte[] buf = String.join(System.lineSeparator(), commands).getBytes(StandardCharsets.UTF_8);

        try (InputStream in = new ByteArrayInputStream(buf); PrintStream out = new TestPrintStream()) {
            Atm atm = new Atm(in, out, out);
            atm.start();
            String[] actualLines = out.toString().trim().split(System.lineSeparator() + "|\n");

            assertThat(actualLines).isNotNull();
            assertThat(actualLines).isNotEmpty();

            int i = shouldContainWelcomeHeader(actualLines);
//            assertThat(actualLines).hasSize(expectedLines.size() + i + 2);

            System.out.println(out);

            for (String expectedLine : expectedLines) {
                String actualLine = actualLines[i].trim();
                int pos = actualLine.indexOf('>');

                if (pos >= 0)
                    actualLine = actualLine.substring(pos + 1).trim();

                assertThat(actualLine).isEqualTo(expectedLine);
                i++;
            }

            assertThat(actualLines[i++].trim()).isEqualTo("");
            assertThat(actualLines[i].trim()).isEqualTo(">");
        }
    }

    private int shouldContainWelcomeHeader(String[] actualLines) {
        int i = 0;
        assertThat(actualLines).hasSizeGreaterThanOrEqualTo(5);
        assertThat(actualLines[i++].trim()).isEqualTo("Welcome to ATM simulation.");
        assertThat(actualLines[i++].trim()).isEqualTo("- command is case insensitive");
        assertThat(actualLines[i++].trim()).isEqualTo("- user login is case insensitive");
        assertThat(actualLines[i++].trim()).isEqualTo("- type 'help' for help notes and");
        assertThat(actualLines[i++].trim()).isEqualTo("");
        return i;
    }

}
