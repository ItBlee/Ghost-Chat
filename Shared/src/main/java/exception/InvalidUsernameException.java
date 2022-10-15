package exception;

public class InvalidUsernameException extends ChatAppException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
