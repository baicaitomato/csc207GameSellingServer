package src.main.users;

import src.main.exceptions.BalanceException;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.InvalidGameException;
import src.main.exceptions.UsernameException;

/**
 * An extension of the User class that is able to buy and put games up for sale.
 */
public class FullStandardUser extends User implements SellerInterface, BuyerInterface {

    /**
     * Initializes a user that can both buy and sell games.
     * @param username the username of this new user.
     * @param balance the starting balance of this user.
     * @throws UsernameException if the username is taken or violates username restrictions.
     */
    public FullStandardUser(String username, double balance) throws UsernameException, BalanceException {
        super(username, balance, FULL_STANDARD_TYPE);
    }

    /**
     * Puts a game with the given gameName, price, and discount up for sale by this user..
     * @param gameName the name of the game to sell.
     * @param price the price of the game.
     * @param discount the discount that can be applied to the new game in an auction.
     * @throws InvalidGameException if the seller already carries a game of the same name.
     */
    public void sell(String gameName, double price, double discount) throws InvalidGameException {
        this.sell(this, gameName, price, discount);
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
