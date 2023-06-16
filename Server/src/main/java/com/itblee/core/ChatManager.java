package com.itblee.core;

import com.itblee.core.Impl.UserSession;
import com.itblee.security.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatManager {

    private final Set<UUID> available = Collections.synchronizedSet(new LinkedHashSet<>());

    private final Set<ChatRoom> rooms = Collections.synchronizedSet(new HashSet<>());

    private final Map<UUID, Set<UUID>> rejectionMap = Collections.synchronizedMap(new HashMap<>());

    public Optional<UserSession> findFriendFor(UUID userId) {
        Queue<UUID> queue = new LinkedList<>(available);
        if (rejectionMap.containsKey(userId))
            queue.removeAll(rejectionMap.get(userId));
        UUID friendId = queue.poll();
        return ServerHelper.getSession(friendId);
    }

    public void reject(UUID userId, UUID rejectUserId) {
        if (!rejectionMap.containsKey(userId)) {
            Set<UUID> rejectionSet = new HashSet<>();
            rejectionSet.add(rejectUserId);
            rejectionMap.put(userId, rejectionSet);
            return;
        }
        rejectionMap.get(userId).add(rejectUserId);
    }

    public boolean registerRoom(ChatRoom room) {
        Stream<UserSession> stream = room.getMembers().stream();
        synchronized (available) {
            if (stream.anyMatch(user -> !available.contains(user.getUid()) && !user.isOnline()))
                return false;
            Set<UUID> memberIds = stream.map(User::getUid).collect(Collectors.toSet());
            available.removeAll(memberIds);
        }
        stream.forEach(user -> user.joinRoom(room));
        rooms.add(room);
        return true;
    }

    public Optional<ChatRoom> leaveRoom(UserSession user) {
        ChatRoom room = user.getRoom();
        user.leaveRoom();
        room.remove(user);
        if (rooms.contains(room)) {
            if (!room.isEmpty())
                return Optional.of(room);
            rooms.remove(room);
        }
        return Optional.empty();
    }

    public void joinQueue(UUID userId) {
        available.add(userId);
    }

    public void quitQueue(UUID userId) {
        available.remove(userId);
    }

}
