package src.main.exceptions;

/**
 * A class that represents constraint exceptions relating to adding too many credits
 * in a day/session.
 */
public class DailyCreditLimitException extends ConstraintException {

    /**
     * Initializes a DailyCreditLimitException
     * @param message the message that describes why this exception was created.
     */
    public DailyCreditLimitException(String message) {
        super(message);
    }
}
