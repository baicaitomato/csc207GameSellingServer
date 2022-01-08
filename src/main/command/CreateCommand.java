package src.main.command;

import src.main.exceptions.ConstraintException;
import src.main.exceptions.FatalException;
import src.main.exceptions.UserAccessException;
import src.main.system.DistributionSystem;
import src.main.users.AdminUser;
import src.main.users.User;

/**
 * A class representing a Create command.
 */
public class CreateCommand implements Command{
    DistributionSystem receiver;
    String username;
    String type;
    double credit;

    /**
     * Initializes a Create command
     * @param system   the DistributionSystem which grants access to the store front
     * @param username the username of the user to be created
     * @param type     the user type of the user
     * @param credit   the initial balance of this user
     */
    public CreateCommand(DistributionSystem system, String username, String type, String credit) {
        receiver = system;
        this.username = username.stripTrailing();
        this.type = type;
        this.credit = Double.parseDouble(credit);
    }

    /**
     * execute (ie run) this Create command. Only an Admin user can create another user (including other admin users).
     * @throws ConstraintException if current user is not an Admin user
     */
    public void execute() throws ConstraintException{
        User user = receiver.getCurrUser();
        if (user instanceof AdminUser) {
            ((AdminUser) user).create(this.username, this.type, this.credit);
        } else {
            throw new UserAccessException("Only AdminUser can create User");
        }
    }
}
