package src.main;

import src.main.exceptions.InvalidGameException;
import src.main.users.AdminUser;

import java.io.Serializable;

/**
 * A class that represents a video game in this backend system and database.
 * Contains information such as the game name, the price, and discount.
 *
 * ==Representation Invariants==
 *  - 0 < name.length < MAXIMUM_NAME_LENGTH
 *  - 0 <= originalPrice <= MAXIMUM_PRICE
 *  - 0 <= discount <= MAXIMUM_SALE_DISCOUNT
 *  - seller is a username that belongs to an existing non-BuyStandardUser
 */
public class Game implements Serializable {

    private final String name;
    private final String seller;
    private final double originalPrice;
    private final double discountedPrice;
    private final double discount;
    private boolean offProbation = false;

    private final static double MAXIMUM_PRICE = 999.99;
    public final static int MAXIMUM_NAME_LENGTH = 25;
    private final static double MAXIMUM_SALE_DISCOUNT = 90.00;

    public final static String INVALID_PRICE_ERROR_MSG = "Game price must be less than " + MAXIMUM_PRICE + ". ";
    public final static String INVALID_GAME_NAME_ERROR_MSG = "Game name must be 25 characters or less. ";
    public final static String INVALID_DISCOUNT_ERROR_MSG = "Sales discount must be no more than 90%.";

    /**
     * Initializes a Game
     * @param name the name of the game
     * @param seller the username of the person that is selling or sold this game.
     * @param originalPrice the price of the game when no discount is active
     * @param discount the discount that can be applied to this game.
     * @throws InvalidGameException If any of the representation invariants are not followed
     */
    public Game(String name, String seller, double originalPrice, double discount) throws InvalidGameException {
        if (originalPrice <= MAXIMUM_PRICE && name.length() <= MAXIMUM_NAME_LENGTH && discount <= MAXIMUM_SALE_DISCOUNT) {
            this.name = name;
            this.originalPrice = originalPrice;
            this.discount = discount;
            this.discountedPrice = this.originalPrice * (1 - this.discount / 100);
            this.seller = seller;
        }  else {
            String errorMsg = "";

            if (originalPrice > MAXIMUM_PRICE || originalPrice < 0) {
                errorMsg += INVALID_PRICE_ERROR_MSG;
            }
            if (name.length() > MAXIMUM_NAME_LENGTH) {
                errorMsg += INVALID_GAME_NAME_ERROR_MSG;
            }
            if (discount > MAXIMUM_SALE_DISCOUNT || discount < 0) {
                errorMsg += INVALID_DISCOUNT_ERROR_MSG;
            }
            throw new InvalidGameException(errorMsg.strip());
        }
    }

    /**
     * @return the name of this game
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the username of the person that is selling/sold this game.
     */
    public String getSeller() {
        return this.seller;
    }

    /**
     * @return the price of this game; can be discounted if an auction sale is occurring.
     */
    public double getPrice() {
        if (AdminUser.isAuction()) {
            return this.discountedPrice;
        } else {
            return this.originalPrice;
        }
    }

    /**
     * A function that makes it possible for other users to act with this game.
     *
     * Act defined as:
     * - buying this game
     * - gifting this game
     * - removing this game
     *
     */
    public void putOffProbation() {
        this.offProbation = true;
    }

    /**
     * Returns if this game is sellable.
     * @return true if the game is sellable and false if the game is unsellable.
     */
    public boolean isOffProbation() {
        return this.offProbation;
    }

    public Game getCopy() {
        try {
            return new Game(this.name, this.seller, this.originalPrice, this.discount);
        } catch (InvalidGameException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return "Title: " + this.getName() + "\nPrice: " + this.getPrice() + "\nSeller: " + this.getSeller();
    }
}
