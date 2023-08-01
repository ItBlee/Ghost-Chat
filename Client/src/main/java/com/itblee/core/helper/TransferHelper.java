package com.itblee.core.helper;

import com.itblee.model.Message;
import com.itblee.transfer.*;

import java.io.IOException;

public final class TransferHelper {

    private static final Packet packet = new Packet();

    private TransferHelper() {
        throw new AssertionError();
    }

    public static Packet get() {
        return packet.clear();
    }

    public static void closeConnect() throws IOException {
        Packet request = get();
        request.setHeader(Request.BREAK_CONNECT);
        ClientHelper.send(request);
    }

    public static void requestChat() throws IOException {
        Packet request = get();
        request.setHeader(Request.FIND_CHAT);
        ClientHelper.send(request);
    }

    public static void stopFindChat() throws IOException {
        Packet request = get();
        request.setHeader(Request.STOP_FIND);
        ClientHelper.send(request);
    }

    public static void leaveChat() throws IOException {
        Packet packet = get();
        packet.setHeader(Request.LEAVE_CHAT);
        ClientHelper.send(packet);
    }

    public static void responseInvite(boolean accept) throws IOException {
        Packet request = get();
        request.setHeader(Request.INVITE_CHAT);
        request.setCode(accept ? StatusCode.OK : null);
        ClientHelper.send(request);
    }

    public static void responseConfirm(boolean accept) throws IOException {
        Packet request = get();
        request.setHeader(Request.CONFIRM_CHAT);
        request.setCode(accept ? StatusCode.OK : null);
        ClientHelper.send(request);
    }

    public static void sendMessage(Message message) throws IOException {
        Packet packet = get();
        packet.setHeader(Request.SEND_MESSAGE);
        packet.putData(DataKey.MESSAGE_BODY, message);
        ClientHelper.send(packet);
    }

    public static void login(String username, String password) throws IOException {
        Packet packet = get();
        packet.setHeader(Request.AUTH_LOGIN);
        packet.putData(DataKey.USERNAME, username);
        packet.putData(DataKey.PASSWORD, password);
        ClientHelper.send(packet);
    }

    public static void register(String username, String password) throws IOException {
        Packet packet = get();
        packet.setHeader(Request.AUTH_REGISTER);
        packet.putData(DataKey.USERNAME, username);
        packet.putData(DataKey.PASSWORD, password);
        ClientHelper.send(packet);
    }

    public static void logout() throws IOException {
        Packet packet = get();
        packet.setHeader(Request.AUTH_LOGOUT);
        ClientHelper.send(packet);
    }

}
