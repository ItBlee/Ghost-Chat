package com.itblee.core.helper;

import com.itblee.core.Client;
import com.itblee.core.Worker;
import com.itblee.gui.ClientFrame;
import com.itblee.security.User;
import com.itblee.transfer.Packet;
import com.itblee.utils.JsonParser;

import java.io.IOException;
import java.util.UUID;

public final class ClientHelper {

    private static final Client CLIENT = Client.getInstance();

    private ClientHelper() {
        throw new AssertionError();
    }

    public static void verify() throws IOException, InterruptedException {
        CLIENT.getService().verify();
    }

    public static ClientFrame getFrame() {
        return CLIENT.getFrame();
    }

    public static boolean verified() {
        return CLIENT.verified();
    }

    public static boolean authenticated() {
        return CLIENT.authenticated();
    }

    public static Worker getConnection() throws IOException, InterruptedException {
        return CLIENT.getConnection();
    }

    public static void send(String message) throws IOException {
        CLIENT.getWorker().send(message);
    }

    public static void closeConnection() throws IOException {
        CLIENT.closeConnection();
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

    public static UUID getUid() {
        return getUser() != null ? getUser().getUid() : null;
    }

}
