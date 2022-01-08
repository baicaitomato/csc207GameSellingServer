package src.tests.command_tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.command.AddCreditCommand;
import src.main.command.Command;
import src.main.exceptions.*;
import src.main.system.DistributionSystem;
import src.main.system.TransactionFactory;
import src.main.users.*;

/**
 * A test suite for the AddCreditCommand in the command package.
 */
public class TestAddCreditCommand {
    static String addCreditCode = TransactionFactory.getAddCreditCode();
    static BuyStandardUser domi;
    static SellStandardUser sunny;
    static FullStandardUser andrew;
    static AdminUser tin;
    static DistributionSystem system;
    static TransactionFactory transFactory;

    /**
     * Starts a new day/back-end session.
     */
    private void startNewDay() {
        HelperFunctions.startNewDay();
        synchronizeUsers();
    }

    /**
     * Synchronizes variable names to user objects every time a new day occurs.
     */
    private static void synchronizeUsers() {
        domi = (BuyStandardUser) User.getAllUsers().get(domi.getUsername());
        sunny = (SellStandardUser) User.getAllUsers().get(sunny.getUsername());
        andrew = (FullStandardUser) User.getAllUsers().get(andrew.getUsername());
        tin = (AdminUser) User.getAllUsers().get(tin.getUsername());
    }


    /**
     * Given a code that describes an addCredit transaction, retrieves an AddCreditTransaction and executes it and then
     * checks the expected value of the given users balance.
     * @param addCreditCode the transaction code describing an addCreditCode
     * @param expectedBalance the expected balance of the given user after the transaction has executed.
     * @param user the user that the transaction is being executed on.
     * @throws FatalException if the given transaction code is invalid
     * @throws ConstraintException if the addCreditCode violates a constraint of any add credit transaction.
     */
    private void executeAndAssert(String addCreditCode, double expectedBalance, User user) throws FatalException, ConstraintException {
        Command addCreditCmd = transFactory.getTransactionCommand(system, addCreditCode);
        assertEquals(AddCreditCommand.class, addCreditCmd.getClass());
        addCreditCmd.execute();
        assertEquals(expectedBalance, user.getBalance());
    }

    /**
     * Sets up the necessary variables to be used in test cases.
     */
    @BeforeEach
    public void setup() throws UsernameException, BalanceException {
        User.getAllUsers().clear();
        domi = new BuyStandardUser("Domi", 100);
        sunny = new SellStandardUser("Sunmastersunboy", 0);
        andrew = new FullStandardUser("Andrew", 250);
        tin = new AdminUser("BaicaiTomato", User.MAXIMUM_CREDITS);
        system = new DistributionSystem();
        transFactory = new TransactionFactory();
    }

    /**
     * Tests cases for executing an add credit command.
     */
    @Test
    public void testExecuteAddCredit() throws ConstraintException, FatalException {
        String domiAddCredits;

        system.login(domi.getUsername(), "BS", 100);

        // Valid amount of credits added.
        domiAddCredits = addCreditCode + " Domi               001000.00";
        executeAndAssert(domiAddCredits, 1100, domi);

        // Adding more than the daily amount
        domiAddCredits = addCreditCode + " Domi               000000.01";
        String finalDomiAddCredits = domiAddCredits;
        assertThrows(DailyCreditLimitException.class, () -> executeAndAssert(finalDomiAddCredits, 1100, domi));
        assertEquals(1100, domi.getBalance());

        system.logout();
        // End of Day 1-------------------------------------------------------------------------------------------------

        startNewDay();
        system.login(domi.getUsername(), "BS", 1100.00);

        executeAndAssert(domiAddCredits, 1100.01, domi);

        domiAddCredits = addCreditCode + " Domi               000999.99";
        executeAndAssert(domiAddCredits, 2100, domi);

        system.logout();
        // End of Day 2-------------------------------------------------------------------------------------------------

        startNewDay();
        system.login(domi.getUsername(), "BS", 2100.00);

        domiAddCredits = addCreditCode + " Domi               999999.99";
        String finalDomiAddCredits1 = domiAddCredits;
        assertThrows(DailyCreditLimitException.class, () -> executeAndAssert(finalDomiAddCredits1, 2100, domi));
        assertEquals(2100, domi.getBalance());

        system.logout();
        system.login(tin.getUsername(), "AA", User.MAXIMUM_CREDITS);

        // Adding more credits into a max credit account
        String tinAddCredits = addCreditCode + " BaicaiTomato       000100.00";
        executeAndAssert(tinAddCredits, tin.getBalance(), tin);
        system.logout();
    }

    /**
     * Test cases for executing AddCreditCommand in admin mode.
     */
    @Test
    public void testAdminModeAddCredit() throws ConstraintException, FatalException {
        system.login(tin.getUsername(), "AA", tin.getBalance());

        String addCreditTransaction = addCreditCode + " Domi               000500.00";
        executeAndAssert(addCreditTransaction, domi.getBalance() + 500, domi);

        addCreditTransaction = addCreditCode + " Domi               000501.00";
        String finalAddCreditTransaction = addCreditTransaction;
        assertThrows(DailyCreditLimitException.class, () -> executeAndAssert(finalAddCreditTransaction, domi.getBalance(), domi));
        assertEquals(600, domi.getBalance());
    }

}
