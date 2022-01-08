package src.tests.user_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.Game;
import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.InvalidGameException;
import src.main.exceptions.UsernameException;
import src.main.users.FullStandardUser;

import java.util.Map;

/**
 * A test suite for the FullStandardUser class.
 */
public class TestFullStandardUser {
    static FullStandardUser sunny, tin, andrew;

    /**
     * Setting up all full standard users to be used in test cases.
     */
    @BeforeAll
    public static void setUp() throws UsernameException, InvalidGameException, BalanceException {
        sunny = new FullStandardUser("sunmastersunboy", 340);
        sunny.sell("Super Smash Bros", 59.99, 30);
        sunny.sell("Super Mario Sunshine", 16.00, 10);
        sunny.sell("Super Mario Galaxy", 13.28, 5);
        putUsersGameOnSale(sunny);

        tin = new FullStandardUser("Tin", 260);
        tin.sell("Final Fantasy", 12.99, 10);
        tin.sell("Genshin Impact", 0.0, 0);
        putUsersGameOnSale(tin);

        andrew = new FullStandardUser("Andrew", 490);
        andrew.sell("GTA V", 59.99, 50);
        putUsersGameOnSale(andrew);
    }

    /**
     * Helper function to bypass the requirement of only being able to buy a game the following day of its sell date.
     * Puts all the games of the given user on sale.
     * @param fullStandardUser the user that will have their games for sale be buyable.
     */
    private static void putUsersGameOnSale(FullStandardUser fullStandardUser) {
        for (Map.Entry<String, Game> gameEntry: fullStandardUser.getGameLibrary().entrySet()) {
            if (gameEntry.getValue().getSeller().equals(fullStandardUser.getUsername())) {
                gameEntry.getValue().putOffProbation();
            }
        }
    }

    /**
     * Tests the case when a FullStandardUser attempts to buy a game that they are selling.
     */
    @Test
    public void testBuyOwnGame() {
        try {
            assertThrows(InvalidGameException.class, () -> sunny.buy(sunny.getUsername(), "Super Smash Bros"));
            sunny.buy(sunny.getUsername(), "Super Smash Bros");
        } catch (ConstraintException e) {
            assertEquals(new InvalidGameException(sunny.getUsername() + " already owns the game 'Super Smash Bros'.").getMessage(), e.getMessage());
        }

        try {
            assertThrows(InvalidGameException.class, () -> sunny.buy(sunny.getUsername(), "Super Mario Sunshine"));
            sunny.buy(sunny.getUsername(), "Super Mario Sunshine");
        } catch (ConstraintException e) {
            assertEquals(new InvalidGameException(sunny.getUsername() + " already owns the game 'Super Mario Sunshine'.").getMessage(), e.getMessage());
        }
    }

    /**
     * Tests the case where a FullStandardUser attempts to buy a game that they already own.
     */
    @Test
    public void testBuyGameAlreadyOwned() throws ConstraintException {
        sunny.buy(tin.getUsername(), "Genshin Impact");
        try {
            assertThrows(InvalidGameException.class, () -> sunny.buy(tin.getUsername(), "Genshin Impact"));
            sunny.buy(tin.getUsername(), "Genshin Impact");
        } catch (ConstraintException e) {
            assertEquals(new InvalidGameException(sunny.getUsername() + " already owns the game 'Genshin Impact'.").getMessage(), e.getMessage());
        }
    }


}
