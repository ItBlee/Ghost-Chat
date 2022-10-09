package core.Impl;

import com.sun.jdi.connect.spi.ClosedConnectionException;
import core.Client;
import org.openeuler.com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.NotYetConnectedException;

import static constant.ClientConstant.*;
import static constant.SystemConstant.*;

public class ClientImpl implements Client {
    private Socket socket;

    public ClientImpl() {
    }

    @Override
    public Socket connect() throws IOException {
        if (socket == null)
            socket = new Socket(SERVER_IP, MAIN_PORT);
        if (isClosed())
            throw new ClosedConnectionException();
        if (isSecured())
            throw new SocketException("invalid connection !");
        return socket;
    }

    @Override
    public SSLSocket connectSecure() throws IOException {
        if (socket == null) {
            addProvider();
            socket = SSLSocketFactory.getDefault().createSocket(SERVER_IP, VERIFY_PORT);
            socket.setSoTimeout(10 * 1000);
        }
        if (isClosed())
            throw new ClosedConnectionException();
        if (!isSecured())
            throw new SocketException("invalid connection !");
        return (SSLSocket) socket;
    }

    @Override
    public Socket reconnect() throws IOException {
        if (socket == null)
            throw new NotYetConnectedException();
        socket.close();
        socket = null;
        socket = connect();
        return socket;
    }

    @Override
    public SSLSocket reconnectSecure() throws IOException {
        if (socket == null)
            throw new NotYetConnectedException();
        socket.close();
        socket = null;
        socket = connectSecure();
        return (SSLSocket) socket;
    }

    @Override
    public void addProvider() {
        java.security.Security.addProvider(new Provider());
        System.setProperty("javax.net.ssl.trustStore", CLIENT_SIDE_PATH + TRUST_STORE_NAME);
        System.setProperty("javax.net.ssl.trustStorePassword", TRUST_STORE_PASSWORD);
        if (SSL_DEBUG_ENABLE)
            System.setProperty("javax.net.debug","all");
    }

    /**
     * Kiểm tra kết nối
     */
    @Override
    public boolean isClosed() {
        if (socket != null)
            return socket.isClosed();
        return false;
    }

    @Override
    public boolean isSecured() {
        return socket instanceof SSLSocket;
    }

    /**
     * Đóng kết nối
     */
    @Override
    public void close() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {
            // This exception does not need to result in any further action or output
        }
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
