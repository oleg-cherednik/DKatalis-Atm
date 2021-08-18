package com.dkatalis.atm;

import java.io.PrintStream;

/**
 * This class is designed to be a part of ATM and be invoked <b>only</b> from command.<br>
 * All command's arguments are validated, hence no need to check input parameters additionally.
 *
 * @author Oleg Cherednik
 * @since 09.08.2021
 */
public interface AtmContext {

    void loginUser(String userLogin);

    void logoutByUserLogin(String userLogin);

    void depositForUserLogin(String userLogin, int amount);

    void withdrawForUserLogin(String userLogin, int amount);

    void transferBetweenUserLogin(String userLoginFrom, String userLoginTo, int amount);

    String getUserNameByLogin(String userLogin);

    PrintStream out();

}
