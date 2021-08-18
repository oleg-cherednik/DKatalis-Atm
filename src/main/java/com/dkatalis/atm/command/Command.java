package com.dkatalis.atm.command;

import com.dkatalis.atm.AtmContext;

/**
 * @author Oleg Cherednik
 * @since 09.08.2021
 */
public interface Command {

    boolean execute(AtmContext context);

}
