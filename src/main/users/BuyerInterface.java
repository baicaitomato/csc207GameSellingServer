package src.main.users;

import src.main.Game;
import src.main.exceptions.ConstraintException;
import src.main.exceptions.BalanceException;
import src.main.exceptions.InvalidGameException;
import src.main.exceptions.UsernameException;

/**
 * An interface that defines the behavior for any User that is able to buy games from other users.
 */
public interface BuyerInterface {

    /**
     * Buyer the specified game from the specified seller
     *
     * Constraints:
     *      - The buyer and seller accounts must be the appropriate type.
     *      - Buyer cannot buy from themselves.
     *      - The buyer doesn't already own the game of the same name.
     *      - The seller exists.
     *      - The seller must carry the game.
     *      - The buyer must have sufficient funds to purchase the game.
     *
     * @param buyer the buyer
     * @param sellerName the name of the seller to buy from
     * @param gameName the name of the game to buy
     * @throws ConstraintException if any of the constraints are violated.
     */
    default void buy(User buyer, String sellerName, String gameName) throws ConstraintException {
        canBuy(buyer, sellerName, gameName);

        User seller = User.getAllUsers().get(sellerName);
        Game game = seller.getGame(gameName).getCopy();

        // Performing transaction: exchange of funds and game
        buyer.setBalance(buyer.getBalance() - game.getPrice());
        buyer.addToGameLibrary(game);
        seller.setBalance(seller.getBalance() + game.getPrice());
        buyer.notifyObserver(buyer.getUsername() + " bought the game '" + gameName + "' from " + sellerName + " for " + game.getPrice());
    }

    /**
     * Verifies that all the constraints are being followed and raises an exception if any constraints
     * are violated.
     * @param buyer the buyer in this buy transaction
     * @param sellerName the name of the seller in this buy transaction
     * @param gameName the name of the game.
     * @throws ConstraintException if any of the constraints outlined in buy are violated.
     */
    private void canBuy(User buyer, String sellerName, String gameName) throws ConstraintException {
        if (buyer.ownsGame(gameName)) {
            throw new InvalidGameException(buyer.getUsername() + " already owns the game '" + gameName + "'.");
        }

        if (!User.userExists(sellerName)) {
            throw new UsernameException("User '" + sellerName + "' does not exist.");
        }

        Game sellersGame = User.getAllUsers().get(sellerName).getGame(gameName);

        sellerCarriesGame(sellerName, gameName);

         if (!buyer.hasEnoughCredits(sellersGame)) {
             throw new BalanceException(buyer.getUsername() + " does not have sufficient funds to purchase '" + sellersGame.getName() + "'");
         }

         if (!sellersGame.isOffProbation()) {
             throw new InvalidGameException("'" + gameName + "' is not available to purchase until the following day");
         }
    }

    private void sellerCarriesGame(String sellerName, String gameName) throws InvalidGameException {
        User seller = User.getAllUsers().get(sellerName);
        Game sellersGame = seller.getGame(gameName);
        // Seller isn't selling the game.
        if (sellersGame == null || !sellersGame.getSeller().equals(seller.getUsername())) {
            throw new InvalidGameException("Seller does not have '" + gameName + "' for sale.");
        }
    }
}
