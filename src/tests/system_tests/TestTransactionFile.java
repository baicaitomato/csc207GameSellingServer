package src.tests.system_tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.command.*;
import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.FatalException;
import src.main.exceptions.UsernameException;
import src.main.system.*;
import java.io.*;
import java.util.*;
import src.main.users.*;
import src.tests.command_tests.HelperFunctions;


/**
 * A test suite for the TransactionFile class.
 */
public class TestTransactionFile {

    DistributionSystem system;
    TransactionFile file;
    TransactionFactory factory;
    UserFactory user_factory;
    Invoker button;
    Command command;

    /**
     * create some users for the test cases
     * @throws UsernameException
     * @throws BalanceException
     */
    @BeforeEach
    public void setup() throws UsernameException, BalanceException {

        user_factory = new UserFactory();

        user_factory.makeUser("God", "AA", 9999.00);

        system = new DistributionSystem();
        factory = new TransactionFactory();
        button = new Invoker();

    }

    /**
     * start new day after each test case
     */
    @AfterEach
    public void teardown(){
        HelperFunctions.startNewDay();
    }

    /**
     * Test case for System.TransactionFile
     * @throws FatalException
     * @throws ConstraintException
     */
    @Test
    public void testTransactionFile_1() throws FatalException, ConstraintException {

        String src = new File(System.getProperty("user.dir"), "src").getPath();
        String src_tests = new File(src, "tests").getPath();
        String src_test_systemTests = new File(src_tests, "system_tests").getPath();

        File testFile = new File(src_test_systemTests, "testfile1.txt");

        file = new TransactionFile(testFile);

        Iterator<String> iterator = file.iterator();


        ArrayList<String> codes = new ArrayList<>();

        while (iterator.hasNext()){
            codes.add(iterator.next());
        }

        system.login("God", "AA", 9999.00);

        for (String code: codes){
            command = factory.getTransactionCommand(system, code);
            command.execute();
        }

        assertFalse(system.isCurrLogin());

    }
}
