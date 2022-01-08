package src.tests.command_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.command.Command;
import src.main.command.Invoker;
import src.main.command.LogoutCommand;
import src.main.exceptions.ConstraintException;
import src.main.system.DistributionSystem;
import src.main.users.SellStandardUser;
import src.main.users.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test suite for the LogoutCommand in the command package.
 */
public class TestLogoutCommand {
    static DistributionSystem system;
    static SellStandardUser rockstar;
    static Invoker button;

    /**
     * Setting up the necessary variables to be used in the test cases.
     */
    @BeforeEach
    public void setUp() throws ConstraintException {
        User.getAllUsers().clear();
        system = new DistributionSystem();
        rockstar = new SellStandardUser("Rockstar Games", 1032);
        button = new Invoker();
    }

    /**
     * Testing if Logout Command works.
     */
    @Test
    public void testLogoutCommandRightFormat() throws ConstraintException {
        system.login("Rockstar Games", "SS", 1032);
        Command c = new LogoutCommand(system);
        button.setCommand(c);
        button.run();
        assertFalse(system.isCurrLogin());
        assertNull(system.getCurrUser());
    }
}
