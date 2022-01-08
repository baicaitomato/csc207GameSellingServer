package src.main.command;

import src.main.exceptions.ConstraintException;
import src.main.exceptions.UserAccessException;
import src.main.system.DistributionSystem;
import src.main.users.*;

/**
 * A class representing a Delete command.
 */
public class DeleteCommand implements Command{
    DistributionSystem receiver;
    String username;

    /**
     * Initializes a Delete command
     * @param system    the DistributionSystem which grants access to the store front
     * @param username  the username of the user to be deleted
     */
    public DeleteCommand(DistributionSystem system, String username) {
        receiver = system;
        this.username = username.stripTrailing();
    }

    /**
     * execute (ie run) this Delete Command.
     * @throws ConstraintException if the current user is not an Admin user
     */
    public void execute() throws ConstraintException {
        User user = receiver.getCurrUser();
        if (user instanceof AdminUser) {
            ((AdminUser) user).delete(this.username);
        } else {
            throw new UserAccessException("Only AdminUser can delete User");
        }
    }

}
