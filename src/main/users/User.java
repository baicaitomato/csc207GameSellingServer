package src.main.users;

import src.main.Game;
import src.main.exceptions.*;
import src.main.observer.AbstractObservable;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;

public abstract class User extends AbstractObservable implements Serializable {

    private String username;
    protected String accountType;
    private double balance;
    private final HashMap<String, Game> gameLibrary = new HashMap<>();
    private static final HashMap<String, User> allUsers = new HashMap<>();
    private double creditsAddedToday = 0.0;

    // Constants
    public final static double DAILY_DEPOSIT_LIMIT = 1000.00;
    public final static double MAXIMUM_CREDITS = 999999.99;
    public final static int MAX_USERNAME_LENGTH = 15;
    public static final String BUYER_TYPE = "BS";
    public static final String SELLER_TYPE = "SS";
    public static final String FULL_STANDARD_TYPE = "FS";
    public static final String ADMIN_TYPE = "AA";

    /**
     * Creates a new user given a username, starting balance, and an account type.
     * @param username the username of the new account
     * @param balance the starting balance
     * @param accountType the type of user: one of BUYER_TYPE, SELLER_TYPE, FULL_STANDARD_TYPE, ADMIN_TYPE
     * @throws UsernameException if the given username is already taken or not violates username length.
     */
    public User(String username, double balance, String accountType) throws UsernameException, BalanceException {
        super();

        if (username.length() > MAX_USERNAME_LENGTH) {
            this.detach();
            throw new UsernameException("Username exceeds allowed length: " + MAX_USERNAME_LENGTH);
        }

        if (allUsers.containsKey(username)) {
            this.detach();
            throw new UsernameException("Username '" + username + "' already taken.");
        }

        if (balance < 0 ) {
            this.detach();
            throw new BalanceException("Cannot have a negative balance");
        }

        this.username = username;
        this.accountType = accountType;

        this.setBalance(balance);
        allUsers.put(username, this);
        this.notifyObserver("A user '" + this.username + "' has been created with an initial balance of " + this.balance);
    }

    /**
     * @return the username of this user.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @return the type of user: one of BUYER_TYPE, SELLER_TYPE, FULL_STANDARD_TYPE, ADMIN_TYPE
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * @return a copy of this users library of games
     */
    public HashMap<String, Game> getGameLibrary() {
        return (HashMap<String, Game> )this.gameLibrary.clone();
    }

