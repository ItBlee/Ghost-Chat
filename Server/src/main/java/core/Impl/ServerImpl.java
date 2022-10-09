package core.Impl;

import core.Server;
import org.openeuler.com.sun.net.ssl.internal.ssl.Provider;
import utils.StringUtil;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerException;
import java.rmi.server.ServerNotActiveException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

import static constant.ServerConstant.*;
import static constant.SystemConstant.MAIN_PORT;
import static constant.SystemConstant.VERIFY_PORT;

public class ServerImpl implements Server {
    private ServerSocket serverSocket;

    public ServerImpl() {
    }

    @Override
    public void open() throws IOException {
        if (isOpened())
            throw new ServerException("Already opened Server !");
        serverSocket = new ServerSocket(MAIN_PORT);
    }

    @Override
    public void openSecure(String keyStorePassword) throws IOException {
        if (isOpened())
            throw new ServerException("Already opened Server !");
        addProvider(keyStorePassword);
        importKey(keyStorePassword);
        serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(VERIFY_PORT);
    }

    @Override
    public Socket accept() throws ServerNotActiveException, IOException {
        if (serverSocket == null)
            throw new ServerNotActiveException();
        return serverSocket.accept();
    }

    @Override
    public boolean isOpened() {
        return serverSocket != null;
    }

    @Override
    public boolean isClosed() {
        if (serverSocket != null)
            return serverSocket.isClosed();
        return false;
    }

    @Override
    public boolean isSecured() {
        return serverSocket instanceof SSLServerSocket;
    }

    @Override
    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed())
            serverSocket.close();
    }

    @Override
    public void addProvider(String keyStorePassword) {
        java.security.Security.addProvider(new Provider());
        System.setProperty("javax.net.ssl.keyStore", SERVER_SIDE_PATH + KEY_STORE_NAME);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
        if (SSL_DEBUG_ENABLE)
            System.setProperty("javax.net.debug","all");
    }

    /**
     * Lấy chứng chỉ, public key, private key từ Key Store key.jks
     */
    public void importKey(String keyStorePassword) {
        try {
            KeyStore ks = KeyStore.getInstance("jks");
            ks.load(new FileInputStream(SERVER_SIDE_PATH + KEY_STORE_NAME), keyStorePassword.toCharArray());
            Key key = ks.getKey(KEY_STORE_ALIAS, keyStorePassword.toCharArray());
            final Certificate cert = ks.getCertificate("mykey");
            System.out.println("--- Certificate START ---");
            System.out.println(cert);
            System.out.println("--- Certificate END ---\n");
            System.out.println("Public key: " + StringUtil.getStringFromKey(cert.getPublicKey()));
            System.out.println("Private key: " + StringUtil.getStringFromKey(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
