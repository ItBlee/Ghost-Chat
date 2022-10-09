package core;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.AlreadyConnectedException;

public interface Client {
    Socket connect() throws IOException;

    SSLSocket connectSecure() throws IOException;

    Socket reconnect() throws IOException;

    SSLSocket reconnectSecure() throws IOException;

    void addProvider();

    boolean isClosed();

    boolean isSecured();

    void close();

    Socket getSocket();

    void setSocket(Socket socket);
}
