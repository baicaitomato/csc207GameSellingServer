package src.tests.command_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.command.Command;
import src.main.command.GiftCommand;
import src.main.system.DistributionSystem;
import src.main.command.Invoker;

import src.main.exceptions.*;
import src.main.users.*;

/**
 * A test suite for the GiftCommand class.
 */
public class TestGiftCommand {
    static DistributionSystem system;
    static Invoker button;
    static SellStandardUser ea;
    static FullStandardUser michael;
    static AdminUser god;

    /**
     * Sets up the necessary objects for tests.
     */
    @BeforeEach
    public void setUp() throws UsernameException, BalanceException {
        system = new DistributionSystem();
        button = new Invoker();
        ea = new SellStandardUser("Electronic Arts", 999);
        michael = new FullStandardUser("Michael", 100);
        god = new AdminUser("Gaben", 999);
    }

    /**
     * Test Gift Command for admin and non-admin users.
     */
    @Test
    public void testGift() throws ConstraintException {
        system.login("Electronic Arts", ea.getAccountType(), ea.getBalance());
        ea.sell("Fifa 20", 80, 0.2);
        ea.getGame("Fifa 20").putOffProbation();
        system.logout();

        system.login("Gaben", god.getAccountType(), god.getBalance());

        // Admin gifts command (Non admin is the sender)
        Command c = new GiftCommand(system, "Fifa 20", "Michael", "Gaben");
        button.setCommand(c);
        button.run();
        system.logout();

        assertFalse(michael.ownsGame("Fifa 20"));
        assertTrue(god.ownsGame("Fifa 20"));
        assertTrue(ea.ownsGame("Fifa 20"));

        // Non admin gifts
        system.login("Electronic Arts", ea.getAccountType(), ea.getBalance());
        c = new GiftCommand(system, "Fifa 20", "Electronic Arts", "Michael");
        button.setCommand(c);
        button.run();
        system.logout();

        assertTrue(ea.ownsGame("Fifa 20"));
        assertTrue(michael.ownsGame("Fifa 20"));

    }
}
