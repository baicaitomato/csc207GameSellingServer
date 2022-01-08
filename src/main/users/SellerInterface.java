package src.main.users;

import src.main.Game;
import src.main.exceptions.InvalidGameException;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * An interface that defines the behavior for any User that must be able to sell games.
 */
public interface SellerInterface {

    /**
     * Puts a game with the given gameName, price, and discount up for sale by the seller.
     * @param seller the seller that will carry the game.
     * @param gameName the name of the game to sell.
     * @param price the price of the game.
     * @param discount the discount that can be applied to the new game in an auction.
     * @throws InvalidGameException if the seller already carries a game of the same name.
     */
    default void sell(User seller, String gameName, double price, double discount) throws InvalidGameException {
        // Checking if the seller already has the new game in their inventory
        if (seller.ownsGame(gameName)) {
            throw new InvalidGameException(seller.getUsername() + " already has '" + gameName + "' in their library");
        } else {
            // Source: https://stackoverflow.com/a/28107087 Retrieved at April 4, 2021
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.FLOOR);
            price = Double.parseDouble(df.format(price));
            seller.addToGameLibrary(new Game(gameName, seller.getUsername(), price, discount));
            seller.notifyObserver(seller.getUsername() + " has put up " + gameName + " for sale at " + price + " credits");
        }
    }
}
