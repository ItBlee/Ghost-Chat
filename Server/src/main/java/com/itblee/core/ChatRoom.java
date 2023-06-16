package com.itblee.core;

import com.itblee.core.Impl.UserSession;

import java.util.*;

public class ChatRoom {
    private final UUID roomId;
    private final Set<UserSession> members;

    public ChatRoom() {
        roomId = UUID.randomUUID();
        members = Collections.synchronizedSet(new HashSet<>());
    }

    public void add(UserSession user) {
        members.add(user);
    }

    public void addAll(Collection<UserSession> users) {
        members.addAll(users);
    }

    public void remove(UserSession user) {
        members.remove(user);
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public UUID getId() {
        return roomId;
    }

    public Set<UserSession> getMembers() {
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
