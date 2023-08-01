package com.itblee.core;

import com.itblee.core.Impl.ConnectorImpl;
import com.itblee.core.Impl.SecureConnector;
import com.itblee.core.Impl.SecureWorker;
import com.itblee.core.helper.TransferHelper;
import com.itblee.core.impl.WorkerImpl;
import com.itblee.exception.UnverifiedException;
import com.itblee.gui.ClientFrame;
import com.itblee.security.User;
import com.itblee.transfer.Packet;
import com.itblee.transfer.Request;
import com.itblee.utils.*;

import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class Client {

    private static Client client;

    private final ClientFrame frame;
    private final ClientService service;

    private User user;
    private Connector connector;
    private Connector sslConnector;
    private Worker worker;
    private Worker sslWorker;

    private Client(ClientFrame frame) {
        this.frame = frame;
        service = new ClientService();
    }

    public static synchronized Client init(ClientFrame frame) {
        ObjectUtil.requireNonNull(frame);
        if (!frame.isEnabled())
            throw new IllegalArgumentException();
        if (client != null)
            throw new IllegalStateException("Already initialized clientLauncher !");
        client = new Client(frame);
        client.loadLocalSession();
        return client;
    }

    private void loadLocalSession() {
        if (user == null)
            user = new User();
        user.setCertificate(SessionUtil.load());
        if (user.getCertificate() != null)
            System.out.println("Loaded Session: " + user.getUid() + "|" + user.getSecretKey());
    }

    public static synchronized Client getInstance() {
        if (client == null)
            throw new IllegalStateException("You have to call init first");
        return client;
    }

    public Worker getConnection() throws IOException, InterruptedException {
        try {
            return connect();
        } catch (UnverifiedException e) {
            try {
                service.verify();
                return connect();
            } catch (UnverifiedException ignored) {
                throw new IOException();
            }
        }
    }

    public Worker connect() throws IOException, UnverifiedException {
        if(!verified())
            throw new UnverifiedException();
        if (connector != null && worker != null
                && connector.isConnected() && worker.isAlive())
            return worker;
        if (connector != null && worker != null
                && (connector.isClosed() || worker.isInterrupted()))
            return reconnect();
        String ip = PropertyUtil.getString("connect.ip");
        int port = PropertyUtil.getInt("connect.port");
        connector = new ConnectorImpl(ip, port);
        worker = new SecureWorker(connector.connect());
        worker.setController(RequestMapping.CONNECT);
        worker.setUid(user.getUid());
        worker.start();
        return worker;
    }

    public Worker reconnect() throws IOException, UnverifiedException {
        if(!verified())
            throw new UnverifiedException();
        if (connector == null)
            throw new IllegalStateException("Client not initialized connection.");
        if (worker != null)
            worker.close();
        worker = new SecureWorker(connector.reconnect());
        worker.setController(RequestMapping.CONNECT);
        worker.setUid(user.getUid());
        worker.start();
        return worker;
    }

    public Worker connectSSL() throws IOException {
        if (sslConnector != null && sslWorker != null
                && sslConnector.isConnected() && sslWorker.isAlive())
            return sslWorker;
        if (sslConnector != null && sslWorker != null
                && (sslConnector.isClosed() || sslWorker.isInterrupted()))
            return reconnectSSL();
        String trust_store_pwd = PropertyUtil.getString("jsse.truststore.pwd");
        String ip = PropertyUtil.getString("connect.ip");
        int portSecure = PropertyUtil.getInt("connect.auth.port");
        sslConnector = new SecureConnector(ip, portSecure, trust_store_pwd);
        SSLSocket sslSocket = (SSLSocket) sslConnector.connect();
        sslWorker = new WorkerImpl(sslSocket);
        sslWorker.setController(RequestMapping.SSL);
        sslWorker.start();
        return sslWorker;
    }

    public Worker reconnectSSL() throws IOException {
        if (sslConnector == null)
            throw new IllegalStateException("Client not initialized auth connection.");
        if (sslWorker != null)
            sslWorker.close();
        SSLSocket sslSocket = (SSLSocket) sslConnector.reconnect();
        sslWorker = new WorkerImpl(sslSocket);
        sslWorker.setController(RequestMapping.SSL);
        sslWorker.start();
        return sslWorker;
    }

    public void closeConnection() throws IOException {
        if (sslWorker != null && sslWorker.isAlive()) {
            Packet request = TransferHelper.get();
            request.setHeader(Request.BREAK_CONNECT);
            sslWorker.send(JsonParser.toJson(request));
            sslWorker.close();
            sslConnector = null;
            sslWorker = null;
        }
        if (worker != null && worker.isAlive()) {
            TransferHelper.closeConnect();
            worker.close();
            connector = null;
            worker = null;
        }
        if (verified())
            SessionUtil.save();
    }

    public Connector getConnector() {
        return connector;
    }

    public ClientFrame getFrame() {
        return frame;
    }

    public ClientService getService() {
        return service;
    }

    public Worker getWorker() {
        return worker;
    }

    public User getUser() {
        return user;
    }
    public void toAnonymous() {
        user.setUsername(null);
    }

    public boolean verified() {
        return ValidateUtil.isValidCertificate(user.getCertificate());
    }

    public boolean authenticated() {
        return !anonymous();
    }

    public boolean anonymous() {
        return StringUtil.isBlank(user.getUsername());
    }

}
