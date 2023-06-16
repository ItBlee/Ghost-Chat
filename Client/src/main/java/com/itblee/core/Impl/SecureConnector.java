package com.itblee.core.Impl;

import com.itblee.core.Connector;
import org.openeuler.com.sun.net.ssl.internal.ssl.Provider;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ConnectException;

import static com.itblee.constant.ClientConstant.*;

public class SecureConnector extends ConnectorImpl implements Connector {

    private final String trustStorePassword;

    private static final class InstanceHolder {
        static final SocketFactory socketFactory = SSLSocketFactory.getDefault();
    }

    public SecureConnector(String ip, int port, String password) {
        this(ip, port, password, SECURE_SOCKET_TIMEOUT);

    }

    public SecureConnector(String ip, int port, String password, int timeout) {
        super(ip, port, timeout);
        this.trustStorePassword = password;
    }

    @Override
    public SSLSocket connect() throws IOException {
        if (socket == null) {
            addProvider();
            socket = InstanceHolder.socketFactory.createSocket(ip, port);
            socket.setSoTimeout(timeout);
        }
        if (isClosed())
            throw new ConnectException();
        return (SSLSocket) socket;
    }

    private void addProvider() {
        java.security.Security.addProvider(new Provider());
        System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        if (SSL_DEBUG_ENABLE)
            System.setProperty("javax.net.debug","all");
    }

}
