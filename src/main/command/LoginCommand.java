package src.main.command;

import src.main.exceptions.BalanceException;
import src.main.exceptions.UsernameException;
import src.main.system.DistributionSystem;

/**
 * A class representing a Login command.
 */
public class LoginCommand implements Command{
    DistributionSystem receiver;
    String username;
    String type;
    double credit;

    /**
     * Initializes a Login command
     * @param system        the DistributionSystem which grants access to the store front
     * @param username      the username of the user to be logged in
     * @param type          the usertype of the user
     * @param credit        the balance of the user
     */
    public LoginCommand(DistributionSystem system, String username, String type, String credit) {
        receiver = system;
        this.username = username.stripTrailing();
        this.type = type;
        this.credit = Double.parseDouble(credit);
    }

    /**
     * execute (ie run) this Login command.
     * @throws UsernameException if the user does not exist in the user database
     */
    public void execute() throws UsernameException {
        receiver.login(this.username, this.type, this.credit);
    }
}
