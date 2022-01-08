package src.main.system;

import src.main.exceptions.*;
import src.main.users.User;

/**
 * A system that keeps track of users logging in and out.
 */
public class DistributionSystem {
    private User currUser;

    /**
     * Initializes a DistributionSystem
     */
    public DistributionSystem() {
        currUser = null;
    }

    /**
     * @return the User that is currently logged in and null if no user is logged in.
     *
     */
    public User getCurrUser() {
        //The case there is a user not in database logging in should get in Client, since we need to ignore all transaction code after
        return currUser;
    }

    /**
     * @return true if there is a user currently logged in and false otherwise.
     *
     */
    public boolean isCurrLogin() {
        return currUser != null;
    }

    /**
     * Logs in a user given the username, account type, and balance.
     * @param username the username of the account being signed into
     * @param type the type of the account being signed into: one of AA, BS, FS, SS
     * @param credit the balance of the users account.
     * @throws UsernameException when a user with the given username does not exist.
     */
    public void login(String username, String type, double credit) throws UsernameException {

        if (isCurrLogin()) {
            throw new UsernameException("There has been a user logged in."); // Do we throw fatal or constraint?
        }
        //Here is the way we get user in database
        if (!User.getAllUsers().containsKey(username)) {
            throw new UsernameException("User '" + username + "' does not exist, will jump to next login request.");
        }
        currUser = User.getAllUsers().get(username);
        currUser.notifyObserver(currUser.getUsername() + " has logged in.");
        if (!currUser.getAccountType().equals(type)) {
            System.out.println(username + ": The user type is not matching! The login transactions will still execute.");
        }
        if (!String.format("%.2f", currUser.getBalance()).equals(String.format("%.2f", credit))){
            System.out.println(username + ": The user balance is not matching! The login transactions will still execute.");
        }
    }

    /**
     * Logs out the user currently logged in.
     * @throws UsernameException when there is no user currently logged in.
     */
    public void logout() throws UsernameException {
        if (isCurrLogin()) {
            currUser.notifyObserver(currUser.getUsername() + " is logging out.");
            currUser = null;
        } else {
            throw new UsernameException("There is no user logged in.");
        }

    }
}
