package src.tests.command_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.command.Command;
import src.main.command.RefundCommand;
import src.main.system.DistributionSystem;
import src.main.command.Invoker;

import src.main.exceptions.*;
import src.main.users.*;

/**
 * A test suite for the RefundCommand class.
 */
public class TestRefundCommand {

    static DistributionSystem system;
    static Invoker button;
    static SellStandardUser ea;
    static FullStandardUser michael;
    static BuyStandardUser kid;
    static AdminUser god;

    /**
     * Sets up the necessary objects for future tests.
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
     * Test RefundCommand for admin user and non admin user.
     */
    @Test
    public void testRefundCommand() throws ConstraintException {

        system.login("Electronic Arts", ea.getAccountType(), ea.getBalance());
        ea.sell("Fifa 20", 80, 0.2);
        ea.getGame("Fifa 20").putOffProbation();
        system.logout();

        system.login("Michael", michael.getAccountType(), michael.getBalance());
        michael.buy("Electronic Arts", "Fifa 20");
        system.logout();

        // Non admin access refund
        system.login("Michael", michael.getAccountType(), michael.getBalance());
        Command c = new RefundCommand(system, "Michael", "Electronic Arts", "80");
        button.setCommand(c);
        assertThrows(UserAccessException.class, () -> button.run());
        system.logout();
        assertEquals(20, michael.getBalance());

        // Admin access refund
        system.login("Gaben", god.getAccountType(), god.getBalance());
        c = new RefundCommand(system, "Michael", "Electronic Arts", "80");
        button.setCommand(c);
        button.run();
        system.logout();
        assertEquals(100, michael.getBalance());
    }
}