package exception;

public class UserExistException extends ChatAppException {
    public UserExistException(String message) {
        super(message);
    }
}
