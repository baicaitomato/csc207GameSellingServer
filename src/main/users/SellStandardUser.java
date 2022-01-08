package src.main.users;


import src.main.exceptions.BalanceException;
import src.main.exceptions.InvalidGameException;
import src.main.exceptions.UsernameException;

/**
 * An extension of the User class with the ability to put games up for sale.
 */
public class SellStandardUser extends User implements SellerInterface{

    /**
     * Initializes a user than can sell games.
     * @param username the username of this user.
     * @param balance the starting balance of this user.
     * @throws UsernameException if the username violates username restrictions or already exists.
     */
    public SellStandardUser(String username, double balance) throws UsernameException, BalanceException {
        super(username, balance, SELLER_TYPE);
    }

    /**
     * Puts a game with the given gameName, price, and discount up for sale by the seller.
     * @param gameName the name of the game to sell.
     * @param price the price of the game.
     * @param discount the discount that can be applied to the new game in an auction.
     * @throws InvalidGameException if the seller already carries a game of the same name.
     */
    public void sell(String gameName, double price, double discount) throws InvalidGameException {
        this.sell(this, gameName, price, discount);
    }
}
