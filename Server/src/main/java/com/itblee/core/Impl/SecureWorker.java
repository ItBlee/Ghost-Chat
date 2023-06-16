package com.itblee.core.Impl;

import com.itblee.core.impl.AbstractSecureWorker;
import com.itblee.core.ServerService;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class SecureWorker extends AbstractSecureWorker {

    public SecureWorker(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    protected UUID getSender() {
        return null;
    }

    @Override
    protected String getSecretKey(UUID uuid) {
        try {
            if (getUid() == null)
                setUid(uuid);
            UserSession session = ServerService.requireSession(this, uuid);
            return session.getSecretKey();
        } catch (IOException ignored) {
            return "";
        }
    }

}
