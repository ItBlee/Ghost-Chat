package core;

import DTO.UserDTO;
import GUI.ClientGUI;
import com.sun.jdi.connect.spi.ClosedConnectionException;
import constant.ClientMethods;
import constant.SystemConstant;
import security.Certificate;
import security.SecurityUtil;
import tranfer.DataKey;
import tranfer.Header;
import tranfer.Packet;
import utils.JsonParser;
import utils.ValidationUtil;
import worker.Impl.AbstractWorker;
import worker.Impl.PairWorker;
import worker.Impl.WorkerImpl;

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
        SSLSocket sslSocket;
        try {
            sslSocket = client.connectSecure();
        } catch (ClosedConnectionException | SocketException e) {
            sslSocket = client.reconnectSecure();
        }
        String secretKey = SecurityUtil.generateKey();
        Packet request = new Packet();
        request.setHeader(Header.AUTH_LOGIN);
        request.putData(DataKey.USERNAME, username);
        request.putData(DataKey.PASSWORD, password);
        request.putData(DataKey.SECRET_KEY, secretKey);
        AbstractWorker secureWorker
                = new WorkerImpl<UserDTO>(sslSocket, ClientMethods.methods, secretKey, SystemConstant.DEFAULT_SOCKET_TIMEOUT);
        String json = JsonParser.toJson(request);
        secureWorker.send(json);
        secureWorker.run();
        return getCertificate() != null;
    }

    public boolean register(String username, String password) throws Exception {
        SSLSocket sslSocket;
        try {
            sslSocket = client.connectSecure();
        } catch (ClosedConnectionException | SocketException e) {
            sslSocket = client.reconnectSecure();
        }
        String secretKey = SecurityUtil.generateKey();
        Packet request = new Packet();
        request.setHeader(Header.AUTH_REGISTER);
        request.putData(DataKey.USERNAME, username);
        request.putData(DataKey.PASSWORD, password);
        request.putData(DataKey.SECRET_KEY, secretKey);
        AbstractWorker secureWorker
                = new WorkerImpl<UserDTO>(sslSocket, ClientMethods.methods, SystemConstant.DEFAULT_SOCKET_TIMEOUT);
        String json = JsonParser.toJson(request);
        secureWorker.send(json);
        secureWorker.run();
        return getCertificate() != null;
    }

    public void launch() throws IOException {
        if (worker != null)
            throw new IllegalThreadStateException();
        if (certificate == null || !certificate.isAuthenticated())
            throw new IllegalStateException("Invalid certificate ! Retry to login");
        Socket socket;
        try {
            socket = client.connect();
        } catch (ClosedConnectionException | SocketException e) {
            socket = client.reconnect();
        }
        worker = new WorkerImpl<UserDTO>(socket, ClientMethods.methods, certificate.getSecretKey(), SystemConstant.DEFAULT_SOCKET_TIMEOUT);
        new Thread(worker).start();
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
        if (!ValidationUtil.isValidCertificate(certificate))
            throw new IllegalArgumentException("Certificate invalid or not authenticated !");
        this.certificate = certificate;
    }
}
