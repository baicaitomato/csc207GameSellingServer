package src.tests.command_tests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.command.Command;
import src.main.command.Invoker;
import src.main.command.LoginCommand;
import src.main.exceptions.ConstraintException;
import src.main.system.DistributionSystem;
import src.main.users.User;
import src.main.users.UserFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A Test suite for the Invoker class
 */
public class TestInvoker {

    Invoker button;
    DistributionSystem system;
    UserFactory factory;

    /**
     * set up different users for the test cases
     */
    @BeforeEach
    public void setup() {
        User.getAllUsers().clear();
        system = new DistributionSystem();
        button = new Invoker();
        factory = new UserFactory();
    }

    /**
     * Test case for Command.Invoker
     * @throws ConstraintException if the command is invalid
     */
    @Test
    public void testInvoker() throws ConstraintException {

        factory.makeUser("Rockstar Games", "SS", 1032.00);
        Command c = new LoginCommand(system, "Rockstar Games", "SS", "1032.00");
        button.setCommand(c);
        button.run();
        assertTrue(system.isCurrLogin());

    }
}
