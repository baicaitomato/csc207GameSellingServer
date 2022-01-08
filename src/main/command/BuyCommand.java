package src.main.command;

import src.main.exceptions.ConstraintException;
import src.main.exceptions.UserAccessException;
import src.main.exceptions.UsernameException;
import src.main.system.DistributionSystem;
import src.main.users.BuyStandardUser;
import src.main.users.FullStandardUser;
import src.main.users.SellStandardUser;
import src.main.users.User;

/**
 * A class representing a buy command.
 */
public class BuyCommand implements Command {
    DistributionSystem receiver;
    String gameName;
    String sellerUsername;
    String buyerUsername;

    /**
     * Initializes a sell command
     * @param system the DistributionSystem which grants access to the store front
     * @param gameName the name of the game to put up for sale
     * @param sellerUsername the name of the game's seller
     * @param buyerUsername the name of the buyer
     *
     */
    public BuyCommand(DistributionSystem system, String gameName, String sellerUsername, String buyerUsername) {
        receiver = system;
        this.gameName = gameName.stripTrailing();
        this.sellerUsername = sellerUsername.stripTrailing();
        this.buyerUsername = buyerUsername.stripTrailing();
    }

    /**
     * execute (ie run) this buy command.
     * @throws ConstraintException if the current user is sell standard user
     */
    public void execute() throws ConstraintException {
        User user = receiver.getCurrUser();
        if (!this.buyerUsername.equals(user.getUsername())) {
            throw new UsernameException("The user cannot make others buy game!");
        }
        if (user instanceof BuyStandardUser){
            ((BuyStandardUser) user).buy(this.sellerUsername, this.gameName);
        } else if (user instanceof FullStandardUser){
            ((FullStandardUser) user).buy(this.sellerUsername, this.gameName);
        } else {
            throw new UserAccessException("A SellStandardUser cannot do buy");
        }
    }
}
