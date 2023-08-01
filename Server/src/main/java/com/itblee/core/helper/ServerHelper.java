package com.itblee.core.helper;

import com.itblee.core.*;
import com.itblee.core.Impl.UserSession;

import java.util.Optional;
import java.util.UUID;

public final class ServerHelper {

    private static final Server SERVER = Server.getInstance();

    private ServerHelper() {
        throw new AssertionError();
    }

    public static void addSession(UserSession session) {
        SERVER.getUserManager().add(session);
    }

    public static Optional<UserSession> getSession(UUID uuid) {
        return SERVER.getUserManager().get(uuid);
    }

    public static Optional<UserSession> getUser(String username) {
        return SERVER.getUserManager().get(username);
    }

    public static boolean removeFromQueue(UUID userId) {
        return getChatManager().quitQueue(userId);
    }

    public static ChatManager getChatManager() {
        return SERVER.getChatManager();
    }

    public static ServerService getService() {
        return SERVER.getService();
    }

    public static UserManager getUserManager() {
        return SERVER.getUserManager();
    }

    public static Logger getLogger() {
        return SERVER.getLogger();
    }
}
