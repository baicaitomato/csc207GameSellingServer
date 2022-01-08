package src.tests.system_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.command.Invoker;
import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.UsernameException;
import src.main.system.DistributionSystem;
import src.main.users.BuyStandardUser;
import src.main.users.SellStandardUser;
import src.main.users.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test suite for the DistributionSystem class in the system package
 */
public class TestDistributionSystem {
    static DistributionSystem system;
    static BuyStandardUser sykkuno;
    static SellStandardUser rockstar;
    static Invoker button;

    @BeforeEach
    public void setUp() throws UsernameException, BalanceException {
        User.getAllUsers().clear();
        system = new DistributionSystem();
        rockstar = new SellStandardUser("Rockstar Games", 1032);
        sykkuno = new BuyStandardUser("Sykkuno", 500);
        button = new Invoker();
    }

    /**
     * Testing if the format is right.
     */
    @Test
    public void testLoginRightformat() throws ConstraintException {
        system.login("Rockstar Games", "SS", 1032.00);
        assertTrue(system.isCurrLogin());
        assertEquals(system.getCurrUser(), rockstar);
    }

    /**
     * Testing if there is a user logging in and a new user try to log in.
     */
    @Test
    public void testLoginUserlogged() throws ConstraintException {
        try {
            system.login("Sykkuno", "BS", 500);
            system.login("Rockstar Games", "SS", 1032.1);
            assertTrue(system.isCurrLogin());
            assertEquals(system.getCurrUser(), sykkuno);
        } catch (UsernameException e) {
            assertEquals(new UsernameException("There has been a user logged in.").getMessage(), e.getMessage());
        }
    }

    /**
     * Testing if the input keeps a wrong type.
     */
    @Test
    public void testLoginWrongFormatType() throws ConstraintException {
        system.login("Rockstar Games", "AB", 1032.00);
        assertTrue(system.isCurrLogin());
        assertEquals(system.getCurrUser(), rockstar);
    }

    /**
     * Testing if the input keeps a wrong credit.
     */
    @Test
    public void testLoginWrongFormatCredit() throws ConstraintException {
        system.login("Rockstar Games", "SS", 1032.1);
        assertTrue(system.isCurrLogin());
        assertEquals(system.getCurrUser(), rockstar);
    }

    /**
     * Testing if the user is not in database.
     */
    @Test
    public void testLoginUserNotIn() throws ConstraintException {
        try {
            system.login("Steam", "SS", 1032.00);
            assertFalse(system.isCurrLogin());
            assertNull(system.getCurrUser());
        } catch (UsernameException e) {
            assertEquals(new UsernameException("User 'Steam' does not exist, will jump to next login request.").getMessage(),
                    e.getMessage());
        }
    }

    /**
     * Testing if the format is right.
     */
    @Test
    public void testLogoutRightformat() throws ConstraintException {
        system.login("Rockstar Games", "SS", 1032);
        system.logout();
        assertFalse(system.isCurrLogin());
        assertNull(system.getCurrUser());
    }

    /**
     * Testing if there is no user has logged yet.
     */
    @Test
    public void testLogoutUserNotLogged() {
        try {
            system.logout();
            assertFalse(system.isCurrLogin());
            assertNull(system.getCurrUser());
        } catch (ConstraintException e) {
            assertEquals(new UsernameException("There is no user logged in.").getMessage(), e.getMessage());
        }

    }
}
