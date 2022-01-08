package src.main.exceptions;

/**
 * A class that represents constraint exceptions relating to a Users balance.
 */
public class BalanceException extends ConstraintException {

    /**
     * Initializes a BalanceException.
     * @param message the message that described why this exception was thrown.
     */
    public BalanceException(String message) {
        super(message);
    }
}
