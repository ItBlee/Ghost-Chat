package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Object chứa info bạn chat
 */
public class PairInfo {
    private String name;
    private String uid;
    private String status;
    private String modifiedDate;

    public PairInfo() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String toString() {
        String time = "";
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(modifiedDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            time = localDateTime.format(formatter);
        } catch (NullPointerException ignored) {}
        return "Name: " + name + "\n" +
                "UID: " + uid + "\n" +
                "Status: " + status + "\n" +
                "Join date: " + time;
    }
}
