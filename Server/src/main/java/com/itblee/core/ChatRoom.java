package com.itblee.core;

import java.util.*;

public class ChatRoom {
    private final UUID roomId;
    private final Set<User> members;

    public ChatRoom() {
        roomId = UUID.randomUUID();
        members = Collections.synchronizedSet(new HashSet<>());
    }

    public void add(User user) {
        members.add(user);
    }

    public void addAll(Collection<User> users) {
        members.addAll(users);
    }

    public void remove(User user) {
        members.remove(user);
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public UUID getId() {
        return roomId;
    }

    public Set<User> getMembers() {
        return members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoom)) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return roomId.equals(chatRoom.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
}
