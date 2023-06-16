package com.itblee.core;

import com.itblee.core.Impl.UserSession;

import java.util.*;

import static com.itblee.constant.ServerConstant.SESSION_EXPIRED_TIME;

public class SessionManager {

    private final Map<UUID, UserSession> sessions = Collections.synchronizedMap(new HashMap<>());

    private final Set<String> usernames = Collections.synchronizedSet(new HashSet<>());

    public boolean isActive(UserSession session) {
        return sessions.containsValue(session)
                && System.currentTimeMillis() - session.getCreatedTime() <= SESSION_EXPIRED_TIME;
    }

    public void addSession(UserSession session) {
        sessions.put(session.getUid(), session);
        usernames.add(session.getUsername());
    }

    public boolean containName(String username) {
        return usernames.contains(username);
    }

    public Optional<UserSession> get(UUID uuid) {
        return Optional.ofNullable(sessions.get(uuid));
    }

    public void clear() {
        if (!sessions.isEmpty())
            sessions.clear();
    }

    public void remove(UUID uuid) {
        sessions.remove(uuid);
    }

}
