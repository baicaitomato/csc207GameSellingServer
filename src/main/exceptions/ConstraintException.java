package src.main.exceptions;

/**
 * This abstract class represents any of the the constraints outlined in the
 * README.md instructions of this assignment.
 */
public abstract class ConstraintException extends Exception {

    /**
     * Initializes a ConstraintException
     * @param message the message that caused this exception.
     */
    public ConstraintException(String message) {
        super("CONSTRAINT ERROR: " + message);
    }

}
