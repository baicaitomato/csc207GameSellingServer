package src.tests.user_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.exceptions.*;
import src.main.users.*;

import java.util.HashMap;

/**
 * A test suite for the AdminUser class.
 */
public class TestAdminUser {
    static AdminUser gaben;
    static User u1, u2, u3, u4;

    /**
     * Setting up all the different types of users for use in test cases.
     * @throws UsernameException Not possible to be thrown here because the usernames assigned all follow the requirements.
     */
    @BeforeAll
    public static void setUp() throws UsernameException, BalanceException {
        gaben = new AdminUser("Gaben", User.MAXIMUM_CREDITS);

        u1 = new FullStandardUser("FullUser", 0);
        u2 = new SellStandardUser("SellUser", 0);
        u3 = new BuyStandardUser("BuyUser", 0);
        u4 = new AdminUser("RandomAdmin", 0);

    }

    /**
     * Testing if the attributes of the Admin have been assigned correctly.
     */
    @Test
    public void testAttributes() {
        assertEquals("Gaben", gaben.getUsername());
        assertEquals(User.MAXIMUM_CREDITS, gaben.getBalance());
        assertEquals(User.ADMIN_TYPE, gaben.getAccountType());
        assertEquals(0, gaben.getGameLibrary().size());
    }

    /**
     * Testing if the AdminUser.delete commands works intended by removing the user from the
     * hashmap of all users in User.
     */
    @Test
    public void testDeleteUser() {
        HashMap<String, User> allUsers = User.getAllUsers();
        assertTrue(allUsers.containsKey(u1.getUsername()));
        assertTrue(allUsers.containsKey(u2.getUsername()));
        assertTrue(allUsers.containsKey(u3.getUsername()));
        assertTrue(allUsers.containsKey(u4.getUsername()));

        try {
            gaben.delete(u1.getUsername());
            assertFalse(allUsers.containsKey(u1.getUsername()));
            gaben.delete(u1.getUsername());
        } catch (ConstraintException e) {
            assertEquals(new UsernameException("User '" + u1.getUsername() + "' does not exist").getMessage(), e.getMessage());
        }

        try {
            gaben.delete(u2.getUsername());
            assertFalse(allUsers.containsKey(u2.getUsername()));
            gaben.delete(u2.getUsername());
        } catch (ConstraintException e) {
            assertEquals(new UsernameException("User '" + u2.getUsername() + "' does not exist").getMessage(), e.getMessage());
        }
    }

    /**
     * Testing that the AdminUser cannot delete their own account.
     */
    @Test
    public void testDeleteSelf() {
        try {
            assertThrows(UsernameException.class, () -> gaben.delete(gaben.getUsername()));
            assertTrue(User.getAllUsers().containsKey(gaben.getUsername()));
            gaben.delete(gaben.getUsername());
        } catch (UsernameException e) {
            assertEquals(new UsernameException("Cannot delete your account").getMessage(), e.getMessage());
        }
    }

    /**
     * Tests that the Admin is able to create new users.
     * @throws UsernameException not possible to be thrown here; all usernames follow the requirements.
     */
    @Test
    public void testCreate() throws UsernameException, BalanceException {
        // Creating buyer type
        gaben.create("Dominique", User.BUYER_TYPE, 250);
        assertTrue(User.getAllUsers().containsKey("Dominique"));
        assertThrows(UsernameException.class, () -> gaben.create("Dominique", User.BUYER_TYPE, 120));

        // Creating seller type
        gaben.create("Rockstar Games", User.SELLER_TYPE, 1000);
        assertTrue(User.getAllUsers().containsKey("Rockstar Games"));
        assertThrows(UsernameException.class, () -> gaben.create("Rockstar Games", User.BUYER_TYPE, 120));

        // Creating full standard user
        gaben.create("Raymond", User.FULL_STANDARD_TYPE, 100);
        assertTrue(User.getAllUsers().containsKey("Raymond"));
        assertThrows(UsernameException.class, () -> gaben.create("Raymond", User.FULL_STANDARD_TYPE, 100));

        // Creating admin type
        gaben.create("Bill Gates", User.ADMIN_TYPE, User.MAXIMUM_CREDITS);
        assertTrue(User.getAllUsers().containsKey("Bill Gates"));
        assertThrows(UsernameException.class, () -> gaben.create("Bill Gates", User.ADMIN_TYPE, 150));
    }

