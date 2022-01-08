package src.tests.user_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.exceptions.BalanceException;
import src.main.exceptions.InvalidGameException;
import src.main.exceptions.UsernameException;
import src.main.users.SellStandardUser;


/**
 * A test suite for the SellStandardUser class.
 */
public class TestSellStandardUser {
    static SellStandardUser rockstar, riot;

    /**
     * Setup functions that creates all the sellers to be used in several test cases.
     */
    @BeforeAll
    public static void setUp() throws UsernameException, BalanceException {
        rockstar = new SellStandardUser("Rockstar Games", 1032);
        riot = new SellStandardUser("Riot", 5832);
    }

    /**
     * Tests that the sellers are able to put their games up on sale.
     */
    @Test
    public void testSell() throws InvalidGameException {
        rockstar.sell("GTA V", 59.99, 50);
        assertTrue(rockstar.getGameLibrary().containsKey("GTA V"));

        riot.sell("VALORANT", 0.0, 0);
        assertTrue(riot.getGameLibrary().containsKey("VALORANT"));
        assertEquals(riot.getGameLibrary().get("VALORANT").getPrice(), 0.0);;
    }

    /**
     * Tests the case where sellers aren't able to sell a game; they already have the game on sale.
     */
    @Test
    public void testSellThrows() {
        try {
            rockstar.sell("Bully", 20, 40);
            assertThrows(InvalidGameException.class, () -> rockstar.sell("Bully", 20, 40));
            rockstar.sell("Bully", 20, 40);
        } catch (InvalidGameException e) {
            assertEquals(new InvalidGameException(rockstar.getUsername() + " already has 'Bully' in their library").getMessage(), e.getMessage());
        }

    }

}
