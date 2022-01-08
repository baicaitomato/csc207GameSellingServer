package src.main.exceptions;

/**
 * A constrain exception class that relates to invalid Games:
 *      - Games that don't exist
 *      - Games created with invalid names, prices, or discounts
 */
public class InvalidGameException extends ConstraintException {

    /**
     * Initializes an InvalidGameException
     * @param message the message relating to this exception.
     */
    public InvalidGameException(String message) {
        super(message);
    }
}
