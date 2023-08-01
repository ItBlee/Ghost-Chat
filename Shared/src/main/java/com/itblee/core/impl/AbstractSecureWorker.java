package com.itblee.core.impl;

import com.itblee.exception.UnverifiedException;
import com.itblee.security.EncryptUtil;
import com.itblee.transfer.EncryptPacket;
import com.itblee.transfer.Request;
import com.itblee.utils.JsonParser;
import com.itblee.utils.StringUtil;

import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.KeyException;
import java.util.Arrays;
import java.util.UUID;

public abstract class AbstractSecureWorker extends WorkerImpl {

    private static final Request[] EXCEPTIONS = {Request.SESSION, Request.SESSION_KEY};

    private String receiveException;

    public AbstractSecureWorker(Socket socket) throws IOException {
        super(socket);
    }

    protected abstract UUID getSender();

    protected abstract String getSecretKey(UUID uuid);

    protected void sendException(String message)  throws IOException {
        if (isException(message))
            super.send(message);
    }

    @Override
    public void send(String message) throws IOException {
        EncryptPacket wrapper = new EncryptPacket();
        wrapper.setSender(getSender());
        String secretKey = getSecretKey(getUid());
        if (StringUtil.isBlank(secretKey))
            throw new SSLPeerUnverifiedException("Not Found");
        try {
            String encrypt = EncryptUtil.encrypt(message, secretKey);
            wrapper.setBody(encrypt);
            super.send(JsonParser.toJson(wrapper));
        } catch (Exception e) {
            throw new SSLKeyException("Key not match !");
        }
    }

    protected String receiveException() {
        if (isException(receiveException)) {
            String temp = receiveException;
            receiveException = null;
            return temp;
        }
        throw new IllegalStateException();
    }

    @Override
    public String receive() throws IOException {
        String message = super.receive();
        if (StringUtil.isBlank(message))
            return null;
        EncryptPacket wrapper = JsonParser.fromJson(message, EncryptPacket.class)
                .orElseThrow(IOException::new);
        String secretKey = getSecretKey(wrapper.getSender());
        if (StringUtil.isBlank(secretKey))
            throw new SSLPeerUnverifiedException("Not Found");
        try {
            String decrypt = EncryptUtil.decrypt(wrapper.getBody(), secretKey);
            System.out.println("decrypt:" + decrypt);
            return decrypt;
        } catch (Exception e) {
            receiveException = message;
            throw new SSLKeyException("Key not match !");
        }
    }

    private boolean isException(String message) {
        if (StringUtil.isBlank(message))
            return false;
        return Arrays.stream(EXCEPTIONS)
                .anyMatch(e -> message.contains(e.toString()));
    }

}
