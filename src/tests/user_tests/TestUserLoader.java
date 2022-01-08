package src.tests.user_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.Game;
import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.InvalidGameException;
import src.main.exceptions.UsernameException;
import src.main.users.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A test suite for the UserLoader class.
 */
public class TestUserLoader {
    static ArrayList<User> temporaryAllUsers = new ArrayList<>();
    static HashMap<String, User> userHashMap = User.getAllUsers();
    static SellStandardUser roadhog, junkrat, widowmaker;
    static BuyStandardUser dva, mercy, lucio, sigma, torb, zen, ashe;
    static AdminUser sombra;
    static FullStandardUser tracer, rein, mcree, winston, bastion;

    /**
     * Sets up all the users to be added to a database and tested in the test cases.
     */
    @BeforeAll
    public static void setUp() throws UsernameException, InvalidGameException, BalanceException {
        createAdmin();
        createFullUsers();
        createBuyers();
        createSellers();
    }

    /**
     * Helper function to create Admin users.
     * @throws UsernameException ignored
     */
    private static void createAdmin() throws UsernameException, BalanceException {
        sombra = new AdminUser("Sombra", 4707);
        temporaryAllUsers.add(sombra);
    }

    /**
     * Helper function that creates buyer users.
     * @throws UsernameException ignored
     */
    private static void createBuyers() throws UsernameException, BalanceException {
        dva = new BuyStandardUser("Hana Song", 6146);
        temporaryAllUsers.add(dva);

        mercy = new BuyStandardUser("Angela Ziegler", 5987);
        temporaryAllUsers.add(mercy);

        lucio = new BuyStandardUser("Lucio", 9187);
        temporaryAllUsers.add(lucio);

        sigma = new BuyStandardUser("Sigma", 9792);
        temporaryAllUsers.add(sigma);

        torb = new BuyStandardUser("Torbjorn", 5592);
        temporaryAllUsers.add(torb);

        zen = new BuyStandardUser("Zenyatta", 246);
        temporaryAllUsers.add(zen);

        ashe = new BuyStandardUser("Ashe", 2441);
        temporaryAllUsers.add(ashe);
    }

    /**
     * Helper function that creates full standard users.
     * @throws UsernameException ignored
     */
    private static void createFullUsers() throws UsernameException, BalanceException {
        tracer = new FullStandardUser("Lena Oxton", 424);
        temporaryAllUsers.add(tracer);

        rein = new FullStandardUser("Reinhardt", 1168);
        temporaryAllUsers.add(rein);

        mcree = new FullStandardUser("Jesse Mcree", 8106);
        temporaryAllUsers.add(mcree);

        bastion = new FullStandardUser("Bastion", 0);
        temporaryAllUsers.add(bastion);

        winston = new FullStandardUser("Winston", 8565);
        temporaryAllUsers.add(winston);
    }

    /**
     * Helper function that creates seller users.
     * @throws UsernameException ignored
     */
    private static void createSellers() throws UsernameException, InvalidGameException, BalanceException {
        roadhog = new SellStandardUser("Roadhog", 5922);
        roadhog.sell("Banzai Rabbit", 93.22, 10);
        roadhog.sell("Fruit Ninja", 33.55, 54);
        temporaryAllUsers.add(roadhog);

        junkrat = new SellStandardUser("Jamison Fawkes", 7609);
        junkrat.sell("Baby T-Rex", 9.99, 5);
        temporaryAllUsers.add(junkrat);

        widowmaker = new SellStandardUser("Amelie Lacroix", 6456);
        widowmaker.sell("Assassins Creed", 59.99, 30);
        widowmaker.sell("Absolver", 39.99, 10);
        widowmaker.sell("Captain Blood", 29.99, 35);
        temporaryAllUsers.add(widowmaker);

    }

    /**
     * Helper function to empty User.allUsers
     */
    private static void emptyAllUsers() {
        userHashMap.clear();
    }

    /**
     * Tests if all the users from the previous session are present in the next session when loaded.
     */
    @Test
    public void testContains() {
        emptyAllUsers();
        assertEquals(0, userHashMap.size());

        UserLoader.loadUsers();
        assertNotEquals(0, userHashMap.size());

        assertEquals(temporaryAllUsers.size(), userHashMap.size());

        for (User user: temporaryAllUsers) {
            assertTrue(userHashMap.containsKey(user.getUsername()));
        }
    }

    /**
     * Tests if all the User objects are the same as the ones deserialized in the next session.
     */
    @Test
    public void testMemoryEquality() {
        for (User user: temporaryAllUsers) {
            assertEquals(user, userHashMap.get(user.getUsername()));
        }
    }

    /**
     * Tests if all games made in a previous session are buyable by other users in the next session
     */
    @Test
    public void testSellableGames() throws ConstraintException {
        startNewDay();

        checkGameSellability();

        dva = (BuyStandardUser) userHashMap.get("Hana Song");
        dva.buy(widowmaker.getUsername(), "Absolver");
        assertFalse(dva.getGame("Absolver").isOffProbation());

        startNewDay();

        checkGameSellability();
    }

