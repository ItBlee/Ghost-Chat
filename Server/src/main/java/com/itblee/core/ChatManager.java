package com.itblee.core;

import java.util.*;

public class ChatManager {

    private final Set<UUID> available = Collections.synchronizedSet(new LinkedHashSet<>());

    private final Set<ChatRoom> rooms = Collections.synchronizedSet(new HashSet<>());

    private final Map<UUID, Set<UUID>> rejectionMap = Collections.synchronizedMap(new HashMap<>());

    public Optional<UUID> findFriendFor(UUID userId) {
        Queue<UUID> queue = new LinkedList<>(available);
        if (rejectionMap.containsKey(userId))
            queue.removeAll(rejectionMap.get(userId));
        UUID friendId;
        Set<UUID> rejectionSet;
        do {
            friendId = queue.poll();
            if (friendId == null)
                break;
            rejectionSet = rejectionMap.get(friendId);
        } while (rejectionSet != null && rejectionSet.contains(userId));
        return Optional.ofNullable(friendId);
    }

    public void reject(UUID userId, UUID rejectUserId) {
        rejectionMap.computeIfAbsent(userId, uuid -> new HashSet<>())
                .add(rejectUserId);
    }

    public boolean registerRoom(ChatRoom room) {
        Set<User> users = room.getMembers();
        synchronized (available) {
            for (User user : users) {
                if (!available.contains(user.getUid()) && !user.isOnline())
                    return false;
            }
            users.forEach(user -> {
                available.remove(user.getUid());
                user.joinRoom(room);
            });
        }
        rooms.add(room);
        return true;
    }

    public Optional<ChatRoom> leaveRoom(User user) {
        ChatRoom room = user.getRoom();
        if (room == null)
            return Optional.empty();
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

    public boolean quitQueue(UUID userId) {
        return available.remove(userId);
    }

    public boolean isInQueue(UUID userId) {
        return available.contains(userId);
    }

}
