package src.tests.user_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.InvalidGameException;
import src.main.exceptions.UsernameException;
import src.main.users.BuyStandardUser;
import src.main.users.SellStandardUser;

/**
 * A test suite for the BuyStandardUser class.
 */
public class TestBuyStandardUser {
    static BuyStandardUser sykkuno, tenZ, deadlyJimmy;
    static SellStandardUser riot, ninjaKiwi;

    /**
     * Helper function that creates the buyers that will be used in the tests.
     */
    private static void createBuyers() throws UsernameException, BalanceException {
        sykkuno = new BuyStandardUser("Sykkuno", 500);
        tenZ = new BuyStandardUser("TenZ", 342);
        deadlyJimmy = new BuyStandardUser("DeadlyJimmy", 138);
    }

    /**
     * Helper function that creates the buyers which will be used in the tests.
     */
    private static void createSellers() throws UsernameException, InvalidGameException, BalanceException {
        ninjaKiwi = new SellStandardUser("Ninja Kiwi", 400);
        riot = new SellStandardUser("RIOT Games", 100);

        ninjaKiwi.sell("Bloons TD 6", 4.99, 30);
        ninjaKiwi.sell("Bloons TD Battles", 2.99, 0);
        ninjaKiwi.getGameLibrary().get("Bloons TD 6").putOffProbation();
        ninjaKiwi.getGameLibrary().get("Bloons TD Battles").putOffProbation();

        riot.sell("VALORANT", 0.0, 0);
        riot.sell("League of Legends", 0.0, 0);
        riot.getGameLibrary().get("VALORANT").putOffProbation();
        riot.getGameLibrary().get("League of Legends").putOffProbation();
    }

    /**
     * Sets up the buyers and sellers for the test cases.
     */
    @BeforeAll
    public static void setUp() throws UsernameException, InvalidGameException, BalanceException {
        createBuyers();
        createSellers();
    }

    /**
     * Tests that buy standard users can buy games from sellers.
     */
    @Test
    public void testBuy() throws ConstraintException {
        sykkuno.buy("RIOT Games", "VALORANT");
        assertEquals(500, sykkuno.getBalance());
        assertEquals(400, ninjaKiwi.getBalance());

        tenZ.buy("Ninja Kiwi", "Bloons TD 6");
        assertEquals(337.01, tenZ.getBalance());
        assertEquals(404.99, ninjaKiwi.getBalance());
    }

    /**
     * Tests the cases in which a transaction might not be valid; throws exceptions.
     */
    @Test
    public void testBuyThrowsException() throws ConstraintException {
        // Buying a game from a non existent seller
        assertThrows(UsernameException.class, () -> sykkuno.buy("Rockstar Games", "GTA V"));
        // Buying a game from a seller that doesn't carry the game.
        assertThrows(InvalidGameException.class, () -> sykkuno.buy("RIOT Games", "Bloons TD 6"));

        // Buying a game from a non-seller user
        BuyStandardUser tempBuyer = new BuyStandardUser("TempBuyer", 100);
        BuyStandardUser tempBuyer2 = new BuyStandardUser("TempBuyer2", 100);
        tempBuyer.buy("RIOT Games", "VALORANT");
        assertThrows(InvalidGameException.class, () -> tempBuyer2.buy(tempBuyer.getUsername(), "VALORANT"));

        // Buying a game you already own
        try {
            tempBuyer.buy("RIOT Games", "VALORANT");
        } catch (InvalidGameException e) {
            assertEquals(new InvalidGameException(tempBuyer.getUsername() + " already owns the game 'VALORANT'.").getMessage(), e.getMessage());
        }

        // Game put up for sale buy seller the same day
        riot.sell("Teamfight Tactics", 10, 30);
        try {
            assertThrows(InvalidGameException.class, () -> tempBuyer.buy(riot.getUsername(), "Teamfight Tactics"));
            tempBuyer.buy(riot.getUsername(), "Teamfight Tactics");
        } catch (InvalidGameException e) {
            assertEquals(new InvalidGameException("'Teamfight Tactics' is not available to purchase until the following day").getMessage(),
                    e.getMessage());
        }
    }

}
