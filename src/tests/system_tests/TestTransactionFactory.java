package src.tests.system_tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import src.main.Game;
import src.main.command.*;
import src.main.exceptions.FatalException;
import src.main.system.DistributionSystem;
import src.main.system.TransactionFactory;
import src.main.users.User;

import java.util.ArrayList;
import java.util.Random;


/**
 * A test suite for the TransactionFactory class in the system package.
 */
public class TestTransactionFactory {
    static DistributionSystem system;
    static TransactionFactory transFactory;
    static Random rng = new Random();
    static ArrayList<String> loginTransactionCodes = new ArrayList<>();
    static ArrayList<String> logoutTransactionCodes = new ArrayList<>();
    static ArrayList<String> deleteTransactionCodes = new ArrayList<>();
    static ArrayList<String> auctionTransactionCodes = new ArrayList<>();
    static ArrayList<String> addCreditTransactionCodes = new ArrayList<>();
    static ArrayList<String> createUserTransactionCodes = new ArrayList<>();
    static ArrayList<String> refundTransactionCodes = new ArrayList<>();
    static ArrayList<String> sellTransactionCodes = new ArrayList<>();
    static ArrayList<String> buyTransactionCodes = new ArrayList<>();
    static ArrayList<String> removeTransactionCodes = new ArrayList<>();
    static ArrayList<String> giftTransactionCodes = new ArrayList<>();

    /**
     * Generates a random alphanumeric and special character username string
     * @return a username made by a random number generator.
     */
    private static String getRandomUsername() {
        String alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ " + "0123456789" + "!@#$%^&()-+=;:<>/?" + "abcdefghijklmnopqrstuvwxyz";
        String username = "";
        String emptyUsername = "";
        for (int i = 1; i <= User.MAX_USERNAME_LENGTH; i++) {
            username += alphanum.charAt(rng.nextInt(alphanum.length()));
            emptyUsername += " ";
        }
        if (username.equals(emptyUsername)) {
            username = "Improbable";
        }

        assertEquals(15, username.length());
        return username;
    }

    /**
     * Generates a random balance of the format \d{6}.\d{2}
     * @return a random number string in the range 0 <= 999,999.99
     */
    private static String getRandomUserBalance() {
        String nums = "0123456789";
        String balance = "";

        for (int i = 0; i < 6; i++) {
            balance += nums.charAt(rng.nextInt(nums.length()));
        }

        balance += '.';
        balance += nums.charAt(rng.nextInt(nums.length()));
        balance += nums.charAt(rng.nextInt(nums.length()));

        return balance;
    }

    /**
     * Generates a random discount of the format \d{2}.\d{2}
     * @return a discount for a game.
     */
    private static String getRandomDiscount() {
        String nums = "0123456789";
        String discount = "";
        for (int i = 1; i <= 2; i++) {
            discount += nums.charAt(rng.nextInt(nums.length()));
        }
        discount += ".";
        for (int i = 1; i <= 2; i++) {
            discount += nums.charAt(rng.nextInt(nums.length()));
        }
        return discount;
    }

    /**
     * @return one of User.FULL_STANDARD_TYPE, User.SELLER_TYPE, User.BUYER_TYPE, User.ADMIN_TYPE
     */
    private static String getRandomUserType() {
        String[] userTypes = new String[]{User.FULL_STANDARD_TYPE, User.SELLER_TYPE, User.BUYER_TYPE, User.ADMIN_TYPE};
        return userTypes[rng.nextInt(userTypes.length)];
    }

    /**
     * Generates a random valid game name.
     * @return a game name.
     */
    private static String getRandomGameName() {
        String alphaNum = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ";
        String gameName = "";
        String emptyGameName = "";
        for (int i = 1; i <= Game.MAXIMUM_NAME_LENGTH; i++) {
            gameName += alphaNum.charAt(rng.nextInt(alphaNum.length()));
            emptyGameName += " ";
        }

        if (gameName.equals(emptyGameName)) {
            gameName = "Near Impossible";
        }

        return gameName;
    }

