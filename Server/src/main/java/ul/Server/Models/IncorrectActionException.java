package ul.Server.Models;

public class IncorrectActionException extends Exception {
    public IncorrectActionException() {
        super("Incorrect action event");
    }

    public IncorrectActionException(String message) {
        super(message);
    }
}
