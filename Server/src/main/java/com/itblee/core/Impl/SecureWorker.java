package com.itblee.core.Impl;

import com.itblee.core.helper.ServerHelper;
import com.itblee.core.helper.TransferHelper;
import com.itblee.core.impl.AbstractSecureWorker;
import com.itblee.exception.UnverifiedException;
import com.itblee.transfer.StatusCode;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLPeerUnverifiedException;
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
            UserSession session = ServerHelper.getService().requireSession(this, uuid);
            return session.getSecretKey();
        } catch (UnverifiedException e) {
            return null;
        }
    }

    @Override
    public void send(String message) throws IOException {
        try {
            super.send(message);
        } catch (SSLException e) {
            sendException(message);
            throw e;
        }
    }

    @Override
    public String receive() throws IOException {
        try {
            return super.receive();
        } catch (SSLPeerUnverifiedException e) {
            TransferHelper.call(this).warnSession(StatusCode.FORBIDDEN);
            throw e;
        } catch (SSLKeyException e) {
            TransferHelper.call(this).warnWrongKey();
            throw e;
        }
    }
}
