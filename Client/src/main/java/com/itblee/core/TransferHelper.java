package com.itblee.core;

import com.itblee.model.Message;
import com.itblee.transfer.*;

import java.io.IOException;

public class TransferHelper {

    private static final Packet packet = new Packet();

    private TransferHelper() {
        throw new AssertionError();
    }

    public static Packet get() {
        return packet.clear();
    }

    public static void closeConnect() throws IOException {
        Packet request = TransferHelper.get();
        request.setHeader(Header.BREAK_CONNECT);
        ClientHelper.send(request);
    }

    public static void registerName(String username) throws IOException {
        Packet request = get();
        request.setHeader(Header.REGISTER_NAME);
        request.putData(DataKey.USERNAME, username);
        ClientHelper.send(request);
    }

    public static void requestChat() throws IOException {
        Packet request = get();
        request.setHeader(Header.FIND_CHAT);
        ClientHelper.send(request);
    }

    public static void stopFindChat() throws IOException {
        Packet request = get();
        request.setHeader(Header.STOP_FIND);
        ClientHelper.send(request);
    }

    public static void leaveChat() throws IOException {
        Packet packet = TransferHelper.get();
        packet.setHeader(Header.LEAVE_CHAT);
        ClientHelper.send(packet);
    }

    public static void responseInvite(boolean accept) throws IOException {
        Packet request = get();
        request.setHeader(Header.INVITE_CHAT);
        request.setCode(accept ? StatusCode.OK : null);
        ClientHelper.send(request);
    }

    public static void responseConfirm(boolean accept) throws IOException {
        Packet request = get();
        request.setHeader(Header.CONFIRM_CHAT);
        request.setCode(accept ? StatusCode.OK : null);
        ClientHelper.send(request);
    }

    public static void sendMessage(Message message) throws IOException {
        Packet packet = get();
        packet.setHeader(Header.SEND_MESSAGE);
        packet.putData(DataKey.MESSAGE_BODY, message);
        ClientHelper.send(packet);
    }

}
