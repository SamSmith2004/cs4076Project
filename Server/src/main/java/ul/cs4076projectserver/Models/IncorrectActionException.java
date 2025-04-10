package ul.cs4076projectserver.Models;

/**
 * The {@code IncorrectActionException} class represents an exception that is
 * thrown when an incorrect or unknown action is performed by the client.
 */
public class IncorrectActionException extends Exception {
    /**
     * Constructs a new {@code IncorrectActionException} with a default error
     * message.
     */
    public IncorrectActionException() {
        super("Incorrect action event");
    }

    /**
     * Constructs a new {@code IncorrectActionException} with the specified detail
     * message.
     * 
     * @param message The detail message.
     */
    public IncorrectActionException(String message) {
        super(message);
    }
}