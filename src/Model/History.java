package Model;

/**
 * Object lịch sử
 */
public class History {
    private final String sender;
    private final String message;
    private final String sentDate;

    public History(String sender, String message, String sentDate) {
        this.sender = sender;
        this.message = message;
        this.sentDate = sentDate;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSentDate() {
        return sentDate;
    }
}
