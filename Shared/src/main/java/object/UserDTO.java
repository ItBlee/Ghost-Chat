package object;

import java.io.Serializable;
import java.util.UUID;

public class UserDTO implements Serializable {
    private UUID uid;
    private String username;

    public UserDTO() {
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
