package com.itblee.core.Impl;

import com.itblee.core.impl.AbstractSecureWorker;
import com.itblee.core.ClientHelper;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class SecureWorker extends AbstractSecureWorker {

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

}
