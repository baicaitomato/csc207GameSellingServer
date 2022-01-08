package src.tests.command_tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.command.Command;
import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.FatalException;
import src.main.exceptions.UsernameException;
import src.main.system.DistributionSystem;
import src.main.system.TransactionFactory;
import src.main.users.AdminUser;
import src.main.users.BuyStandardUser;
import src.main.users.User;

/**
 * A test suite for the CreateCommand class in the commands package
 */
public class TestCreateCommand {
    static AdminUser adminUser;
    static String createCode;
    static DistributionSystem system;
    static TransactionFactory transFactory;

    /**
     * Sets up the necessary admin user, distribution system, and transaction factory to be used in test cases
     * @throws UsernameException if the username of the users are invalid
     * @throws BalanceException if the balance is not valid
     */
    @BeforeEach
    public void setup() throws UsernameException, BalanceException {
        User.getAllUsers().clear();
        adminUser = new AdminUser("Lyle Kam", 0);
        transFactory = new TransactionFactory();
        system = new DistributionSystem();
        system.login(adminUser.getUsername(), "AA", adminUser.getBalance());
        createCode = TransactionFactory.getCreateCode();
    }

    /**
     * Fetches a CreateCommand given a transaction code and executes it.
     * Asserts that the user was created and with the proper attributes.
     * @throws FatalException
     * @throws ConstraintException
     */
    @Test
    public void testCreateUser() throws FatalException, ConstraintException {
        String createUserTrans = createCode + " Dominique       BS 030212.99";
        Command createCmd = transFactory.getTransactionCommand(system, createUserTrans);

        createCmd.execute();

        assertNotEquals(1, User.getAllUsers().size());
        assertTrue(User.getAllUsers().containsKey("Dominique"));
        User dominique = User.getAllUsers().get("Dominique");
        assertEquals(BuyStandardUser.class, dominique.getClass());
        assertEquals(30212.99, dominique.getBalance());
    }
}