    /**
     * Sets the balance of this user to the given amount.
     * @param amount the amount of credits to set the new balance to.
     */
    protected void setBalance(double amount) {
        if (amount > MAXIMUM_CREDITS) {
            System.out.println("WARNING: Balance exceeds the maximum - " + MAXIMUM_CREDITS + "\nBalance will be set to maximum");
            this.balance = MAXIMUM_CREDITS;
        } else {
            // Source: https://stackoverflow.com/a/28107087 Retrieved at April 4, 2021
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.FLOOR);
            this.balance = Double.parseDouble(df.format(amount));
        }
    }

    /**
     * @return the balance in this users account
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Adds the given amount of credits to this users balance.
     * If the daily deposit limit is exceeded, no credit will be deposited.
     * @param amount the amount to be deposited.
     */
    public void addCredit(double amount) throws DailyCreditLimitException {
        this.setCreditsAddedToday(amount);
        double previousBalance = this.getBalance();
        this.setBalance(this.getBalance() + amount);
        this.notifyObserver(this.username + " has added " + String.format("%.2f", this.getBalance() - previousBalance) + " credits to their balance.");
    }

    /**
     * @return the amount of credit that has been deposited in the current session (day).
     */
    private double getCreditsAddedToday() {
        return this.creditsAddedToday;
    }

    /**
     * Sets the amount of credits deposited in the current session to the newAmount.
     *
     * @param addAmount the new value of how many credits have been deposited in the sessions (day).
     */
    private void setCreditsAddedToday(double addAmount) throws DailyCreditLimitException {
        if (this.creditsAddedToday + addAmount > DAILY_DEPOSIT_LIMIT) {
            throw new DailyCreditLimitException(
                    "Warning: You cannot deposit more than " + DAILY_DEPOSIT_LIMIT + " credits in a day.");
        }
        this.creditsAddedToday += addAmount;
    }

    /**
     * Return true iff game is in user's library.
     *
     * @param gameName Game's title
     * @return true iff game is in user's library.
     */
    public boolean ownsGame(String gameName) {
        return this.getGameLibrary().containsKey(gameName);
    }

    /**
     * Return true iff user exists in database
     *
     * @param accountName user's name
     * @return true iff use exists in database
     */
    public static boolean userExists(String accountName) {
        return User.getAllUsers().containsKey(accountName);
    }

    /**
     * Return true iff this user has enough credits to purchase the game
     *
     * @param game Game
     * @return true iff this user has enough credits to purchase this game
     */
    protected boolean hasEnoughCredits(Game game) {
        // Checking if the buyer has has enough balance to buy this game.
        return this.getBalance() - game.getPrice() >= 0;
    }

    /**
     * Adds a given game to this users library.
     * @param game Game
     */
    protected void addToGameLibrary(Game game) {
        this.gameLibrary.put(game.getName(), game);
    }

    /**
     * @return a hashmap of all Users in the system index by a username.
     */
    public static HashMap<String, User> getAllUsers() {
        return allUsers;
    }

    /**
     * Resets this users amount of credits that have been added.
     * Only used when a new day (backend executed) has occurred.
     */
    protected void resetDailyCreditsAdded() {
        this.creditsAddedToday = 0.0;
    }

    /**
     * @return a string representation of this user containing account information.
     */
    public String toString() {
        return "Username: " + this.username + "\nBalance: " + this.balance + "\nType: " + this.accountType + "\nCredits added: " + this.getCreditsAddedToday();
    }

    /**
     * Removes a game from its own library.
     *
     * @param gameName name of the game
     * @throws InvalidGameException Occurs when the game does not exist in their library
     */
    public void removeGame(String gameName) throws InvalidGameException {
        // checks if the user has the game in their library
        this.gameInLibrary(gameName);

        // Checks if the game has been bought/listed for at least a day
        this.dayPassed(gameName);
        this.gameLibrary.remove(gameName);

        this.notifyObserver(this.username + " has removed '" + gameName + "' from their library");
    }

    /**
     * Non-admin users can only gift the games they own.
     * Gifts a game to another user if the following conditions are meet:
     *      - Game  must be in this user's inventory
     *      - Recipient must not have the game in their inventory
     *      - Recipient must exist in database
     *      - Recipient is not a Sell Standard user
     *
     * @param receiverName Recipient's username
     * @param gameName Game's title
     * @throws ConstraintException Occurs when the conditions are not satisfied.
     */
    public void gift(String receiverName, String gameName) throws ConstraintException {
        // MAKE SURE YOU ADD 24 HOURS SHIT!!!

        // checks if the user has the game in their library
        this.gameInLibrary(gameName);
        Game game = this.gameLibrary.get(gameName).getCopy();
        this.giftHelper(receiverName, game);
    }

    /**
     * If all conditions are met, gift the game.
     * Checks the conditions:
     *      - Recipient is not a Sell Standard Account
     *      - Recipient must not have the game in their inventory
     *      - Recipient must exist in database
     *
     * @param receiverName User receiving the gift
     * @param game Game begin gifted
     * @throws ConstraintException Occurs when any of the checks fail
     */
    protected void giftHelper(String receiverName, Game game) throws ConstraintException {
        User receiver = this.getUser(receiverName);

        // Checks if recipient is a SellStandard user
        this.receiverNotSellOnly(receiver);

        // Checks if recipient already has game
        this.receiverHasGame(receiver, game.getName());

        // Checks if a day has passed since purchase and listing
        this.dayPassed(game.getName());

        // if sender is a owns this game and isnt the original seller, remove game from their inventory
        if (!this.getUsername().equals(game.getSeller())) {
            this.removeGame(game.getName());
        }
        receiver.addToGameLibrary(game);
        this.notifyObserver(this.username + " has gifted the game '" + game.getName() + "' to " + receiverName);
    }
    /**
     * Checks if user has the game in their library
     *
     * @param gameName Game's title
     * @throws InvalidGameException Occurs when user has the game in their inventory
     */
    private void gameInLibrary(String gameName) throws InvalidGameException {
        if (!this.ownsGame(gameName))
            throw new InvalidGameException("Game not in inventory!");
    }

    /**
     * Checks if the recipient has the game
     *
     * @param receiver Recipient
     * @param gameName Game' title
     * @throws InvalidGameException Occurs when the recipient already has the game in their library
     */
    private void receiverHasGame(User receiver, String gameName) throws  InvalidGameException {
        if (receiver.ownsGame(gameName))
            throw new InvalidGameException("Receiver already has game!");
    }

    /**
     * Return user iif username exists in database
     *
     * @param username username
     * @throws UsernameException Occurs when username does not exist in database
     */
    protected User getUser(String username) throws UsernameException {
        if (!userExists(username))
            throw new UsernameException("User '" + username + "' does not exist");

        return User.getAllUsers().get(username);
    }

    /**
     * Check if recipient is of SellStandard Account type
     *
     * @param receiver Recipient of a gift
     * @throws UserAccessException Occurs when the recipient is a SellStandard Account
     */
    private void receiverNotSellOnly(User receiver) throws UserAccessException {
        if (receiver.getAccountType().equals(User.SELLER_TYPE))
            throw new UserAccessException("Cannot gift to a SellStandard account!");
    }

    /**
     * Checks if game is on/off probation
     *
     * @param gameName Game's title
     * @throws InvalidGameException occurs when game is on probation
     */
    protected void dayPassed(String gameName) throws InvalidGameException {
        if (!this.getGame(gameName).isOffProbation())
            throw new InvalidGameException("The action cannot be preformed until the game is off probation!");
    }

    /**
     * Returns the game in user's library
     *
     * @param gameName Game's name
     * @return Game
     */
    public Game getGame(String gameName) {
        return this.getGameLibrary().get(gameName);
    }
}