    /**
     * Checks that the games of every user is buyable if the user is the seller of that game and checks
     * that the games of every user is not buyable if the user is not the seller of that game.
     */
    private static void checkGameSellability() {
        for (Map.Entry<String, User> userEntry: userHashMap.entrySet()) {
            User user = userEntry.getValue();
            for (Map.Entry<String, Game> gameEntry: user.getGameLibrary().entrySet()) {
                Game game = gameEntry.getValue();
                assertTrue(game.isOffProbation());
            }
        }
    }

    /**
     * Tests the saving of users across several sequential sessions.
     */
    @Test
    public void testMultipleSessions() throws ConstraintException {
        startNewDay();

        // Session 1--------------------------------------------------------------
        widowmaker = (SellStandardUser) userHashMap.get("Amelie Lacroix");
        Game absolver = widowmaker.getGame("Absolver");

        rein = (FullStandardUser) userHashMap.get("Reinhardt");
        rein.buy(widowmaker.getUsername(), absolver.getName());

        startNewDay();

        // Session 2--------------------------------------------------------------
        widowmaker = (SellStandardUser) userHashMap.get("Amelie Lacroix");
        absolver = widowmaker.getGame("Absolver");
        rein = (FullStandardUser) userHashMap.get("Reinhardt");

        assertTrue(rein.getGameLibrary().containsKey(absolver.getName()));
        assertNotEquals(rein.getGame(absolver.getName()), absolver);
        assertEquals(1128.01, rein.getBalance());
        assertEquals(6495.99, widowmaker.getBalance());

        sombra = (AdminUser) userHashMap.get("Sombra");
        sombra.addCredit(1000);
        sombra.addCredit("Bastion", 1000);

        startNewDay();

        // Session 3--------------------------------------------------------------
        sombra = (AdminUser) userHashMap.get("Sombra");
        assertEquals(5707, sombra.getBalance());
        assertEquals(1000, userHashMap.get("Bastion").getBalance());
        sombra.auctionSale();

        // Auction sale goes on for multiple days
        for (int i = 1; i <= 10; i++) {
            startNewDay();
            assertTrue(AdminUser.isAuction());
        }
        sombra = (AdminUser) userHashMap.get("Sombra");
        sombra.auctionSale();

        startNewDay();

        // Session 4--------------------------------------------------------------
        assertFalse(AdminUser.isAuction());
        double absolverPreviousDay = userHashMap.get(widowmaker.getUsername()).getGame(absolver.getName()).getPrice();
        double assassinsCreedPreviousDay = userHashMap.get(widowmaker.getUsername()).getGame("Assassins Creed").getPrice();
        double capBloodPreviousDay = userHashMap.get(widowmaker.getUsername()).getGame("Captain Blood").getPrice();
        double babyTRexPreviousDay = userHashMap.get(junkrat.getUsername()).getGame("Baby T-Rex").getPrice();
        double banzaiPreviousDay = userHashMap.get(roadhog.getUsername()).getGame("Banzai Rabbit").getPrice();
        double fruitNinjaPreviousDay = userHashMap.get(roadhog.getUsername()).getGame("Fruit Ninja").getPrice();
        sombra.auctionSale();

        startNewDay();

        // Session 5--------------------------------------------------------------
        assertNotEquals(absolverPreviousDay, userHashMap.get(widowmaker.getUsername()).getGame(absolver.getName()).getPrice());
        assertNotEquals(assassinsCreedPreviousDay, userHashMap.get(widowmaker.getUsername()).getGame("Assassins Creed").getPrice());
        assertNotEquals(capBloodPreviousDay, userHashMap.get(widowmaker.getUsername()).getGame("Captain Blood").getPrice());
        assertNotEquals(babyTRexPreviousDay, userHashMap.get(junkrat.getUsername()).getGame("Baby T-Rex").getPrice());
        assertNotEquals(babyTRexPreviousDay, userHashMap.get(roadhog.getUsername()).getGame("Banzai Rabbit").getPrice());
        assertNotEquals(banzaiPreviousDay, userHashMap.get(roadhog.getUsername()).getGame("Banzai Rabbit").getPrice());
        assertNotEquals(fruitNinjaPreviousDay, userHashMap.get(roadhog.getUsername()).getGame("Fruit Ninja").getPrice());
    }

    /**
     * Starts a new day/session of this backend.
     */
    private static void startNewDay() {
        emptyAllUsers();
        UserLoader.loadUsers();
        synchronizeUsers();
    }

    /**
     * Puts all the users in userHashmap into temporaryAllUsers.
     */
    private static void synchronizeUsers() {
        temporaryAllUsers.clear();
        for (Map.Entry<String, User> entry: userHashMap.entrySet()) {
            temporaryAllUsers.add(entry.getValue());
        }
    }
}
