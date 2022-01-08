package src.main.command;

import src.main.exceptions.ConstraintException;
import src.main.exceptions.UserAccessException;
import src.main.system.DistributionSystem;
import src.main.users.AdminUser;
import src.main.users.User;

/**
 * A class representing a Refund command.
 */
public class RefundCommand implements Command {
    DistributionSystem receiver;
    String buyerUsername;
    String sellerUsername;
    double credit;

    /**
     * Initializes a Refund command
     * @param system            the DistributionSystem which grants access to the store front
     * @param buyerUsername     the username of the buyer who will be refunded
     * @param sellerUsername    the username of the seller who will provide the refund credit
     * @param credit            the amount to refund
     */
    public RefundCommand(DistributionSystem system, String buyerUsername, String sellerUsername, String credit) {
        receiver = system;
        this.buyerUsername = buyerUsername.stripTrailing();
        this.sellerUsername = sellerUsername.stripTrailing();
        this.credit = Double.parseDouble(credit);
    }

    /**
     * execute (ie run) this Refund command.
     * @throws ConstraintException if the current user is not an Admin user
     */
    public void execute() throws ConstraintException {
        User user = receiver.getCurrUser();
        if (user instanceof AdminUser) {
            ((AdminUser) user).refund(this.buyerUsername, this.sellerUsername, this.credit);
        } else {
            throw new UserAccessException("Only AdminUser can make a refund");
        }
    }
}
