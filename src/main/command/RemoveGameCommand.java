package src.main.command;

import src.main.exceptions.ConstraintException;
import src.main.system.DistributionSystem;
import src.main.users.AdminUser;
import src.main.users.User;

/**
 * A class representing a RemoveGame command.
 */
public class RemoveGameCommand implements Command {
    DistributionSystem receiver;
    String gameName;
    String ownerName;

    /**
     * Initializes a RemoveGame command
     * @param system        the DistributionSystem which grants access to the store front
     * @param gameName      the name of the game to be removed
     * @param ownerName     the name of the game's owner
     */
    public RemoveGameCommand(DistributionSystem system, String gameName, String ownerName) {
        receiver = system;
        this.gameName = gameName.stripTrailing();
        this.ownerName = ownerName.stripTrailing();
    }

    /**
     * execute (ie run) this RemoveGame command
     * @throws ConstraintException if the current user is non-admin and attempts to remove a game they do not own, OR
     * a user attempts to remove a game that was purchased or put on sale the same day
     */
    public void execute() throws ConstraintException {
        User user = receiver.getCurrUser();
        if (user instanceof AdminUser) {
            ((AdminUser) user).removeGame(this.ownerName, this.gameName);
        }
        else {
            user.removeGame(this.gameName);
        }
    }
}
