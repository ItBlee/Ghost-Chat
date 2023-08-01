package com.itblee.core.helper;

import com.google.gson.reflect.TypeToken;
import com.itblee.core.Worker;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.security.User;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Request;
import com.itblee.transfer.Packet;
import com.itblee.transfer.StatusCode;
import com.itblee.utils.JsonParser;
import com.itblee.utils.ObjectUtil;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public final class TransferHelper {

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

    public static Caller call(User user) {
        return CALLERS.get().setTarget(user.getWorker());
    }

    public static Packet responseSession(UUID uuid, StatusCode code) {
        Packet packet = get();
        packet.setHeader(Request.SESSION);
        packet.setCode(code);
        packet.putData(DataKey.SESSION_ID, uuid);
        return packet;
    }

    public static Packet responseChangeKey(StatusCode code) {
        Packet packet = get();
        packet.setHeader(Request.SESSION_KEY);
        packet.setCode(code);
        return packet;
    }

    public static Packet responseLogin(String username, StatusCode code) {
        Packet packet = get();
        packet.setHeader(Request.AUTH_LOGIN);
        packet.setCode(code);
        packet.putData(DataKey.USERNAME, username);
        return packet;
    }

    public static Packet responseRegister(String username, StatusCode code) {
        Packet packet = get();
        packet.setHeader(Request.AUTH_REGISTER);
        packet.setCode(code);
        packet.putData(DataKey.USERNAME, username);
        return packet;
    }

    public static Packet warnSession(StatusCode code) {
        Packet packet = get();
        packet.setHeader(Request.SESSION);
        packet.setCode(code);
        return packet;
    }

    public static Packet warnWrongKey() {
        Packet packet = get();
        packet.setHeader(Request.SESSION_KEY);
        packet.setCode(StatusCode.BAD_REQUEST);
        return packet;
    }

    public static Packet warnUnauthenticated() {
        Packet packet = get();
        packet.setHeader(Request.UNAUTHENTICATED);
        return packet;
    }

    public static Packet warnStopFindChat() {
        Packet packet = get();
        packet.setHeader(Request.STOP_FIND);
        return packet;
    }

    public static Packet askForInvitation(String friendName) {
        Packet packet = get();
        packet.setHeader(Request.INVITE_CHAT);
        packet.putData(DataKey.FRIEND_NAME, friendName);
        return packet;
    }

    public static Packet askForConfirmation(String userName) {
        Packet packet = get();
        packet.setHeader(Request.CONFIRM_CHAT);
        packet.putData(DataKey.FRIEND_NAME, userName);
        return packet;
    }

    public static Packet responseFriendOffline(String friendName) {
        Packet packet = get();
        packet.setHeader(Request.CHAT_INFO);
        packet.setCode(StatusCode.NOT_FOUND);
        packet.putData(DataKey.FRIEND_NAME, friendName);
        return packet;
    }

    public static Packet responseInvitationDeclined(String friendName) {
        Packet packet = get();
        packet.setHeader(Request.CHAT_INFO);
        packet.setCode(StatusCode.FORBIDDEN);
        packet.putData(DataKey.FRIEND_NAME, friendName);
        return packet;
    }

    public static Packet responseCreatedChat(FriendInfo info) {
        Packet packet = get();
        packet.setHeader(Request.CHAT_INFO);
        packet.setCode(StatusCode.CREATED);
        packet.putData(DataKey.FRIEND_INFO, info);
        return packet;
    }

    public static Packet sendChatMessage(Message message) {
        Packet packet = get();
        packet.setHeader(Request.RECEIVE_MESSAGE);
        packet.putData(DataKey.MESSAGE_BODY, message);
        return packet;
    }

    public static Packet responseSendMessageStatus(boolean isSuccess) {
        Packet packet = get();
        packet.setHeader(Request.SEND_MESSAGE);
        packet.setCode(isSuccess ? StatusCode.CREATED : StatusCode.BAD_REQUEST);
        return packet;
    }

    public static Packet modifyFriendInfo(FriendInfo info) {
        Packet packet = get();
        packet.setHeader(Request.FRIEND_INFO);
        packet.putData(DataKey.FRIEND_INFO, info);
        return packet;
    }

    public static Packet recoveryHistoryChat(List<Message> messages) {
        Packet packet = get();
        packet.setHeader(Request.HISTORY_RECOVERY);
        packet.putData(DataKey.HISTORY_CHAT, messages, new TypeToken<List<Message>>(){}.getType());
        return packet;
    }

    public static Packet serverMessage(Message message) {
        Packet packet = get();
        packet.setHeader(Request.SERVER_MESSAGE);
        packet.putData(DataKey.MESSAGE_BODY, message);
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

        public void send(Packet packet) {
            try {
                send(JsonParser.toJson(packet));
            } catch (IOException ignored) {}
        }

        public void responseSession(StatusCode code) {
            send(TransferHelper.responseSession(null, code));
        }

        public void responseSession(UUID uuid, StatusCode code) {
            send(TransferHelper.responseSession(uuid, code));
        }

        public void responseChangeKey(StatusCode code) {
            send(TransferHelper.responseChangeKey(code));
        }

        public void responseLogin(StatusCode code) {
            send(TransferHelper.responseLogin(null, code));
        }

        public void responseLogin(String username, StatusCode code) {
            send(TransferHelper.responseLogin(username, code));
        }

        public void responseRegister(StatusCode code) {
            send(TransferHelper.responseRegister(null, code));
        }

        public void responseRegister(String username, StatusCode code) {
            send(TransferHelper.responseRegister(username, code));
        }

        public void warnSession(StatusCode code) {
            send(TransferHelper.warnSession(code));
        }

        public void warnWrongKey() {
            send(TransferHelper.warnWrongKey());
        }

        public void warnUnauthenticated() {
            send(TransferHelper.warnUnauthenticated());
        }

        public void warnStopFindChat() {
            send(TransferHelper.warnStopFindChat());
        }

        public void askForInvitation(String friendName) {
            send(TransferHelper.askForInvitation(friendName));
        }

        public void askForConfirmation(String userName) {
            send(TransferHelper.askForConfirmation(userName));
        }

        public void responseFriendOffline(String friendName) {
            send(TransferHelper.responseFriendOffline(friendName));
        }

        public void responseInvitationDeclined(String friendName) {
            send(TransferHelper.responseInvitationDeclined(friendName));
        }

        public void responseCreatedChat(FriendInfo info) {
            send(TransferHelper.responseCreatedChat(info));
        }

        public void sendChatMessage(Message message) {
            send(TransferHelper.sendChatMessage(message));
        }

        public void responseSendMessageStatus(boolean isSuccess) {
            send(TransferHelper.responseSendMessageStatus(isSuccess));
        }

        public void modifyFriendInfo(FriendInfo info) {
            send(TransferHelper.modifyFriendInfo(info));
        }

        public void recoveryHistoryChat(List<Message> messages) {
            send(TransferHelper.recoveryHistoryChat(messages));
        }

        public void serverMessage(Message message) {
            send(TransferHelper.serverMessage(message));
        }
    }

}
