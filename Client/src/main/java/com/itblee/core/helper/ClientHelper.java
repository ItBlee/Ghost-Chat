package com.itblee.core.helper;

import com.itblee.core.ClientContainer;
import com.itblee.core.User;
import com.itblee.core.Worker;
import com.itblee.core.client.Client;
import com.itblee.gui.ClientFrame;
import com.itblee.transfer.Packet;

import java.io.IOException;

public final class ClientHelper {

    private ClientHelper() {
        throw new AssertionError();
    }

    public static Client getClient() {
        return ClientContainer.client;
    }

    public static ClientFrame getFrame() {
        return ClientContainer.frame;
    }

    public static User getUser() {
        return ClientContainer.user;
    }

    public static boolean authenticated() {
        return getUser().authenticated();
    }

    public static Worker getConnection() throws IOException, InterruptedException {
        return getClient().getConnection();
    }

    public static void closeConnection() throws IOException {
        getClient().closeConnection();
    }

    public static Packet await() throws InterruptedException {
        return getClient().getWorker().await();
    }

}
