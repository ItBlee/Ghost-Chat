package com.itblee.core;

import com.itblee.core.Impl.UserSession;

import java.util.Optional;
import java.util.UUID;

public class ServerHelper {

    private static final Server SERVER = Server.getInstance();

    private ServerHelper() {
        throw new AssertionError();
    }

    public static void addSession(UserSession session) {
        SERVER.getSessionManager().addSession(session);
    }

    public static boolean isActiveSession(UserSession session) {
        return SERVER.getSessionManager().isActive(session);
    }

    public static boolean containName(String username) {
        return SERVER.getSessionManager().containName(username);
    }

    public static void removeSession(UUID uuid) {
        SERVER.getSessionManager().remove(uuid);
    }

    public static Optional<UserSession> getSession(UUID uuid) {
        return SERVER.getSessionManager().get(uuid);
    }

    public static void removeFromQueue(UUID userId) {
        getChatManager().quitQueue(userId);
    }

    public static ChatManager getChatManager() {
        return SERVER.getChatManager();
    }

}
