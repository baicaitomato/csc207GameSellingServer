package src.tests.command_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.command.Command;
import src.main.command.RemoveGameCommand;
import src.main.system.DistributionSystem;
import src.main.command.Invoker;

import src.main.exceptions.*;
import src.main.users.*;

/**
 * A test suite for the RemoveCommand class.
 */
public class TestRemoveGameCommand {
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
      * Test RemoveGame Command for admin and non admin users.
      */
     @Test
     public void testRemoveGameCommand() throws ConstraintException {
          system.login("Electronic Arts", ea.getAccountType(), ea.getBalance());
          ea.sell("Fifa 20", 80, 0.2);
          ea.getGame("Fifa 20").putOffProbation();
          system.logout();

          system.login("Michael", michael.getAccountType(), michael.getBalance());
          michael.buy("Electronic Arts", "Fifa 20");
          michael.getGame("Fifa 20").putOffProbation();
          system.logout();

          // Non Admin User removing game
          system.login("Electronic Arts", ea.getAccountType(), ea.getBalance());
          Command c = new RemoveGameCommand(system, "Fifa 20", "Electronic Arts");
          button.setCommand(c);
          button.run();
          system.logout();
          assertFalse(ea.ownsGame("Fifa 20"));

          // Admin User removing game
          system.login("Gaben", god.getAccountType(), god.getBalance());
          god.removeGame("Michael", "Fifa 20");
          assertFalse(michael.ownsGame("Fifa 20"));
     }
}
