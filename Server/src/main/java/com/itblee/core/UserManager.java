package com.itblee.core;

import com.itblee.core.Impl.UserSession;
import com.itblee.core.helper.ServerHelper;
import com.itblee.exception.UnauthenticatedException;
import com.itblee.repository.document.Log;
import com.itblee.utils.PropertyUtil;

import java.util.*;
import java.util.stream.Collectors;

public class UserManager extends Thread {

    private final Map<UUID, UserSession> sessionMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, UserSession> authenticatedMap = Collections.synchronizedMap(new HashMap<>());

    private static final long TIMEOUT = PropertyUtil.getInt("session.timeout");

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        while (!interrupted()) {
            try {
                Thread.sleep(minSessionTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            long current = System.currentTimeMillis();
            for (UserSession session : sessionMap.values()) {
                if (current - session.getLatestAccessTime() >= TIMEOUT) {
                    ServerHelper.getService().removeSession(session);
                    ServerHelper.getLogger().log(session);
                }
            }
        }
    }

    private long minSessionTime() {
        if (sessionMap.isEmpty())
            return TIMEOUT;
        long min = TIMEOUT;
        long currentTime = System.currentTimeMillis();
        for (UserSession session : sessionMap.values()) {
            long time = currentTime - (session.getLatestAccessTime() + TIMEOUT);
            if (time < 0)
                continue;
            if (time < min)
                min = time;
        }
        return min;
    }

    public void add(UserSession session) {
        sessionMap.put(session.getUid(), session);
    }

    public Optional<UserSession> get(UUID sessionId) {
        return Optional.ofNullable(sessionMap.get(sessionId));
    }

    public Optional<UserSession> get(String username) {
        return Optional.ofNullable(authenticatedMap.get(username));
    }

    public List<UserSession> getSessions() {
        return new ArrayList<>(sessionMap.values());
    }

    public List<UserSession> getAuthenticated() {
        return new ArrayList<>(authenticatedMap.values());
    }

    public boolean contain(UUID sessionId) {
        return sessionMap.containsKey(sessionId);
    }

    public void clear() {
        sessionMap.clear();
        authenticatedMap.clear();
    }

    public void remove(UUID sessionId) {
        remove(sessionMap.get(sessionId));
    }

    public void remove(UserSession session) {
        try {
            unauthenticate(session.getUid());
        } catch (UnauthenticatedException ignored) {}
        sessionMap.remove(session.getUid());
    }

    public UserSession authenticate(UUID sessionId, String username) {
        UserSession session = get(sessionId)
                .orElseThrow(IllegalStateException::new);
        session.setUsername(username);
        authenticatedMap.put(username, session);
        return session;
    }

    public UserSession unauthenticate(UUID sessionId) throws UnauthenticatedException {
        UserSession session = get(sessionId)
                .orElseThrow(IllegalStateException::new);
        if (!authenticated(session.getUsername()))
            throw new UnauthenticatedException();
        authenticatedMap.remove(session.getUsername());
        session.setUsername(null);
        return session;
    }
    
    public boolean authenticated(String username) {
        return authenticatedMap.containsKey(username);
    }

}