package src.tests.command_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import src.main.command.Command;
import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.FatalException;
import src.main.exceptions.UsernameException;
import src.main.system.DistributionSystem;
import src.main.system.TransactionFactory;
import src.main.users.AdminUser;
import src.main.users.User;


/**
 * A test suite for the DeleteCommand in the command package.
 */
public class TestDeleteCommand {
    static DistributionSystem system;
    static TransactionFactory transFactory;
    static AdminUser adminUser;
    static String deleteCode = TransactionFactory.getDeleteCode();

    /**
     * Sets up all the variables required in the test cases of this suite.
     */
    @BeforeAll
    public static void setup() throws UsernameException, BalanceException {
        adminUser = new AdminUser("OompaLoomp@", 99999);
        adminUser.create("CaesarCypher", User.ADMIN_TYPE, 123);
        adminUser.create("ToxicReyna", User.SELLER_TYPE, 4992);
        adminUser.create("MrPhoenix", User.BUYER_TYPE, 222);
        adminUser.create("Brimstone", User.FULL_STANDARD_TYPE, 421.3);
        system = new DistributionSystem();
        transFactory = new TransactionFactory();
        system.login(adminUser.getUsername(), User.ADMIN_TYPE, adminUser.getBalance());
    }

    /**
     * Test cases for receiving and executing a DeleteCommand.
     */
    @Test
    public void testDeleteAdmin() throws FatalException, ConstraintException {
        Command deleteCmd;

        String deleteCypher = deleteCode + " CaesarCypher       000000.00";
        deleteCmd = transFactory.getTransactionCommand(system, deleteCypher);

        deleteCmd.execute();
        assertFalse(User.getAllUsers().containsKey("CaesarCypher"));
    }

    /**
     * Test cases for admin deleting non-admin users using DeleteCommand
     */
    @Test
    public void testDeleteNonAdmin() throws FatalException, ConstraintException {
        Command deleteCmd;

        String deleteTransCode = deleteCode + " ToxicReyna         000000000";
        deleteCmd = transFactory.getTransactionCommand(system, deleteTransCode);
        deleteCmd.execute();
        assertFalse(User.getAllUsers().containsKey("ToxicReyna"));

        deleteTransCode = deleteCode + " MrPhoenix          000000.00";
        deleteCmd = transFactory.getTransactionCommand(system, deleteTransCode);
        deleteCmd.execute();
        assertFalse(User.getAllUsers().containsKey("MrPhoenix"));

        deleteTransCode = deleteCode + " Brimstone          000000000";
        deleteCmd = transFactory.getTransactionCommand(system, deleteTransCode);
        deleteCmd.execute();
        assertFalse(User.getAllUsers().containsKey("Brimstone"));
    }

    /**
     * Test case for admin trying to delete themself using DeleteCommand
     */
    @Test
    public void testDeleteSelf() throws UsernameException, BalanceException, FatalException {
        User.getAllUsers().clear();
        setup();
        Command deleteCmd;

        String deleteTransCode = deleteCode + " OompaLoomp@        000000.00";
        deleteCmd = transFactory.getTransactionCommand(system, deleteTransCode);
        assertThrows(UsernameException.class, () -> deleteCmd.execute());
    }

}
