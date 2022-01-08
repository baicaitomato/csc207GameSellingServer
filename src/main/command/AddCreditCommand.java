package src.main.command;

import src.main.exceptions.ConstraintException;
import src.main.exceptions.FatalException;
import src.main.system.DistributionSystem;
import src.main.users.AdminUser;
import src.main.users.User;

/**
 * A class representing an AddCredit command.
 */
public class AddCreditCommand implements Command{
    DistributionSystem receiver;
    String username;
    Double credit;

    /**
     * Initializes an AddCredit command
     * @param system   the DistributionSystem which grants access to the store front
     * @param username the username of the user to add this credit
     * @param credit   the credit to add
     */
    public AddCreditCommand(DistributionSystem system, String username, String credit) {
        receiver = system;
        this.username = username.stripTrailing();
        this.credit = Double.parseDouble(credit);
    }

    /**
     * execute (ie run) this AddCredit command.
     * @throws ConstraintException if the credits cannot be added or the username (in admin mode) does not exist
     * in the user database
     */
    public void execute() throws ConstraintException {
        User user = receiver.getCurrUser();
        if (user instanceof AdminUser) {
            ((AdminUser) user).addCredit(this.username, this.credit);
        } else {
            user.addCredit(this.credit);
        }
    }
}
