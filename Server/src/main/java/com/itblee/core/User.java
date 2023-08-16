package com.itblee.core;

import com.itblee.security.Session;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class User implements Serializable {
    private Session session;
    private String username;
    private Status status;
    private ChatRoom currentRoom;
    private Date createdDate;

    public User(Session session) {
        this.session = session;
        status = Status.OFFLINE;
    }

    public enum Status {
        BANNED,
        OFFLINE,
        ONLINE;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public String getIp() {
        return session.getWorker() != null ? session.getWorker().getIp() : null;
    }

    public void offline() {
        status = Status.OFFLINE;
    }

    public void online() {
        status = Status.ONLINE;
    }

    public void ban() {
        status = Status.BANNED;
    }

    public boolean isOnline() {
        return status == Status.ONLINE;
    }

    public boolean isBanned() {
        return status == Status.BANNED;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public UUID getUid() {
        return session.getUid();
    }

    public void setUid(String uid) {
        session.setUid(UUID.fromString(uid));
    }

    public void setUid(UUID uid) {
        session.setUid(uid);
    }

    public Worker getWorker() {
        return session.getWorker();
    }

    public void setWorker(Worker worker) {
        session.setWorker(worker);
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
