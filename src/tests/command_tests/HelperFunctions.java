package src.tests.command_tests;

import src.main.users.User;
import src.main.users.UserLoader;

/**
 * A collection of Helper Functions that can be used in the command package test suite.
 */
public class HelperFunctions {

    /**
     * Starts a new day/session of this backend.
     */
    public static void startNewDay() {
        User.getAllUsers().clear();
        UserLoader.loadUsers();
    }

}
