package com.dkatalis.atm;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author Oleg Cherednik
 * @since 10.08.2021
 */
public final class TestPrintStream extends PrintStream {

    private final ByteArrayOutputStream os;

    public TestPrintStream() throws UnsupportedEncodingException {
        super(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8.name());
        os = (ByteArrayOutputStream)out;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        try {
            return os.toString(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
