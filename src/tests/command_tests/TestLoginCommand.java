package src.tests.command_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.command.Command;
import src.main.command.Invoker;
import src.main.command.LoginCommand;
import src.main.exceptions.*;
import src.main.system.DistributionSystem;
import src.main.users.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test suite for the LoginCommand class in the commands package.
 */
public class TestLoginCommand {
    static DistributionSystem system;
    static BuyStandardUser sykkuno;
    static SellStandardUser rockstar;
    static Invoker button;

    /**
     * Sets up the necessary variables to be used in the test cases.
     */
    @BeforeEach
    public void setUp() throws UsernameException, BalanceException {
        User.getAllUsers().clear();
        system = new DistributionSystem();
        rockstar = new SellStandardUser("Rockstar Games", 1032);
        sykkuno = new BuyStandardUser("Sykkuno", 500);
        button = new Invoker();
    }

    /**
     * Testing if Login Command works.
     */
    @Test
    public void testLoginCommand() throws ConstraintException {
        Command c = new LoginCommand(system, "Rockstar Games", "SS", "1032.00");
        button.setCommand(c);
        button.run();
        assertTrue(system.isCurrLogin());
        assertEquals(system.getCurrUser(), rockstar);
    }
}
