package com.itblee.core.Impl;

import com.itblee.core.Gate;
import org.openeuler.com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.ServerException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Base64;

import static com.itblee.constant.ServerConstant.KEY_STORE_ALIAS;
import static com.itblee.constant.ServerConstant.KEY_STORE_PATH;

public class SecureGate extends GateImpl implements Gate {

    public static final boolean SSL_DEBUG_ENABLE = false;

    private final String keyStorePassword;

    private static final class InstanceHolder {
        static final ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
    }

    public SecureGate(int port, String password) {
        super(port);
        this.keyStorePassword = password;
    }

    @Override
    public void open() throws IOException {
        if (isOpened())
            throw new ServerException("Already opened Server !");
        addProvider();
        serverSocket = InstanceHolder.serverSocketFactory.createServerSocket(port);
    }

    private void addProvider() {
        java.security.Security.addProvider(new Provider());
        System.setProperty("javax.net.ssl.keyStore", KEY_STORE_PATH);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
        if (SSL_DEBUG_ENABLE)
            System.setProperty("javax.net.debug","all");
        checkKey();
    }

    private void checkKey() {
        try {
            KeyStore ks = KeyStore.getInstance("jks");
            ks.load(new FileInputStream(KEY_STORE_PATH), keyStorePassword.toCharArray());
            Key key = ks.getKey(KEY_STORE_ALIAS, keyStorePassword.toCharArray());
            Certificate cert = ks.getCertificate(KEY_STORE_ALIAS);
            System.out.println("--- Certificate START ---");
            System.out.println(cert);
            System.out.println("--- Certificate END ---\n");
            System.out.println("Public key: " + Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded()));
            System.out.println("Private key: " + Base64.getEncoder().encodeToString(key.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
