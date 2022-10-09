package core;

import object.*;
import GUI.ClientGUI;
import com.sun.jdi.connect.spi.ClosedConnectionException;
import constant.ClientMethods;
import utils.JsonParser;
import utils.SecurityUtil;
import utils.StringUtil;
import utils.ValidationUtils;
import worker.Impl.WorkerImpl;
import worker.Impl.PairWorker;
import worker.Impl.SecureWorker;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Launcher {
    private static Launcher launcher;
    private final Client client;
    private final ClientGUI frame;
    private Certificate certificate;
    private PairWorker<UserDTO> worker;

    private Launcher(Client client, ClientGUI frame) {
        this.client = client;
        this.frame = frame;
    }

    public static synchronized Launcher init(Client client, ClientGUI frame) {
        if (launcher != null)
            throw new AssertionError("Already initialized launcher !");
        launcher = new Launcher(client, frame);
        return launcher;
    }

    public static synchronized Launcher getInstance() {
        if (launcher == null)
            throw new AssertionError("You have to call init first");
        return launcher;
    }

    public boolean login(String username, String password) throws Exception {
        if (StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(password))
            return false;
        SSLSocket sslSocket;
        try {
            sslSocket = client.connectSecure();
        } catch (ClosedConnectionException | SocketException e) {
            sslSocket = client.reconnectSecure();
        }
        SecretKey secretKey = SecurityUtil.generateKey();
        Packet packet = new Packet(Header.AUTH_LOGIN);
        packet.getData().put(DataKey.USERNAME, username);
        packet.getData().put(DataKey.PASSWORD, username);
        packet.getData().put(DataKey.SECRET_KEY, secretKey.getKey());
        SecureWorker secureWorker = new WorkerImpl<UserDTO>(sslSocket, ClientMethods.methods, secretKey);
        String json = JsonParser.toJson(packet);
        secureWorker.send(json);
        secureWorker.run();
        return getCertificate() != null;
    }

    public void startWorker() throws IOException {
        if (worker != null && worker.isAlive())
            throw new IllegalThreadStateException();
        if (certificate == null)
            throw new IllegalStateException("Invalid certificate ! Retry to login");
        Socket socket;
        try {
            socket = client.connect();
        } catch (ClosedConnectionException | SocketException e) {
            socket = client.reconnect();
        }
        worker = new WorkerImpl<UserDTO>(socket, ClientMethods.methods, certificate.getSecretKey());
        worker.start();
    }

    public Adapter getAdapter() {
        return new Adapter();
    }

    public Client getClient() {
        return client;
    }

    public ClientGUI getFrame() {
        return frame;
    }

    public PairWorker<UserDTO> getWorker() {
        return worker;
    }

    public synchronized Certificate getCertificate() {
        if (!certificate.isAuthenticated())
            return null;
        return certificate;
    }

    public synchronized void setCertificate(Certificate certificate) {
        if (!ValidationUtils.isValidCertificate(certificate))
            throw new IllegalArgumentException("Certificate invalid or not authenticated !");
        this.certificate = certificate;
    }
}
