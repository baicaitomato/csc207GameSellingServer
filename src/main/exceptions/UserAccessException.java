package src.main.exceptions;

/**
 * A constraint exception class that relates to a User attempting to access
 * behaviors that they don't have access to.
 * Example: A non-admin using attempting to start an auction sale.
 */
public class UserAccessException extends ConstraintException {

    /**
     * Initializes a UserAccessException
     * @param message describes what occurred to cause this exception.
     */
    public UserAccessException(String message) {
        super(message);
    }
}
