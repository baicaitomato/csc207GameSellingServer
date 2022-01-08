package src.tests.user_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import src.main.exceptions.*;
import src.main.users.FullStandardUser;
import src.main.users.SellStandardUser;
import src.main.users.User;
import src.main.Game;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test suite for the abstract User class.
 */
public class TestUser {
    static UserForTest u1, u2, u3;
    static FullStandardUser bob;
    static SellStandardUser ea;
    static Game game;

    /**
     * A setup method that sets up all the users to be used in the test cases.
     */
    @BeforeAll
    static void setUp() throws UsernameException, InvalidGameException, BalanceException {
        u1 = new UserForTest("Tempest", "Basic", 0.0);
        u2 = new UserForTest("Sunmastersunboy", "Basic", 0.0);
        u3 = new UserForTest("Terminan", "Basic", 0.0);

        bob = new FullStandardUser("Bob", 100);
        ea = new SellStandardUser("EA", 100);
        game = new Game("FIFA", "EA", 80.0, 0.50);
    }

    /**
     * Tests that the users should be able to add credits to their account following the daily deposit limit.
     */
    @Test
    public void testAddCredit() throws DailyCreditLimitException {
        try {
            u1.addCredit(1000.01);
        } catch (DailyCreditLimitException ignored) {}
        assertEquals(0.0, u1.getBalance());

        u1.addCredit(200);
        assertEquals(200.0, u1.getBalance());

        try {
            u1.addCredit(810);
        } catch (DailyCreditLimitException ignore) {}
        assertEquals(200.0, u1.getBalance());

        u1.addCredit(800);
        assertEquals(1000.0, u1.getBalance());

        try {
            u1.addCredit(900);
        } catch (DailyCreditLimitException ignore) {}
        assertEquals(1000.0, u1.getBalance());
    }

    /**
     * Tests that creating users with invalid usernames should throw exceptions.
     */
    @Test
    public void testThrowsException()  {
        assertThrows(UsernameException.class, () -> new UserForTest("123456789ABCDEFG", "Basic", 0));

        try {
            UserForTest tempUser = new UserForTest("TakenUsername", "Basic", 148);
        } catch (UsernameException | BalanceException e) {
            e.printStackTrace();
        }

        assertThrows(UsernameException.class, () -> new UserForTest("TakenUsername", "Basic", 0));
    }

    /**
     * Tests removeGame based on the following properties:
     * - Game's probation
     * - game existence
     * - game ownership
     */
    @Test
    public void testRemoveGameSuccessfulRemoval() throws ConstraintException {
        ea.sell("FIFA", 80.0, 0.50);
        ea.getGame("FIFA").putOffProbation();
        bob.buy("EA", "FIFA");

        // Cannot remove game bought in the same day.
        assertThrows(InvalidGameException.class, () -> bob.removeGame("FIFA"));

        bob.getGame("FIFA").putOffProbation();
        bob.removeGame("FIFA");

        assertEquals(0, bob.getGameLibrary().size());

        // removing a game that doesnt exist in library
        assertThrows(InvalidGameException.class, () -> bob.removeGame("FIFA"));

        // game does not exist
        assertThrows(InvalidGameException.class, () -> bob.removeGame("Minecraft"));
    }

    /**
     * Tests gift based on the following properties:
     * - Game's probation
     * - User existence
     * - Game existence
     * - Game ownership
     * - Recipient's type
     * - re-gifting
     */
    @Test
    public void testGift() throws ConstraintException {

        SellStandardUser bethesda = new SellStandardUser("Bethesda", 100);
        Game doom = new Game("DOOM", "Bethesda", 80.0, 0.5);
        Game tesv = new Game("Skyrim", "Bethesda", 60.0, 0.8);
        FullStandardUser gifter = new FullStandardUser("Daniel", 900);

        bethesda.sell("DOOM", 80, 0.5);
        bethesda.getGame("DOOM").putOffProbation();
        gifter.buy("Bethesda", "DOOM");
        gifter.getGame("DOOM").putOffProbation();

        bethesda.sell("Skyrim", 60, 0.8);
        bethesda.getGame("Skyrim").putOffProbation();
        gifter.buy("Bethesda", "Skyrim");

        // It has not been 24 hours of purchase
        FullStandardUser kyle = new FullStandardUser("Kyle", 100);
        assertThrows(InvalidGameException.class, () -> gifter.gift("Kyle", "Skyrim"));

        // Recipient does not exist in database
        assertThrows(UsernameException.class, () -> gifter.gift("Bezos", "DOOM"));

        // Game does not exist in gifter's inventory
        FullStandardUser musk = new FullStandardUser("Elon", 100);
        assertThrows(InvalidGameException.class, () -> gifter.gift("Elon", "Minecraft"));

        // Recipient already has game in their inventory
        musk.buy("Bethesda", "DOOM");
        assertThrows(InvalidGameException.class, () -> gifter.gift("Elon", "DOOM"));

        // Recipient is a sell standard user
        SellStandardUser rockstar = new SellStandardUser("Rockstar", 100);
        assertThrows(UserAccessException.class, () -> gifter.gift("Rockstar", "DOOM"));

        // All conditions are meet game is 'gifted' successfully

        // Sender had to purchase the game
        gifter.getGame("Skyrim").putOffProbation();
        gifter.gift("Elon", "Skyrim");
        assertTrue(musk.ownsGame("Skyrim"));

        // Game is re-gifted
        musk.getGame("Skyrim").putOffProbation();
        musk.gift("Kyle", "Skyrim");
        assertTrue(kyle.ownsGame("Skyrim"));
        assertFalse(musk.ownsGame("Skyrim"));

        // Sender is the seller of the game
        bethesda.gift("Elon", "Skyrim");
        assertTrue(musk.ownsGame("Skyrim"));
        assertTrue(bethesda.ownsGame("Skyrim"));
    }

}

/**
 * A concrete user that only has the capabilities of User to be used in the test suite
 */
class UserForTest extends User {

    public UserForTest(String username, String accountType, double balance) throws UsernameException, BalanceException {
        super(username, balance, accountType);
    }
}