    /**
     * Generates a random price in the range of 000.00 <= 999.99.
     * @return a price for a given game.
     */
    private static String getRandomGamePrice() {
        String nums = "0123456789";
        String price = "";

        for (int i = 1; i <= 3; i++) {
            price += nums.charAt(rng.nextInt(nums.length()));
        }
        price += ".";
        for (int i = 1; i <= 2; i++) {
            price += nums.charAt(rng.nextInt(nums.length()));
        }
        return price;
    }

    /**
     * Used in the setup method to populate the login, create, and add credit transaction codes array lists.
     */
    private static void populateLoginAddCreditCreateTransactionCodes() {
        loginTransactionCodes.add("00 Dominique123456 AA 003040.99");
        loginTransactionCodes.add("00 Raymond         BS 145832.41");
        loginTransactionCodes.add("00 RyGuy390        SS 000000.00");
        loginTransactionCodes.add("00 MicroTofu       FS 321469.01");
        loginTransactionCodes.add("00 Kage            FS 999999.99");

        String newCode;
        String addCreditCode;
        for (int i = 0; i <= 100; i++) {
            newCode = getRandomUsername() + " " + getRandomUserType() + " " + getRandomUserBalance();
            addCreditCode = getRandomUsername() + "    " + getRandomUserBalance();
            loginTransactionCodes.add(TransactionFactory.getLoginCode() + " " + newCode);
            addCreditTransactionCodes.add(TransactionFactory.getAddCreditCode() + " " + addCreditCode);
            createUserTransactionCodes.add(TransactionFactory.getCreateCode() + " " + newCode);
        }
    }

    /**
     * Used in the setup method to populate the logout, delete, and auction sale transaction codes array lists.
     */
    private static void populateLogoutDeleteAuctionTransactionCodes() {
        String code;
        String[] balances = {"000000000", "000000.00"};

        for (int i = 0; i <= 100; i++) {
            code = getRandomUsername() + "    " + balances[rng.nextInt(2)];

            logoutTransactionCodes.add(TransactionFactory.getLogoutCode() + " " + code);
            deleteTransactionCodes.add(TransactionFactory.getDeleteCode() + " " + code);
            auctionTransactionCodes.add(TransactionFactory.getAuctionCode() + " " + code);
        }
    }

    /**
     * Used in the setup method to populate the array list that contains refund based transactions.
     */
    private static void populateRefundTransactionCodes() {
        String code;

        for (int i = 0; i <= 100; i++) {
            code = TransactionFactory.getRefundCode() + " ";
            code += getRandomUsername() + " " + getRandomUsername() + " " + getRandomUserBalance();
            refundTransactionCodes.add(code);
        }
    }

    /**
     * Used in the setup method to populate the array list that contains sell based transactions
     */
    private static void populateSellTransactionCodes() {
        String code;

        for (int i = 0; i <= 100; i++) {
            code = TransactionFactory.getSellCode() + " ";
            code += getRandomGameName() + " " + getRandomUsername() + " " + getRandomDiscount() + " " + getRandomGamePrice();
            sellTransactionCodes.add(code);
        }
    }

    /**
     * Used in the setup method to populate the array list that contains buy based transactions
     */
    private static void populateBuyTransactionCodes() {
        String code;

        for (int i = 0; i <= 100; i++) {
            code = TransactionFactory.getBuyCode() + " ";
            code += getRandomGameName() + " " + getRandomUsername() + " " + getRandomUsername();
            buyTransactionCodes.add(code);
        }
    }

    /**
     * Used in the setup method to populate the array lists that contain removegame and gift based transactions.
     */
    private static void populateRemoveGameGiftTransactionCodes() {
        String code;
        String emptyUsername = "               ";
        for (int i = 0; i <= 100; i++) {
            code = getRandomGameName() + " " + getRandomUsername() + " " + getRandomUsername();
            removeTransactionCodes.add(TransactionFactory.getRemoveGameCode() + " " + getRandomGameName() + " " + getRandomUsername() + " " + emptyUsername);
            giftTransactionCodes.add(TransactionFactory.getGiftCode() + " " + code);
        }
    }