    /**
     * Tests if the admin can invoke an auctionSale by calling AdminUser.auctionSale.
     * @throws InvalidGameException ignore because all games will be valid here.
     * @throws UsernameException ignore because all usernames are valid.
     */
    @Test
    public void testAuctionSale() throws InvalidGameException, UsernameException, BalanceException {
        SellStandardUser seller = new SellStandardUser("Blizzard", 3500);
        seller.sell("Overwatch", 29.99, 30);
        seller.sell("Overwatch 2", 49.99, 0);

        // Previously made games go up on auctionSale
        assertFalse(AdminUser.isAuction());
        gaben.auctionSale();
        assertTrue(AdminUser.isAuction());
        assertEquals(20.993, seller.getGameLibrary().get("Overwatch").getPrice());
        assertEquals(49.99, seller.getGameLibrary().get("Overwatch 2").getPrice());

        // Newly made games during auctionSale should be on auctionsale too.
        seller.sell("Diablo III", 19.99, 10);
        assertEquals(17.991, seller.getGameLibrary().get("Diablo III").getPrice());
    }

    /**
     * Tests that an admin is able to add credits to their own account and other users' accounts.
     * @throws UsernameException ignore because all usernames will be valid and/or the exception will be caught.
     */
    @Test
    public void testAddCredit() throws UsernameException, DailyCreditLimitException {
        try {
            gaben.addCredit("Does Not Exist", 300);
        } catch (UsernameException | DailyCreditLimitException e) {
            assertEquals(new UsernameException("User 'Does Not Exist' does not exist" ).getMessage(), e.getMessage());
        }

        gaben.addCredit(u3.getUsername(), 30);
        assertEquals(30, u3.getBalance());

        try{
            gaben.addCredit(u3.getUsername(), 990);
            assertEquals(30, u3.getBalance());

            gaben.addCredit(u3.getUsername(), 1001);
            assertEquals(30, u3.getBalance());
        } catch (DailyCreditLimitException e) {
            assertEquals(new DailyCreditLimitException(
                    "Warning: You cannot deposit more than " + User.DAILY_DEPOSIT_LIMIT + " credits in a day.").getMessage(), e.getMessage());
        }
    }

    /**
     * Test RemoveGame based on these following properties
     * - Game & user existence
     * - Game's probation
     */
    @Test
    public void testRemoveGame() throws ConstraintException {

        SellStandardUser sellStandardUser = new SellStandardUser("SellerMan", 100);
        sellStandardUser.sell("Terrible Game", 100, 80);

        // Can not remove game until the day passes
        assertThrows(InvalidGameException.class, () -> gaben.removeGame(sellStandardUser.getUsername(), "Terrible Game"));

        // Day passes, can remove game
        sellStandardUser.getGameLibrary().get("Terrible Game").putOffProbation();

        gaben.removeGame(sellStandardUser.getUsername(), "Terrible Game");
        assertEquals(0, sellStandardUser.getGameLibrary().size());

        // User does not exist, Game does not exist
        assertThrows(UsernameException.class, () -> ((AdminUser)u4).removeGame("Cindy", "Minecraft"));

        FullStandardUser cindy = new FullStandardUser("Cindy", 80);

        // User exists, Game does not exist
        assertThrows(InvalidGameException.class, () -> ((AdminUser)u4).removeGame("Cindy", "Minecraft"));

        SellStandardUser mojang = new SellStandardUser("Mojang", 80);

        // User exist, Game exists, User does not own game.
        assertThrows(InvalidGameException.class, () -> ((AdminUser)u4).removeGame("Cindy", "Minecraft"));

        // User exist, Game exists, User owns the game. Sold and bought game successfully
        mojang.sell("Minecraft", 30, 0.3);
        mojang.getGameLibrary().get("Minecraft").putOffProbation();
        cindy.buy("Mojang", "Minecraft");
        cindy.getGameLibrary().get("Minecraft").putOffProbation();

        ((AdminUser)u4).removeGame("Cindy", "Minecraft");
        assertEquals(0, cindy.getGameLibrary().size());

        // Game exists, User does not exist
        assertThrows(UsernameException.class, () -> ((AdminUser)u4).removeGame("Tim", "Minecraft"));
    }

    /**
     * Test Refund based on whether the users existence.
     */
    @Test
    public void testRefundUserExistence() throws ConstraintException {
        new SellStandardUser("EA", 80);
        BuyStandardUser andrew = new BuyStandardUser("Andrew", 0);

        // when both users don't exist
        assertThrows(UsernameException.class,() -> ((AdminUser)u4).refund("Bob", "Jeff", 80));

        // when buyer does not exist
        assertThrows(UsernameException.class,() -> ((AdminUser)u4).refund("Bob", "EA", 80));

        // when seller does not exist
        assertThrows(UsernameException.class,() -> ((AdminUser)u4).refund("Bob", "EA", 80));

        // when both user exists and other conditions are satisfied.
        ((AdminUser)u4).refund("Andrew", "EA", 80);
        assertEquals(80, andrew.getBalance());
    }

