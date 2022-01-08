package src.main.command;

import src.main.exceptions.ConstraintException;
import src.main.exceptions.UserAccessException;
import src.main.system.DistributionSystem;
import src.main.users.AdminUser;
import src.main.users.User;

/**
 * A class representing an auction sale command.
 */
public class AuctionsaleCommand implements Command{
    DistributionSystem receiver;
    String username;

    /**
     * Initializes an auction sale command
     * @param system the DistributionSystem which grants access to the store front
     * @param userName the username of the user that requested this auctionsale
     *
     */
    public AuctionsaleCommand(DistributionSystem system, String userName) {
        receiver = system;
        username = userName;
    }

    /**
     * execute (ie run) this auction sale command.
     * @throws ConstraintException if the current user is not an admin user
     */
    public void execute() throws ConstraintException {
        User user = receiver.getCurrUser();

        if (user instanceof AdminUser) {
            if (!username.equals(user.getUsername())) {
                System.out.println("Warning: The username given for this auction sale is not the same as the one currently logged in" +
                        "Will continue to toggle the auction sale.");
            }
            ((AdminUser) user).auctionSale();
        } else {
            throw new UserAccessException("Only AdminUser can make a auctionsale");
        }
    }
}
