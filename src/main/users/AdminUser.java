package src.main.users;

import src.main.Game;
import src.main.exceptions.*;


/**
 * An Administrator user with the permissions of all user types along with:
 *      - Adding credits to other users.
 *      - Creating and deleting users.
 *      - Activate discounts on all games on sale.
 */
public class AdminUser extends FullStandardUser {

    private static boolean auction = false;

    /**
     * Initializes a AdminUser with the given attributes.
     *
     * @param username    the username of this User
     * @param balance     the amount of credits in this User's account.
     * @throws UsernameException if the username already exists or username.length > User.MAX_USERNAME_LENGTH
     */
    public AdminUser(String username, double balance) throws UsernameException, BalanceException {
        super(username, balance);
        this.accountType = User.ADMIN_TYPE;
        this.notifyObserver("An admin has been created");
    }

    /**
     * return the new user with the attribute
     * precondition: new user names must be different from all other current users
     *
     * @param userName the name of the new user
     * @param type the type of the new user
     * @param balance the balance of the new user
     * @throws UsernameException if the username already exists or username.length > User.MAX_USERNAME_LENGTH
     */
    public void create(String userName, String type, double balance) throws UsernameException, BalanceException {
        // remember to check all other current users
        UserFactory factory = new UserFactory();
        factory.makeUser(userName, type, balance);
    }

    /**
     * Deletes the user with the given userName.
     * @param userName the username of the user to be deleted.
     * @throws UsernameException if the given username doesn't exist or an admin is trying to delete themselves.
     */
    public void delete(String userName) throws UsernameException {
        if (!User.getAllUsers().containsKey(userName)) {
            throw new UsernameException("User '" + userName + "' does not exist");
        }

        if (userName.equals(this.getUsername())) {
            throw new UsernameException("Cannot delete your account");
        }

        User.getAllUsers().get(userName).detach();
        User.getAllUsers().remove(userName);
        this.notifyObserver(userName + " has been deleted.");
    }

    /**
     * Toggles an auction on all games for sale.
     *      - If an auction is currently occurring, stops the auction.
     *      - If there is no current auction, starts and auction.
     */
    public void auctionSale() {
        auction = !auction;
        String obsMessage;
        if (auction) {
            obsMessage = "Auction sale has started.";
        } else {
            obsMessage = "Auction sale has ended.";
        }
        this.notifyObserver(obsMessage);
    }

    /**
     * @return true if an auction sale is occurring and false otherwise.
     */
    public static boolean isAuction() {
        return auction;
    }

    /**
     * Adds the given amount of credit to the account with the given username.
     * @param userName the user having credit added to their account
     * @param amount the amount of credit being added
     * @throws UsernameException If the given username does not exist.
     */
    public void addCredit(String userName, double amount) throws UsernameException, DailyCreditLimitException {
        if (!User.getAllUsers().containsKey(userName)) {
            throw new UsernameException("User '" + userName + "' does not exist");
        }
        User user = User.getAllUsers().get(userName);
        user.addCredit(amount);
    }

    /**
     * Refunds the transaction iff:
     * - both users exists
     * - seller has enough funds
     * - both account types are valid (buyer is not a SellStandard & seller is not a BuyStandard)
     *
     * @param buyerName Buyer's username
     * @param sellerName Seller's username
     * @param amount Amount to be refunded
     * @throws ConstraintException Occurs when at least one constraint is not satisfied.
     */
    public void refund(String buyerName, String sellerName, double amount) throws ConstraintException {
        // checks to ensure both users exist
        User buyer = this.getUser(buyerName);
        User seller = this.getUser(sellerName);

        // checks to ensure both account types make sense
        this.validateUserType(buyer, seller);
        this.validateSellerBalance(seller, amount);

        //  transfer the seller's funds to buyer's balance
        this.refundFunds(buyer, seller, amount);
        this.notifyObserver(buyerName + "has gotten a refund from " + sellerName + " for " + amount + " credits");
    }

    /**
     * Checks if users are valid
     * Constraints:
     * - buyer cannot be a sell standard account
     * - seller cannot be a buy standard account
     *
     * @param buyer User that purchased the game
     * @param seller User that sold the product
     * @throws UserAccessException occurs when constraints are not satisfied.
     */
    private void validateUserType(User buyer, User seller) throws UserAccessException {
        if (buyer.getAccountType().equals(User.SELLER_TYPE) || seller.getAccountType().equals(User.BUYER_TYPE))
            throw new UserAccessException("Transaction not possible!");

         if (buyer == seller){
             throw new UserAccessException("Can not refund if both buyer and seller are the same!");
         }
    }

    /**
     * Checks if seller has enough balance to refund the buyer
     *
     * @param seller User that sold the product
     * @param amount Amount to be refunded
     * @throws BalanceException Occurs when seller does not have enough credit to refund
     */
    private void validateSellerBalance(User seller, double amount) throws BalanceException {
        if (seller.getBalance() - amount < 0.0)
            throw new BalanceException(seller.getUsername() + " does not have sufficient funds to refund the game!");
    }

    /**
     * Move the amount from seller's balance to user's balance
     *
     * @param buyer User that purchased the product
     * @param seller User that sold the product
     * @param amount Amount to be refunded
     */
    private void refundFunds(User buyer, User seller, double amount) {
        buyer.setBalance(buyer.getBalance() + amount);
        seller.setBalance(seller.getBalance() - amount);
    }

    /**
     * Removes user's game iff:
     * - user exists in database
     * - user owns the game
     *
     * @param username username of user that wants to remove their game
     * @param gameName game name
     * @throws ConstraintException occurs if at least one constraint is not met
     */
    public void removeGame(String username, String gameName) throws ConstraintException {
        User user = this.getUser(username);
        user.removeGame(gameName);
    }

    /**
     * Admins can gift from any user to any other.
     * Gifts a game to another user if the following conditions are meet:
     *      - Recipient must not have the game in their inventory
     *      - Recipient must exist in database
     *      - Recipient is not a Sell Standard user
     *      - Game must exist
     *
     * @param receiverName Recipient's username
     * @param gameName Game's title
     * @throws ConstraintException Occurs when the conditions are not satisfied.
     */
    public void gift(String senderName, String receiverName, String gameName) throws ConstraintException {
        User sender = this.getUser(senderName);

        // If the sender has the game, remove game from sender's inventory
         if (sender.ownsGame(gameName)) {
             sender.gift(receiverName, gameName);
         }

         // If sender does not have the game
         else {
             // Checks if any other users has the game
             Game game = null;

             for (User user: User.getAllUsers().values() ) {
                 if (user.ownsGame(gameName) && user.getGame(gameName).isOffProbation()) {
                     game = user.getGame(gameName).getCopy();
                     game.putOffProbation();
                     break;
                 }
             }

             if (game == null) {
                 throw new InvalidGameException("Game cannot be gifted!");
             }
             else {
                 sender.addToGameLibrary(game);
                 sender.giftHelper(receiverName, game);
             }
         }
    }
}
