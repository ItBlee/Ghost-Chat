package com.itblee.core;

import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Header;
import com.itblee.transfer.Packet;
import com.itblee.transfer.StatusCode;
import com.itblee.utils.JsonParser;
import com.itblee.utils.ObjectUtil;

import java.io.IOException;
import java.util.UUID;

public class TransferHelper {

    private static final ThreadLocal<Caller> CALLERS = ThreadLocal.withInitial(Caller::new);

    private TransferHelper() {
        throw new AssertionError();
    }

    public static Packet get() {
        return CALLERS.get().getPacket();
    }

    public static Caller call(Worker worker) {
        return CALLERS.get().setTarget(worker);
    }

    public static Packet responseNameRegister(boolean accept) {
        Packet packet = get();
        packet.setHeader(Header.REGISTER_NAME);
        packet.setCode(accept ? StatusCode.OK : StatusCode.CONFLICT);
        return packet;
    }

    public static Packet responseAuthRegister(UUID sessionId) {
        Packet packet = get();
        packet.setHeader(Header.AUTH_REGISTER);
        packet.putData(DataKey.SESSION_ID, sessionId);
        return packet;
    }

    public static Packet warnUnauthenticated() {
        Packet packet = get();
        packet.setHeader(Header.UNAUTHENTICATED);
        return packet;
    }

    public static Packet warnStopFindChat() {
        Packet packet = get();
        packet.setHeader(Header.STOP_FIND);
        return packet;
    }

    public static Packet askForInvitation(String friendName) {
        Packet packet = get();
        packet.setHeader(Header.INVITE_CHAT);
        packet.putData(DataKey.FRIEND_NAME, friendName);
        return packet;
    }

    public static Packet askForConfirmation(String userName) {
        Packet packet = get();
        packet.setHeader(Header.CONFIRM_CHAT);
        packet.putData(DataKey.FRIEND_NAME, userName);
        return packet;
    }

    public static Packet responseFriendOffline(String friendName) {
        Packet packet = get();
        packet.setHeader(Header.CHAT_INFO);
        packet.setCode(StatusCode.NOT_FOUND);
        packet.putData(DataKey.FRIEND_NAME, friendName);
        return packet;
    }

    public static Packet responseInvitationDeclined(String friendName) {
        Packet packet = get();
        packet.setHeader(Header.CHAT_INFO);
        packet.setCode(StatusCode.FORBIDDEN);
        packet.putData(DataKey.FRIEND_NAME, friendName);
        return packet;
    }

    public static Packet responseCreatedChat(FriendInfo info) {
        Packet packet = get();
        packet.setHeader(Header.CHAT_INFO);
        packet.setCode(StatusCode.CREATED);
        packet.putData(DataKey.FRIEND_INFO, info);
        return packet;
    }

    public static Packet sendChatMessage(Message message) {
        Packet packet = get();
        packet.setHeader(Header.RECEIVE_MESSAGE);
        packet.putData(DataKey.MESSAGE_BODY, message);
        return packet;
    }

    public static Packet responseCreatedMessage() {
        Packet packet = get();
        packet.setHeader(Header.SEND_MESSAGE);
        packet.setCode(StatusCode.CREATED);
        return packet;
    }

    public static Packet modifyFriendInfo(FriendInfo info) {
        Packet packet = get();
        packet.setHeader(Header.FRIEND_INFO);
        packet.putData(DataKey.FRIEND_INFO, info);
        return packet;
    }

    public static class Caller {
        private Worker target;
        private final Packet packet;

        public Caller() {
            packet = new Packet();
        }

        private Caller setTarget(Worker target) {
            this.target = ObjectUtil.requireNonNull(target);
            return this;
        }

        private Packet getPacket() {
            return packet.clear();
        }

        public void send(String message) throws IOException {
            target.send(message);
        }

        public void send(Packet packet) throws IOException {
            send(JsonParser.toJson(packet));
        }

        public void responseNameRegister(boolean accept) throws IOException {
            send(TransferHelper.responseNameRegister(accept));
        }

        public void responseAuthRegister(UUID sessionId) throws IOException {
            send(TransferHelper.responseAuthRegister(sessionId));
        }

        public void warnUnauthenticated() throws IOException {
            send(TransferHelper.warnUnauthenticated());
        }

        public void warnStopFindChat() throws IOException {
            send(TransferHelper.warnStopFindChat());
        }

        public void askForInvitation(String friendName) throws IOException {
            send(TransferHelper.askForInvitation(friendName));
        }

        public void askForConfirmation(String userName) throws IOException {
            send(TransferHelper.askForConfirmation(userName));
        }

        public void responseFriendOffline(String friendName) throws IOException {
            send(TransferHelper.responseFriendOffline(friendName));
        }

        public void responseInvitationDeclined(String friendName) throws IOException {
            send(TransferHelper.responseInvitationDeclined(friendName));
        }

        public void responseCreatedChat(FriendInfo info) throws IOException {
            send(TransferHelper.responseCreatedChat(info));
        }

        public void sendChatMessage(Message message) throws IOException {
            send(TransferHelper.sendChatMessage(message));
        }

        public void responseCreatedMessage() throws IOException {
            send(TransferHelper.responseCreatedMessage());
        }

        public void modifyFriendInfo(FriendInfo info) throws IOException {
            send(TransferHelper.modifyFriendInfo(info));
        }
    }

}