    /**
     * Checks if all the transaction codes within the transactionCodes array list make TransactionFactory produce
     * a command object of type commandClass.
     * @param commandClass the type of Transaction that should be produced by TransactionFactory
     * @param transactionCodes the codes that should produce a commandClass type object
     */
    private static void testAssertCommandType(Class commandClass, ArrayList<String> transactionCodes) throws FatalException {
        Command cmd;

        assertNotEquals(0, transactionCodes.size());

        for (String code: transactionCodes) {
            cmd = transFactory.getTransactionCommand(system, code);
            assertEquals(commandClass, cmd.getClass());
        }
    }

    /**
     * Sets up all the necessary static variables to be used in this test suite.
     */
    @BeforeAll
    public static void setUp() {
        system = new DistributionSystem();
        transFactory = new TransactionFactory();
        populateLoginAddCreditCreateTransactionCodes();
        populateLogoutDeleteAuctionTransactionCodes();
        populateRefundTransactionCodes();
        populateSellTransactionCodes();
        populateBuyTransactionCodes();
        populateRemoveGameGiftTransactionCodes();
    }

    /**
     * Test cases for UserFactory for login transactions
     */
    @Test
    public void testGetLoginTransaction() throws FatalException {
        testAssertCommandType(LoginCommand.class, loginTransactionCodes);
    }

    /**
     * Test cases for UserFactory for logout transactions
     */
    @Test
    public void testGetLogoutTransaction() throws FatalException {
        testAssertCommandType(LogoutCommand.class, logoutTransactionCodes);
    }

    /**
     * Test cases for UserFactory for delete transactions
     */
    @Test
    public void testGetDeleteTransaction() throws FatalException {
        testAssertCommandType(DeleteCommand.class, deleteTransactionCodes);
    }

    /**
     * Test cases for UserFactory for auction transactions
     */
    @Test
    public void testGetAuctionTransaction() throws FatalException {
        testAssertCommandType(AuctionsaleCommand.class, auctionTransactionCodes);
    }

    /**
     * Test cases for UserFactory for add credit transactions
     */
    @Test
    public void testGetAddCreditTransaction() throws FatalException {
        testAssertCommandType(AddCreditCommand.class, addCreditTransactionCodes);
    }

    /**
     * Test cases for UserFactory for create user transactions
     */
    @Test
    public void testGetCreateTransaction() throws FatalException {
        testAssertCommandType(CreateCommand.class, createUserTransactionCodes);
    }

    /**
     * Test cases for UserFactory for refund transactions
     */
    @Test
    public void testGetRefundTransaction() throws FatalException {
        testAssertCommandType(RefundCommand.class, refundTransactionCodes);
    }

    /**
     * Test cases for UserFactory for sell transactions
     */
    @Test
    public void testGetSellTransaction() throws FatalException {
        testAssertCommandType(SellCommand.class, sellTransactionCodes);
    }

    /**
     * Test cases for UserFactory for buy transactions
     */
    @Test
    public void testGetBuyTransaction() throws FatalException {
        testAssertCommandType(BuyCommand.class, buyTransactionCodes);
    }

    /**
     * Test cases for UserFactory for remove game transactions
     */
    @Test
    public void testGetRemoveTransaction() throws FatalException {
        testAssertCommandType(RemoveGameCommand.class, removeTransactionCodes);
    }

    /**
     * Test cases for UserFactory for gift transactions
     */
    @Test
    public void testGetGiftTransaction() throws FatalException {
        testAssertCommandType(GiftCommand.class, giftTransactionCodes);
    }

    /**
     * Test cases for UserFactory for when fatal exceptions are thrown.
     */
    @Test
    public void testThrowsFatalException() {
        assertThrows(FatalException.class, () -> transFactory.getTransactionCommand(system, "hjkdsa;h 1231j ;124n 1j;233 21kj; "));
        assertThrows(FatalException.class, () -> transFactory.getTransactionCommand(system, "23 Domi            AA 333222.99"));
        assertThrows(FatalException.class, () -> transFactory.getTransactionCommand(system, "01 ThisNameIsOverFifteenCharacters BS 000000.00"));
        assertThrows(FatalException.class, () -> transFactory.getTransactionCommand(system, "01 ValidName      FS 100000000"));
    }
}
