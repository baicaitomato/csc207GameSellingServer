package src.main.command;

import src.main.exceptions.ConstraintException;
import src.main.system.DistributionSystem;
import src.main.users.AdminUser;
import src.main.users.User;

/**
 * A class representing a Gift command.
 */
public class GiftCommand implements Command {
    DistributionSystem receiver;
    String gameName;
    String ownerName;
    String receiverName;

    /**
     * Initializes a Gift command
     * @param system        the DistributionSystem which grants access to the store front
     * @param gameName      the name of the game that will be gifted
     * @param ownerName     the name of the game's owner
     * @param receiverName  the name of the user receiving the gift
     */
    public GiftCommand(DistributionSystem system, String gameName, String ownerName, String receiverName) {
        receiver = system;
        this.gameName = gameName.stripTrailing();
        this.ownerName = ownerName.stripTrailing();
        this.receiverName = receiverName.stripTrailing();
    }

    /**
     * execute (ie run) this Gift command.
     * @throws ConstraintException if the game cannot be gifted
     */
    public void execute() throws ConstraintException {
        User user = receiver.getCurrUser();
        if (user instanceof AdminUser) {
            ((AdminUser) user).gift(this.ownerName, this.receiverName, this.gameName);
        }
        else {
            user.gift(this.receiverName, this.gameName);
        }
    }
}
