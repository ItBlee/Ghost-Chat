package exception;

public class InvalidPasswordException extends ChatAppException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
