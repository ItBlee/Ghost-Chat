package com.itblee.core.impl;

import com.itblee.security.EncryptUtil;
import com.itblee.transfer.PacketWrapper;
import com.itblee.utils.JsonParser;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public abstract class AbstractSecureWorker extends WorkerImpl {

    public AbstractSecureWorker(Socket socket) throws IOException {
        super(socket);
    }

    protected abstract UUID getSender();

    protected abstract String getSecretKey(UUID uuid);

    @Override
    public void send(String message) throws IOException {
        PacketWrapper wrapper = new PacketWrapper();
        wrapper.setSender(getSender());
        wrapper.setBody(EncryptUtil.encrypt(message, getSecretKey(wrapper.getSender())));
        super.send(JsonParser.toJson(wrapper));
    }

    @Override
    public String receive() throws IOException {
        String message = super.receive();
        PacketWrapper wrapper = JsonParser.fromJson(message, PacketWrapper.class)
                .orElseThrow(IOException::new);
        return EncryptUtil.decrypt(wrapper.getBody(), getSecretKey(wrapper.getSender()));
    }

}
