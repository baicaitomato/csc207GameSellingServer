package src.main.command;

import src.main.exceptions.UsernameException;
import src.main.system.DistributionSystem;

/**
 * A class representing a Logout command.
 */
public class LogoutCommand implements Command{
    DistributionSystem receiver;

    /**
     * Initializes a Logout command.
     * @param system    the DistributionSystem which grants access to the store front
     */
    public LogoutCommand(DistributionSystem system) {
        receiver = system;
    }

    /**
     * execute (ie run) this Logout command.
     * @throws UsernameException if no user is currently logged in
     */
    public void execute() throws UsernameException {
        receiver.logout();
    }
}
