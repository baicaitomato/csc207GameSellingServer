package src.main.command;

import src.main.exceptions.ConstraintException;
import src.main.exceptions.UserAccessException;
import src.main.exceptions.UsernameException;
import src.main.system.DistributionSystem;
import src.main.users.*;

/**
 * A class representing a sell command.
 */
public class SellCommand implements Command{
    DistributionSystem receiver;
    String gameName;
    String sellerUsername;
    Double discount;
    Double price;

    /**
     * Initializes a sell command
     * @param system the DistributionSystem which grants access to the store front
     * @param gameName the name of the game to put up for sale
     * @param sellerUsername the name of the seller
     * @param discount the discount to apply to the game
     * @param price the price of the game
     *
     */
    public SellCommand(DistributionSystem system, String gameName, String sellerUsername, String discount, String price) {
        receiver = system;
        this.gameName = gameName.stripTrailing();
        this.sellerUsername = sellerUsername.stripTrailing();
        this.discount = Double.parseDouble(discount);
        this.price = Double.parseDouble(price);
    }

    /**
     * execute (ie run) the sell command.
     * @throws ConstraintException if the current user is buy standard user
     */
    public void execute() throws ConstraintException {
            User user = receiver.getCurrUser();
            if (!this.sellerUsername.equals(user.getUsername())) {
                throw new UsernameException("The user cannot make others sell game!");
            }
            if (user instanceof SellStandardUser){
                ((SellStandardUser) user).sell(this.gameName, this.price, this.discount);
            } else if (user instanceof FullStandardUser){
                ((FullStandardUser) user).sell(this.gameName, this.price, this.discount);
            } else {
                throw new UserAccessException("A BuyStanderUSer cannot do sell");
            }
    }
}
