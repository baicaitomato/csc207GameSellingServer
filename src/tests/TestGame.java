package src.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.Game;
import src.main.exceptions.BalanceException;
import src.main.exceptions.InvalidGameException;
import src.main.exceptions.UsernameException;
import src.main.users.AdminUser;
import src.main.users.User;

/**
 * A test suite for the Game class
 */
public class TestGame {
    Game overwatch, spellbreak;
    AdminUser adminUser;

    /**
     * Sets up the necessary variables to be used in test cases
     */
    @BeforeEach
    public void setup() throws InvalidGameException, UsernameException, BalanceException {
        overwatch = new Game("Overwatch", "Blizzard", 29.99, 50);
        spellbreak = new Game("Spellbreak", "Proletariat, Inc.", 0.0, 0);
        overwatch.putOffProbation();
        spellbreak.putOffProbation();
        User.getAllUsers().clear();
        adminUser = new AdminUser("AuctionToggler", 0);
    }

    /**
     * Tests cases for the getOnSale method in Game.
     */
    @Test
    public void testIsOffProbation() throws InvalidGameException {
        assertTrue(overwatch.isOffProbation());
        assertFalse(new Game("TF2", "IDK", 30, 30).isOffProbation());
    }

    /**
     * Test cases for Game.getName.
     */
    @Test
    public void testGetName() {
        assertEquals("Overwatch", overwatch.getName());
        assertEquals("Spellbreak", spellbreak.getName());
    }

    /**
     * Test cases for Game.getSeller.
     */
    @Test
    public void testGetSeller() {
        assertEquals("Blizzard", overwatch.getSeller());
        assertEquals("Proletariat, Inc.", spellbreak.getSeller());
    }

    /**
     * Test cases for Game.getPrice.
     */
    @Test
    public void testGetPrice() {
        assertEquals(29.99, overwatch.getPrice());
        assertEquals(0.0, spellbreak.getPrice());
    }

    /**
     * Test cases for when creating a new Game throws an Exception.
     */
    @Test
    public void testInvalidGameException() {
        String errorMsg = "CONSTRAINT ERROR: ";
        try {
            assertThrows(InvalidGameException.class, () -> new Game("The Lord of the Rings: Battle for Middle Earth II: Rise of the Witch King",
                    "Electronic Arts",4.99, 30));
            new Game("The Lord of the Rings: Battle for Middle Earth II: Rise of the Witch King",
                    "Electronic Arts",4.99, 30);
        } catch (InvalidGameException e) {
            assertEquals(errorMsg + Game.INVALID_GAME_NAME_ERROR_MSG.strip(), e.getMessage());
        }

        try {
            assertThrows(InvalidGameException.class, () -> new Game("Atlantis II", "Cryo Interactive", 5000, 10));
            new Game("Atlantis II", "Cryo Interactive", 5000, 10);
        } catch (InvalidGameException e) {
            assertEquals(errorMsg + Game.INVALID_PRICE_ERROR_MSG.strip(), e.getMessage());
        }

        try {
            assertThrows(InvalidGameException.class, () -> new Game("GTA V", "Epic Games", 59.99, 100));
            new Game("GTA V", "Epic Games", 59.99, 100);
        } catch (InvalidGameException e) {
            assertEquals(errorMsg + Game.INVALID_DISCOUNT_ERROR_MSG, e.getMessage());
        }

        try {
            assertThrows(InvalidGameException.class, () -> new Game("The Lord of the Rings: Battle for Middle Earth II: Rise of the Witch King",
                    "Epic Games", 5000, 100));
            new Game("The Lord of the Rings: Battle for Middle Earth II: Rise of the Witch King",
                    "Epic Games",
                    5000,
                    100);
        } catch (InvalidGameException e) {
            assertEquals((errorMsg + Game.INVALID_PRICE_ERROR_MSG + Game.INVALID_GAME_NAME_ERROR_MSG
                    + Game.INVALID_DISCOUNT_ERROR_MSG).strip(), e.getMessage());
        }
    }

    /**
     * Tests if the static auction variable retains its old value whenever a new movie is created.
     * @throws InvalidGameException not used.
     */
    @Test
    public void testStaticAuction() throws InvalidGameException {
        adminUser.auctionSale();
        assertFalse(AdminUser.isAuction());
        adminUser.auctionSale();
        new Game("Placeholder", "Non-existent", 50, 50);
        assertTrue(AdminUser.isAuction());
    }

    /**
     * Test cases for when auctionSale is called by AdminUsers
     */
    @Test
    public void testAuctionSale() {
        // Games aren't on auction
        while (AdminUser.isAuction()) {
            adminUser.auctionSale();
        }
        this.testGetPrice();

        // Auction is on
        adminUser.auctionSale();
        assertEquals(14.995, overwatch.getPrice());
        assertEquals(0, spellbreak.getPrice());
    }

    /**
     * Test cases for Game.getCopy
     */
    @Test
    public void testGetCopy() throws InvalidGameException {
        assertNotEquals(overwatch.getCopy(), overwatch);
        assertNotEquals(spellbreak.getCopy(), spellbreak);

        Game newGame = new Game("Bruh", "Pain", 30, 40);
        newGame.putOffProbation();

        Game newGameCopy = newGame.getCopy();
        assertFalse(newGameCopy.isOffProbation());
    }
}
