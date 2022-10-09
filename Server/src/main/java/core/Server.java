package core;

import java.io.IOException;
import java.net.Socket;
import java.rmi.server.ServerNotActiveException;

public interface Server extends Cloneable {
    void open() throws IOException;

    void openSecure(String keyStorePassword) throws IOException;

    Socket accept() throws ServerNotActiveException, IOException;

    boolean isOpened();

    boolean isClosed();

    boolean isSecured();

    void close() throws IOException;


    void addProvider(String keyStorePassword);
}
