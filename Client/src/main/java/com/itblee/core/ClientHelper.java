package com.itblee.core;

import com.itblee.gui.ClientFrame;
import com.itblee.security.User;
import com.itblee.transfer.Packet;
import com.itblee.utils.JsonParser;

import java.io.IOException;
import java.util.UUID;

public class ClientHelper {

    private static final Client CLIENT = Client.getInstance();

    private ClientHelper() {
        throw new AssertionError();
    }

    public static ClientFrame getFrame() {
        return CLIENT.getFrame();
    }

    public static Worker requireConnection() throws IOException {
        return CLIENT.requireConnection();
    }

    public static void requestSession() throws IOException {
        CLIENT.requestSession();
    }

    public static boolean isConnected() {
        return CLIENT.getConnector() != null && CLIENT.getConnector().isConnected();
    }

    public static void send(String message) throws IOException {
        CLIENT.getWorker().send(message);
    }

    public static void close() throws IOException {
        CLIENT.getWorker().close();
    }

    public static void send(Packet request) throws IOException {
        send(JsonParser.toJson(request));
    }

    public static Packet await() throws InterruptedException {
        return CLIENT.getWorker().await();
    }

    public static User getUser() {
        return CLIENT.getUser();
    }

    public static void setUid(UUID uuid) {
        getUser().setUid(uuid);
    }

    public static UUID getUid() {
        return getUser().getUid();
    }

}
