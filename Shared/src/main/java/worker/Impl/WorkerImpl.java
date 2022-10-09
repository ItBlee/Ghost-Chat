package worker.Impl;

import com.google.gson.JsonParseException;
import object.*;
import utils.JsonParser;
import utils.SecurityUtil;
import utils.StringUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class WorkerImpl<T> extends PairWorker<T> {
    private final Map<Header, Executable> methods;

    public WorkerImpl(Socket socket, Map<Header, Executable> methods) throws IOException, NoSuchAlgorithmException {
        this(socket, methods, SecurityUtil.generateKey(), 0);
    }

    public WorkerImpl(Socket socket, Map<Header, Executable> methods, SecretKey secretKey) throws IOException {
        this(socket, methods, secretKey, 0);
    }

    public WorkerImpl(Socket socket, Map<Header, Executable> methods, SecretKey secretKey, int timeOut) throws IOException {
        super(socket, secretKey);
        this.methods = methods;
        this.socket.setSoTimeout(timeOut);
    }

    @Override
    public void run() {
        while (!this.isInterrupted() && !socket.isClosed()) {
            try {
                String message;
                try {
                    message = receive();
                } catch (SocketTimeoutException ignored) {
                    throw new SocketTimeoutException("Socket time out !");
                }
                if (StringUtil.isNullOrEmpty(message))
                    throw new IllegalArgumentException();
                Packet packet = JsonParser.fromJson(message, Packet.class);
                if (packet == null)
                    throw new JsonParseException("invalid message format !");
                String senderID = packet.getData().get(DataKey.SENDER);
                if (!StringUtil.isNullOrEmpty(senderID))
                    setUid(senderID);

                methods.get(packet.getHeader())
                        .run(this, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            Packet packet = new Packet(Header.BREAK_CONNECT);
            String json = JsonParser.toJson(packet);
            send(json);
        } catch (Exception ignored) {}
        super.close();
        this.interrupt();
    }
}
