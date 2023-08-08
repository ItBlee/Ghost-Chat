package com.itblee.core.helper;

import com.itblee.core.client.Client;
import com.itblee.model.Message;
import com.itblee.transfer.*;
import com.itblee.utils.JsonParser;

import java.io.IOException;

public final class TransferHelper {

    private static final Packet packet = new Packet();

    public static Packet get() {
        return packet.clear();
    }

    public static void send(String message) throws IOException {
        Client.getInstance().getWorker().send(message);
    }

    public static void send(Packet request) throws IOException {
        send(JsonParser.toJson(request));
    }

    public static void requestChat() throws IOException {
        Packet request = get();
        request.setHeader(MyRequest.FIND_CHAT);
        send(request);
    }

    public static void stopFindChat() throws IOException {
        Packet request = get();
        request.setHeader(MyRequest.STOP_FIND);
        send(request);
    }

    public static void leaveChat() throws IOException {
        Packet packet = get();
        packet.setHeader(MyRequest.LEAVE_CHAT);
        send(packet);
    }

    public static void responseInvite(boolean accept) throws IOException {
        Packet request = get();
        request.setHeader(MyRequest.INVITE_CHAT);
        request.setCode(accept ? DefaultStatusCode.OK : null);
        send(request);
    }

    public static void responseConfirm(boolean accept) throws IOException {
        Packet request = get();
        request.setHeader(MyRequest.CONFIRM_CHAT);
        request.setCode(accept ? DefaultStatusCode.OK : null);
        send(request);
    }

    public static void sendMessage(Message message) throws IOException {
        Packet packet = get();
        packet.setHeader(MyRequest.SEND_MESSAGE);
        packet.putData(MyDataKey.MESSAGE_BODY, message);
        send(packet);
    }

    public static void login(String username, String password) throws IOException {
        Packet packet = get();
        packet.setHeader(MyRequest.AUTH_LOGIN);
        packet.putData(MyDataKey.USERNAME, username);
        packet.putData(MyDataKey.PASSWORD, password);
        send(packet);
    }

    public static void register(String username, String password) throws IOException {
        Packet packet = get();
        packet.setHeader(MyRequest.AUTH_REGISTER);
        packet.putData(MyDataKey.USERNAME, username);
        packet.putData(MyDataKey.PASSWORD, password);
        send(packet);
    }

    public static void logout() throws IOException {
        Packet packet = get();
        packet.setHeader(MyRequest.AUTH_LOGOUT);
        send(packet);
    }

}
