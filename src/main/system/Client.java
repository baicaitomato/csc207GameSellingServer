package src.main.system;

import src.main.command.*;
import src.main.exceptions.*;
import src.main.users.UserFactory;
import src.main.users.UserLoader;
import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * A class that performs the execution of the backend system through the use of transaction codes in daily.txt files.
 */
public class Client {
    private static Scanner scanner = new Scanner(System.in);
    private static final int MANUALLY_INPUT_USERS = 0;
    private static final int EXECUTE_BACKEND = 1;
    private static final int INVALID_CHOICE = -1;

    /**
     * Allows the user to manually input users for use in daily transactions in the backend.
     * This function was made because Prof Miljanovic said that we should have a way of manually adding users to our
     * backend during office hours.
     */
    public static void manuallyInputUsers() {
        UserFactory userFactory = new UserFactory();

        chooseLoadPreviousSession();
        File manualUserInput = findManualUserInputFile();
        if (manualUserInput == null) {
            System.out.println("Something went wrong with manual_user_input.txt\nAborting...");
            return;
        }
        TransactionFile userInput = new TransactionFile(manualUserInput);

        int index = 0;
        String username, userType;
        double initialBalance;
        for (String userInfo: userInput) {
            String[] entries = userInfo.split(",");
            if (entries.length != 3) {
                if (!userInfo.equals("")) {  // Ignoring empty lines
                    System.out.println("The following line doesn't follow the specified format (username,usertype,balance): " + (index + 1));
                }
            } else {
                username = entries[0];
                userType = entries[1];
                try {
                    initialBalance = Double.parseDouble(entries[2]);
                    try {
                        userFactory.makeUser(username, userType, initialBalance);
                    } catch (UsernameException | BalanceException e) {
                        System.out.println(e.getMessage());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Line " + (index + 1) + " does not have a valid balance entry");
                }
            }
            index++;
        }
    }

    /**
     * Retrieves and returns the file that is used to manually load in users into the database.
     * If the file doesn't exist, will create one.
     * @return the .txt file containing user information in the format username,usertype,balance
     */
    private static File findManualUserInputFile() {
        String dbDirectory = new File(System.getProperty("user.dir"), "db").getPath();
        File manualUserInput = new File(dbDirectory, "manual_user_input.txt");
        if (!manualUserInput.exists()) {
            try {
                manualUserInput.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        return manualUserInput;
    }

    /**
     * Continuously prompts the user for their choice in loading in the users from the previous backend session until
     * a proper reply has been given.
     */
    private static void chooseLoadPreviousSession() {
        String choice;
        while (true) {
            System.out.println("Do you want to load in the users from the previous session before manually adding new users?\n" +
                    "Insert 'yes' or 'no'");
            choice = scanner.next().stripTrailing().stripLeading().toLowerCase();
            if (choice.equals("yes")) {
                UserLoader.loadUsers();
                return;
            } else if (choice.equals("no")) {
                return;
            } else {
                scanner = new Scanner(System.in);
            }
        }
    }

    /**
     * Prompts the user to choose one of the presented operations until a valid reply is inputted.
     * @return either MANUALLY_INPUT_USERS or EXECUTE_BACKEND
     */
    private static int getChoice() {
        int choice;
        do {
            System.out.println("Choose one of the following:\n" +
                    "0 - Manually create users for the backend\n" +
                    "1 - Read transactions from daily.txt");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner = new Scanner(System.in);
                choice = INVALID_CHOICE;
            }

        } while (choice != MANUALLY_INPUT_USERS && choice != EXECUTE_BACKEND);
        return choice;
    }

    /**
     * Executes the backend by reading transactions from a daily.txt file.
     */
    public static void executeBackend(){
        UserLoader.loadUsers();
        ErrorRecorder recorder = new ErrorRecorder();

        DistributionSystem system = new DistributionSystem();
        Invoker button = new Invoker();
        TransactionFactory factory = new TransactionFactory();

        TransactionFile transactionFile = new TransactionFile(new File(System.getProperty("user.dir"), "daily.txt"));

        Command cmd;
        for (String transactionCode: transactionFile) {
            String errorMessage;
            try {
                cmd = factory.getTransactionCommand(system, transactionCode);
                if (!system.isCurrLogin() && !(cmd instanceof LoginCommand)){
                    continue; //According to @801
                }
                button.setCommand(cmd);
                try {
                    button.run();
                } catch (ConstraintException e) {
                    errorMessage = "Constraint error: Transaction Code: " + transactionCode +
                            "\nMessage - " + e.getMessage() + "\n\n";
                    recorder.recordError(errorMessage);
                    System.out.println("Transaction Code: " + transactionCode + " - " + e.getMessage());
                }
            } catch (FatalException e) {
                errorMessage = "Fatal error: Transaction Code: " + transactionCode +
                        "\nMessage - " + e.getMessage() +
                        "\nin file: " + "daily.txt" +"\n\n";
                recorder.recordError(errorMessage);
                System.out.println("Transaction Code: " + transactionCode + " - " + e.getMessage());
            }
        }
        recorder.closeWriter();
    }

    /**
     * This function gives asks the user whether they want to manually create users or execute a days worth of
     * transactions in the backend by reading a daily.txt file.
     */
    public static void main(String[] args) {
        int choice = getChoice();

        if (choice == MANUALLY_INPUT_USERS) {
            manuallyInputUsers();
        } else {
            executeBackend();
        }
    }
}