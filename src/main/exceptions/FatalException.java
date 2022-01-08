package src.main.exceptions;

/**
 * This class relates to exceptions caused by invalid transaction codes.
 */
public class FatalException extends Exception {

    /**
     * Initializes a FatalException.
     * @param message the message that describes why this FatalException occurred.
     */
    public FatalException(String message) {
        super("FATAL ERROR: " + message);
    }
}
