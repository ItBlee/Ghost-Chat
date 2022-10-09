package worker;

import java.io.IOException;
import java.net.Socket;

public interface Worker extends Runnable {
    void send(String message) throws Exception;

    String receive() throws Exception;

    void close() throws IOException;

    String getUid();

    void setUid(String uid);

    String getWorkerName();

    void setWorkerName(String workerName);

    Socket getSocket();
}
