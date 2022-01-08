package src.main.users;

import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.UsernameException;

/**
 * An extension of the User class with the ability to buy Games on sale.
 */
public class BuyStandardUser extends User implements BuyerInterface {

    /**
     * Initializes a user that can buy games.
     * @param username the username of this user
     * @param balance the starting balance of this user
     * @throws UsernameException if the username violates username restrictions or the username is taken.
     */
    public BuyStandardUser(String username, double balance) throws UsernameException, BalanceException {
        super(username, balance, BUYER_TYPE);
    }

    /**
     * Buys a game specified by its game name from a seller with username sellerName.
     * @param sellerName the username of the seller account
     * @param gameName the name of the game to buy from the seller.
     * @throws ConstraintException if the seller doesn't carry the game, the buyer is buying from themself, the seller doesn't exist
     *                             or the buyer doesn't have sufficient balance to buy the game.
     */
    public void buy(String sellerName, String gameName) throws ConstraintException {
        this.buy(this, sellerName, gameName);
    }
}
