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
 * A test suite for the AuctionSaleCommand class.
 */
public class TestAuctionSaleCommand {

    UserFactory factory;
    SellCommand sell;
    AuctionsaleCommand auction_sale;
    DistributionSystem system;

    /**
     * set up different users for use in the test cases
     * @throws UsernameException
     * @throws BalanceException
     */
    @BeforeEach
    public void setup() throws UsernameException, BalanceException {

        factory = new UserFactory();

        factory.makeUser("God", "AA", 9999.00);

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
     * Test case for Command.AuctionSaleCommand
     */
    @Test
    public void testAuctionSaleCommand() throws ConstraintException {

        system = new DistributionSystem();

        system.login("God", "AA", 9999.00);

        auction_sale = new AuctionsaleCommand(system, "God");
        auction_sale.execute();

        system.logout();

        system.login("Blizzard", "SS", 1000.00);

        sell = new SellCommand(system, "Overwatch", "Blizzard", "50", "29.99");
        sell.execute();

        system.getCurrUser().getGameLibrary().get("Overwatch").putOffProbation();


        assertEquals(14.995, system.getCurrUser().getGameLibrary().get("Overwatch").getPrice());

    }
}
