package com.itblee.core;

import com.itblee.core.server.SessionManager;
import com.itblee.exception.UnauthenticatedException;
import com.itblee.security.Session;

import java.util.*;

public class UserManager extends SessionManager {

    private final Map<String, User> authenticatedMapByName = Collections.synchronizedMap(new HashMap<>());
    private final Map<UUID, User> authenticatedMapBySession = Collections.synchronizedMap(new HashMap<>());

    public User authenticate(UUID sessionId, String username) {
        Session session = get(sessionId).orElseThrow(IllegalStateException::new);
        User user = new User(session);
        user.setUsername(username);
        authenticatedMapByName.put(username, user);
        authenticatedMapBySession.put(sessionId, user);
        return user;
    }

    public User unauthenticate(User user) throws UnauthenticatedException {
        if (!authenticated(user.getUsername()))
            throw new UnauthenticatedException();
        authenticatedMapByName.remove(user.getUsername());
        authenticatedMapBySession.remove(user.getUid());
        user.setUsername(null);
        return user;
    }

    public boolean authenticated(UUID sessionId) {
        return authenticatedMapBySession.containsKey(sessionId);
    }

    public boolean authenticated(String username) {
        return authenticatedMapByName.containsKey(username);
    }

    public Optional<User> getUser(UUID sessionId) {
        return Optional.ofNullable(authenticatedMapBySession.get(sessionId));
    }

    public Optional<User> getUser(String username) {
        return Optional.ofNullable(authenticatedMapByName.get(username));
    }

    public List<User> getAuthenticated() {
        return new ArrayList<>(authenticatedMapByName.values());
    }

    public void clear() {
        super.clear();
        authenticatedMapByName.clear();
        authenticatedMapBySession.clear();
    }

    @Override
    public void remove(Session session) {
        super.remove(session);
        getUser(session.getUid()).ifPresent(u -> {
            try {
                unauthenticate(u);
            } catch (UnauthenticatedException ignored) {}
        });
    }


}