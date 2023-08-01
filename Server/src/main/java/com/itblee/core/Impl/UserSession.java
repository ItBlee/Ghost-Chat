package com.itblee.core.Impl;

import com.itblee.core.ChatRoom;
import com.itblee.repository.document.Log;
import com.itblee.security.User;

import java.io.Serializable;
import java.util.*;

public class UserSession extends User implements Serializable {
    private final Map<String, List<Log>> logMap;
    private Status status;
    private Long latestAccessTime;
    private ChatRoom currentRoom;

    public UserSession() {
        logMap = Collections.synchronizedMap(new HashMap<>());
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

    public Long getLatestAccessTime() {
        return latestAccessTime;
    }

    public String getIp() {
        return getWorker() != null ? getWorker().getIp() : null;
    }

    public void resetTimer() {
        latestAccessTime = System.currentTimeMillis();
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

    public void log(Log log) {
        logMap.computeIfAbsent(log.getUsername(), s -> new ArrayList<>())
                .add(log);
    }

    public Map<String, List<Log>> getLogs() {
        return logMap;
    }

    public List<Log> getLogs(String username) {
        return logMap.get(username);
    }
}
