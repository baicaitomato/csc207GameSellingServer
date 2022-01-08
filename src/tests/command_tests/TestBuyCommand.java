package src.tests.command_tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.command.*;
import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.UsernameException;
import src.main.system.DistributionSystem;
import src.main.users.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A test suite for the BuyCommand class.
 */
public class TestBuyCommand {

    UserFactory factory;
    SellCommand sell;
    BuyCommand buy;
    DistributionSystem system;

    /**
     * set up different users for use in the test cases
     * @throws UsernameException
     * @throws BalanceException
     */
    @BeforeEach
    public void setup() throws UsernameException, BalanceException {

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
     * Test case for Command.BuyCommand
     */
    @Test
    public void testBuyCommand() throws ConstraintException {

        system = new DistributionSystem();


        system.login("Blizzard", "SS", 1000.00);

        sell = new SellCommand(system, "Overwatch", "Blizzard", "50", "29.99");
        sell.execute();

        assertTrue(system.getCurrUser().getGameLibrary().containsKey("Overwatch"));

        system.getCurrUser().getGameLibrary().get("Overwatch").putOffProbation();

        system.logout();


        system.login("DavidTheStrongA", "BS", 500.00);
        buy = new BuyCommand(system, "Overwatch", "Blizzard", "DavidTheStrongA");
        buy.execute();

        assertTrue(system.getCurrUser().getGameLibrary().containsKey("Overwatch"));
        assertEquals(470.01, system.getCurrUser().getBalance());


    }
}
