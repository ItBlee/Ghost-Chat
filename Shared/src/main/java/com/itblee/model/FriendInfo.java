package com.itblee.model;

import java.io.Serializable;

public class FriendInfo implements Serializable {
    private String name;
    private String status;
    private String avatar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Friend Info:\n" +
                "name: " + name + "\n" +
                "status: " + status + "\n";
    }
}