    /**
     * Test Refund based on the the user's type.
     */
    @Test
    public void testRefundUserType() throws ConstraintException {
        SellStandardUser steam = new SellStandardUser("Steam", 80);
        SellStandardUser epicGames = new SellStandardUser("Epic Games", 800);
        BuyStandardUser bob = new BuyStandardUser("Bob", 0);
        BuyStandardUser sam = new BuyStandardUser("Sam", 0);
        FullStandardUser god = new FullStandardUser("God", 100);


        // both user types don't make sense
        assertThrows(UserAccessException.class, () -> ((AdminUser)u4).refund("Steam", "Sam", 80));

        // seller type does not make sense
        assertThrows(UserAccessException.class, () -> ((AdminUser)u4).refund("Sam", "Bob", 80));

        // buyer type does not make sense
        assertThrows(UserAccessException.class, () -> ((AdminUser)u4).refund("Epic Games", "Steam", 80));

        // buyer is the same as seller
        assertThrows(UserAccessException.class, () -> ((AdminUser)u4).refund("God", "God", 80));

        // both user make sense
        ((AdminUser)u4).refund("Sam", "Steam", 80);
        assertEquals(80, sam.getBalance());
    }

    /**
     * Test Refund based on the seller's balance
     */
    @Test
    public void testRefundSellerBalance() throws ConstraintException {
        SellStandardUser originGames = new SellStandardUser("Origin Games", 0);
        BuyStandardUser james = new BuyStandardUser("James", 0);

        // When seller does not have enough balance
        assertThrows(BalanceException.class, () -> ((AdminUser)u4).refund("James", "Origin Games", 80));

        // When seller does have enough balance
        originGames.addCredit(80);
        ((AdminUser)u4).refund("James", "Origin Games", 80);
        assertEquals(80, james.getBalance());
    }

    /**
     * Test gift based on the game ownership.
     */
    @Test
    public void testGift() throws ConstraintException {
        FullStandardUser jamison = new FullStandardUser("Jamison", 500);
        FullStandardUser michael = new FullStandardUser("Michael", 100);

        SellStandardUser taleworlds = new SellStandardUser("TaleWorlds", 900);
        taleworlds.sell("Mount & Blade", 30, 0.9);
        taleworlds.getGameLibrary().get("Mount & Blade").putOffProbation();

        // Sender does not own the game, a seller has the game for sale
        gaben.gift("Jamison", "Michael", "Mount & Blade");
        assertFalse(jamison.ownsGame("Mount & Blade"));
        assertTrue(michael.ownsGame("Mount & Blade"));
        assertTrue(taleworlds.ownsGame("Mount & Blade"));

        // Sender owns the game
        michael.removeGame("Mount & Blade");
        jamison.buy("TaleWorlds", "Mount & Blade");
        jamison.getGameLibrary().get("Mount & Blade").putOffProbation();

        gaben.gift("Jamison", "Michael", "Mount & Blade");
        assertFalse(jamison.ownsGame("Mount & Blade"));
        assertTrue(michael.ownsGame("Mount & Blade"));

        // Sender does not own the game, a non seller has the game in their inventory
        // removes game to reset test
        michael.getGameLibrary().get("Mount & Blade").putOffProbation();
        michael.removeGame("Mount & Blade");

        FullStandardUser domi = new FullStandardUser("Domi", 50);
        domi.buy("TaleWorlds", "Mount & Blade");
        domi.getGameLibrary().get("Mount & Blade").putOffProbation();
        taleworlds.removeGame("Mount & Blade");

        gaben.gift("Jamison", "Michael", "Mount & Blade");
        assertFalse(jamison.ownsGame("Mount & Blade"));
        assertTrue(michael.ownsGame("Mount & Blade"));
        assertTrue(domi.ownsGame("Mount & Blade"));

        // Sender does not own the game, no users have the game
        domi.getGameLibrary().get("Mount & Blade").putOffProbation();
        domi.removeGame("Mount & Blade");

        michael.getGameLibrary().get("Mount & Blade").putOffProbation();
        michael.removeGame("Mount & Blade");

        assertThrows(InvalidGameException.class, () -> gaben.gift("Jamison", "Michael", "Mount & Blade"));
        assertFalse(jamison.ownsGame("Mount & Blade"));
        assertFalse(michael.ownsGame("Mount & Blade"));
        assertFalse(domi.ownsGame("Mount & Blade"));
        assertFalse(taleworlds.ownsGame("Mount & Blade"));
    }
}
