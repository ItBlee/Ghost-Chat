package com.itblee.core.helper;

import com.google.gson.reflect.TypeToken;
import com.itblee.core.User;
import com.itblee.core.Worker;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.security.Session;
import com.itblee.transfer.*;
import com.itblee.utils.JsonParser;
import com.itblee.utils.ObjectUtil;

import java.io.IOException;
import java.util.List;

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

    public static Caller call(Session session) {
        return CALLERS.get().setTarget(session.getWorker());
    }

    public static Caller call(User user) {
        return CALLERS.get().setTarget(user.getWorker());
    }

    public static Packet responseLogin(String username, DefaultStatusCode code) {
        Packet packet = get();
        packet.setHeader(MyRequest.AUTH_LOGIN);
        packet.setCode(code);
        packet.putData(MyDataKey.USERNAME, username);
        return packet;
    }

    public static Packet responseRegister(String username, DefaultStatusCode code) {
        Packet packet = get();
        packet.setHeader(MyRequest.AUTH_REGISTER);
        packet.setCode(code);
        packet.putData(MyDataKey.USERNAME, username);
        return packet;
    }

    public static Packet warnUnauthenticated() {
        Packet packet = get();
        packet.setHeader(MyRequest.UNAUTHENTICATED);
        return packet;
    }

    public static Packet warnStopFindChat() {
        Packet packet = get();
        packet.setHeader(MyRequest.STOP_FIND);
        return packet;
    }

    public static Packet askForInvitation(String friendName) {
        Packet packet = get();
        packet.setHeader(MyRequest.INVITE_CHAT);
        packet.putData(MyDataKey.FRIEND_NAME, friendName);
        return packet;
    }

    public static Packet askForConfirmation(String userName) {
        Packet packet = get();
        packet.setHeader(MyRequest.CONFIRM_CHAT);
        packet.putData(MyDataKey.FRIEND_NAME, userName);
        return packet;
    }

    public static Packet responseFriendOffline(String friendName) {
        Packet packet = get();
        packet.setHeader(MyRequest.CHAT_INFO);
        packet.setCode(DefaultStatusCode.NOT_FOUND);
        packet.putData(MyDataKey.FRIEND_NAME, friendName);
        return packet;
    }

    public static Packet responseInvitationDeclined(String friendName) {
        Packet packet = get();
        packet.setHeader(MyRequest.CHAT_INFO);
        packet.setCode(DefaultStatusCode.FORBIDDEN);
        packet.putData(MyDataKey.FRIEND_NAME, friendName);
        return packet;
    }

    public static Packet responseCreatedChat(FriendInfo info) {
        Packet packet = get();
        packet.setHeader(MyRequest.CHAT_INFO);
        packet.setCode(DefaultStatusCode.CREATED);
        packet.putData(MyDataKey.FRIEND_INFO, info);
        return packet;
    }

    public static Packet sendChatMessage(Message message) {
        Packet packet = get();
        packet.setHeader(MyRequest.RECEIVE_MESSAGE);
        packet.putData(MyDataKey.MESSAGE_BODY, message);
        return packet;
    }

    public static Packet responseSendMessageStatus(boolean isSuccess) {
        Packet packet = get();
        packet.setHeader(MyRequest.SEND_MESSAGE);
        packet.setCode(isSuccess ? DefaultStatusCode.CREATED : DefaultStatusCode.BAD_REQUEST);
        return packet;
    }

    public static Packet modifyFriendInfo(FriendInfo info) {
        Packet packet = get();
        packet.setHeader(MyRequest.FRIEND_INFO);
        packet.putData(MyDataKey.FRIEND_INFO, info);
        return packet;
    }

    public static Packet recoveryHistoryChat(List<Message> messages) {
        Packet packet = get();
        packet.setHeader(MyRequest.HISTORY_RECOVERY);
        packet.putData(MyDataKey.HISTORY_CHAT, messages, new TypeToken<List<Message>>(){}.getType());
        return packet;
    }

    public static Packet serverMessage(Message message) {
        Packet packet = get();
        packet.setHeader(MyRequest.SERVER_MESSAGE);
        packet.putData(MyDataKey.MESSAGE_BODY, message);
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

        public void responseLogin(DefaultStatusCode code) {
            send(TransferHelper.responseLogin(null, code));
        }

        public void responseLogin(String username, DefaultStatusCode code) {
            send(TransferHelper.responseLogin(username, code));
        }

        public void responseRegister(DefaultStatusCode code) {
            send(TransferHelper.responseRegister(null, code));
        }

        public void responseRegister(String username, DefaultStatusCode code) {
            send(TransferHelper.responseRegister(username, code));
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
