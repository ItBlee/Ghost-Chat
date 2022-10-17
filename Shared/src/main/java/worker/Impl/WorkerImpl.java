package worker.Impl;

import security.SecurityUtil;
import tranfer.DataKey;
import tranfer.Executable;
import tranfer.Packet;
import utils.JsonParser;
import utils.StringUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class WorkerImpl<T> extends PairWorker<T> {
    private final Map<String, Executable> methods;

    public WorkerImpl(Socket socket, Map<String, Executable> methods) throws IOException, NoSuchAlgorithmException {
        this(socket, methods, SecurityUtil.generateKey(), 0);
    }

    public WorkerImpl(Socket socket, Map<String, Executable> methods, int timeOut) throws IOException, NoSuchAlgorithmException {
        this(socket, methods, SecurityUtil.generateKey(), timeOut);
    }

    public WorkerImpl(Socket socket, Map<String, Executable> methods, String secretKey) throws IOException {
        this(socket, methods, secretKey, 0);
    }

    public WorkerImpl(Socket socket, Map<String, Executable> methods, String secretKey, int timeOut) throws IOException {
        super(socket, secretKey);
        this.methods = methods;
        this.socket.setSoTimeout(timeOut);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
            try {
                String message;
                try {
                    message = receive();
                } catch (SocketTimeoutException ignored) {
                    close();
                    throw new SocketTimeoutException("Socket time out !");
                }
                if (StringUtil.isNullOrEmpty(message))
                    continue;
                Packet packet = JsonParser.fromJson(message, Packet.class);
                if (packet == null)
                    continue;
                String senderID = packet.getData(DataKey.UID);
                if (!StringUtil.isNullOrEmpty(senderID))
                    setUid(senderID);

                methods.get(packet.getHeader())
                        .run(this, packet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
