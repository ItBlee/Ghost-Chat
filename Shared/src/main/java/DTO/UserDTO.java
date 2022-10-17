package DTO;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private String uid;
    private String username;

    public UserDTO() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
