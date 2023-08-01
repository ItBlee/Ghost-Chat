package com.itblee.core.helper;

import com.itblee.core.Impl.UserSession;
import com.itblee.repository.document.Log;
import com.itblee.security.User;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public final class LoggerHelper {

    private static final ThreadLocal<LogOwner> LOGGERS = ThreadLocal.withInitial(LogOwner::new);
    private static final String SERVER = "Server";

    private LoggerHelper() {
        throw new AssertionError();
    }

    public static Log get() {
        return LOGGERS.get().getLog();
    }

    public static LogOwner log(UserSession user) {
        return LOGGERS.get().setTarget(user);
    }

    public static Log breakConnect(String ip) {
        Log log = get();
        log.setContact(SERVER);
        log.setAction("Stop connect to Server");
        log.setDetail("IP: " + ip);
        return log;
    }

    public static Log login(String ip, String username) {
        Log log = get();
        log.setContact(SERVER);
        log.setAction("Access with individual account");
        log.setDetail("IP: " + ip + "\nUsername: " + username);
        return log;
    }

    public static Log register(String ip, String username) {
        Log log = get();
        log.setContact(SERVER);
        log.setAction("Create new individual account");
        log.setDetail("IP: " + ip + "\nUsername: " + username);
        return log;
    }

    public static Log logout() {
        Log log = get();
        log.setContact(SERVER);
        log.setAction("Logout");
        return log;
    }

    public static Log startFindChat() {
        Log log = get();
        log.setContact(SERVER);
        log.setAction("Start finding chat friend");
        return log;
    }

    public static Log stopFindChat() {
        Log log = get();
        log.setContact(SERVER);
        log.setAction("Stop finding chat friend");
        return log;
    }

    public static Log matchFriend(UUID friendId, String friendName) {
        Log log = get();
        log.setContact(friendName);
        log.setAction("Chatting with Friend");
        log.setDetail("Friend ID: " + friendId);
        return log;
    }

    public static Log leaveChat(UUID friendId, String friendName) {
        Log log = get();
        log.setContact(friendName);
        log.setAction("Stop chatting with Friend");
        log.setDetail("Friend ID: " + friendId);
        return log;
    }

    public static Log sendMessage(String receiver, String message) {
        Log log = get();
        log.setContact(receiver);
        log.setAction("Send message");
        log.setDetail("Message: " + message);
        return log;
    }

    public static Log receiveMessage(String sender, String message) {
        Log log = get();
        log.setContact(sender);
        log.setAction("Receive message");
        log.setDetail("Message: " + message);
        return log;
    }

    public static Log messageFromServer(String message) {
        Log log = get();
        log.setContact(SERVER);
        log.setAction("Receive alert");
        log.setDetail("Message: " + message);
        return log;
    }

    public static class LogOwner {
        private UserSession target;

        private LogOwner setTarget(UserSession target) {
            this.target = Objects.requireNonNull(target);
            return this;
        }

        private Log getLog() {
            /*log.setId(null);
            log.setCreatedDate(null);
            log.setModifiedDate(null);
            log.setUid(null);
            log.setUsername(null);
            log.setContact(null);
            log.setAction(null);
            log.setDetail(null);
            log.setStatus(null);*/
            return new Log();
        }

        public void log(Log log) {
            log.setUsername(target.getUsername());
            log.setUid(target.getUid().toString());
            log.setStatus(1);
            log.setCreatedDate(new Date());
            target.log(log);
        }

        public void breakConnect() {
            log(LoggerHelper.breakConnect(target.getWorker().getIp()));
        }

        public void login() {
            log(LoggerHelper.login(target.getWorker().getIp(), target.getUsername()));
        }

        public void register() {
            log(LoggerHelper.register(target.getWorker().getIp(), target.getUsername()));
        }

        public void logout() {
            log(LoggerHelper.logout());
        }

        public void startFindChat() {
            log(LoggerHelper.startFindChat());
        }

        public void stopFindChat() {
            log(LoggerHelper.stopFindChat());
        }

        public void matchFriend(UUID friendId, String friendName) {
            log(LoggerHelper.matchFriend(friendId, friendName));
        }

        public void leaveChat(UUID friendId, String friendName) {
            log(LoggerHelper.leaveChat(friendId, friendName));
        }

        public void sendMessage(String receiver, String message) {
            log(LoggerHelper.sendMessage(receiver, message));
        }

        public void receiveMessage(String sender, String message) {
            log(LoggerHelper.receiveMessage(sender, message));
        }

        public void messageFromServer(String message) {
            log(LoggerHelper.messageFromServer(message));
        }

    }
}
