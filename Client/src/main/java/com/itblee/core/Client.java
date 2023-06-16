package com.itblee.core;

import com.itblee.core.impl.WorkerImpl;
import com.itblee.gui.ClientFrame;
import com.itblee.constant.ClientMethod;
import com.itblee.core.Impl.ConnectorImpl;
import com.itblee.core.Impl.SecureConnector;
import com.itblee.core.Impl.SecureWorker;
import com.itblee.security.Certificate;
import com.itblee.security.EncryptUtil;
import com.itblee.security.User;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Header;
import com.itblee.transfer.Packet;
import com.itblee.utils.JsonParser;
import com.itblee.utils.ObjectUtil;
import com.itblee.utils.ValidateUtil;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.ConnectException;

import static com.itblee.constant.ClientConstant.*;

public class Client {

    private static Client client;

    private final ClientFrame frame;

    private User user;
    private Connector connector;
    private Worker worker;

    private Client(ClientFrame frame) {
        this.frame = frame;
    }

    public static synchronized Client newInstance(ClientFrame frame) {
        ObjectUtil.requireNonNull(frame);
        if (!frame.isEnabled())
            throw new IllegalArgumentException();
        if (client != null)
            throw new IllegalStateException("Already initialized clientLauncher !");
        client = new Client(frame);
        return client;
    }

    public static synchronized Client getInstance() {
        if (client == null)
            throw new IllegalStateException("You have to call init first");
        return client;
    }

    public Worker connect() throws IOException {
        if (user == null || !ValidateUtil.isValid(user.getCertificate()))
            requestSession();
        if (connector instanceof ConnectorImpl && connector.isConnected())
            return reconnect();
        connector = new ConnectorImpl(IP, PORT);
        worker = new SecureWorker(connector.connect());
        worker.setController(ClientMethod.CONNECT);
        worker.start();
        return worker;
    }

    public Worker reconnect() throws IOException {
        if (!(connector instanceof ConnectorImpl) || !(worker instanceof SecureWorker))
            throw new IllegalStateException("Client not initialized connection.");
        worker.close();
        worker = new SecureWorker(connector.reconnect());
        worker.setController(ClientMethod.CONNECT);
        worker.start();
        return worker;
    }

    public void requestSession() throws IOException {
        if (user == null) {
            Certificate certificate = new Certificate(null, EncryptUtil.generateSecretKey());
            user = new User(certificate);
        } else user.setSecretKey(EncryptUtil.generateSecretKey());

        Connector connector = new SecureConnector(IP, PORT_SECURE, TRUST_STORE_PWD);
        SSLSocket sslSocket = (SSLSocket) connector.connect();

        Packet request = TransferHelper.get();
        request.setHeader(Header.AUTH_REGISTER);
        request.putData(DataKey.SECRET_KEY, user.getSecretKey());
        request.putData(DataKey.SESSION_ID, user.getUid());
        Worker worker = new WorkerImpl(sslSocket);
        worker.setController(ClientMethod.AUTH);
        worker.send(JsonParser.toJson(request));
        worker.run();
    }

    public Worker requireConnection() throws IOException {
        if (connector == null || !connector.isConnected())
            return worker = connect();
        if (connector != null && connector.isClosed())
            return worker = reconnect();
        throw new ConnectException();
    }

    public Connector getConnector() {
        return connector;
    }

    public ClientFrame getFrame() {
        return frame;
    }

    public Worker getWorker() {
        return worker;
    }

    public User getUser() {
        return user;
    }

}
