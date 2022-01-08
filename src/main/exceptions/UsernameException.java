package src.main.exceptions;

/**
 * A constraint exception class relating to usernames of Users.
 * Can be caused by invalid usernames or taken usernames.
 */
public class UsernameException extends ConstraintException {

    /**
     * Initializes a UsernameException
     * @param message the reason why this exception was created.
     */
    public UsernameException(String message) {
        super(message);
    }
}
