package exception;

public class InvalidStatusException extends ChatAppException {
    public InvalidStatusException(String message) {
        super(message);
    }
}
