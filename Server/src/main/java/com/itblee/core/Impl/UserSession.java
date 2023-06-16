package com.itblee.core.Impl;

import com.itblee.core.ChatRoom;
import com.itblee.security.Certificate;
import com.itblee.security.User;

import java.io.Serializable;

public class UserSession extends User implements Serializable {
    private Status status;
    private long createdTime;
    private ChatRoom currentRoom;

    public enum Status {
        OFFLINE,
        ONLINE;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public UserSession(Certificate certificate) {
        super(certificate);
        this.status = Status.OFFLINE;
        this.createdTime = System.currentTimeMillis();
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public void renew() {
        createdTime = System.currentTimeMillis();
    }

    public void offline() {
        status = Status.OFFLINE;
    }

    public void online() {
        status = Status.ONLINE;
    }

    public boolean isOnline() {
        return status == Status.OFFLINE;
    }

    public Status getStatus() {
        return status;
    }

    public ChatRoom getRoom() {
        return currentRoom;
    }

    public void joinRoom(ChatRoom currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void leaveRoom() {
        currentRoom = null;
    }
}
