package com.dkatalis.atm.exceptions;

import com.dkatalis.atm.AtmContext;

/**
 * @author Oleg Cherednik
 * @since 09.08.2021
 */
public class AtmException extends RuntimeException {

    private final ErrorCode code;
    private final Object[] args;

    public AtmException(ErrorCode code, Object... args) {
        super(code.name());
        this.code = code;
        this.args = args;
    }

    public String getMessage(AtmContext context) {
        return code.getMessage(context, args);
    }

}
