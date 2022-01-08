package src.tests.command_tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.command.SellCommand;
import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.UserAccessException;
import src.main.exceptions.UsernameException;
import src.main.system.DistributionSystem;
import src.main.users.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test suite for the SellCommand class.
 */
public class TestSellCommand {

    UserFactory factory;
    SellCommand sell;
    DistributionSystem system;

    /**
     * set up different users for use in the test cases
     * @throws UsernameException
     * @throws BalanceException
     */
    @BeforeEach
    public void setup() throws UsernameException, BalanceException {
        User.getAllUsers().clear();
        factory = new UserFactory();

        factory.makeUser("Blizzard", "SS", 1000.00);
        factory.makeUser("DavidTheStrongA", "BS", 500.00);

    }

    /**
     * called after each test to start a new day
     */
    @AfterEach
    public void teardown(){
        HelperFunctions.startNewDay();
    }


    /**
     * Test case for Command.SellCommand with Seller
     */
    @Test
    public void testSellCommand_valid() throws ConstraintException {

        system = new DistributionSystem();

        system.login("Blizzard", "SS", 1000.00);

        sell = new SellCommand(system, "Overwatch", "Blizzard", "50", "29.99");
        sell.execute();

        assertTrue(system.getCurrUser().getGameLibrary().containsKey("Overwatch"));

    }

    /**
     * Test cases for Command.SellCommand with Buyer (will fail)
     */
    @Test
    public void testSellCommand_invalid() throws UsernameException {

        system = new DistributionSystem();

        system.login("DavidTheStrongA", "BS", 500.00);

        assertThrows(UserAccessException.class, () -> {new SellCommand(system, "Overwatch", "DavidTheStrongA", "50", "29.99").execute();});

    }
}
