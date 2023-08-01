package com.itblee.core.Impl;

import com.itblee.core.helper.TransferHelper;
import com.itblee.core.impl.AbstractSecureWorker;
import com.itblee.core.helper.ClientHelper;
import com.itblee.transfer.StatusCode;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyException;
import java.util.UUID;

public class SecureWorker extends AbstractSecureWorker {

    private String lastMsg;

    public SecureWorker(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    protected UUID getSender() {
        return ClientHelper.getUid();
    }

    @Override
    protected String getSecretKey(UUID uuid) {
        return ClientHelper.getUser().getSecretKey();
    }

    @Override
    public void send(String message) throws IOException {
        lastMsg = message;
        super.send(message);
    }

    public void resend() throws IOException {
        send(lastMsg);
    }

    @Override
    public String receive() throws IOException {
        try {
            return super.receive();
        } catch (SSLException e) {
            return receiveException();
        }
    }
}
