package worker.Impl;

import security.SecurityUtil;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public abstract class SecureWorker extends AbstractWorker {
    protected final String secretKey;

    public SecureWorker(Socket socket) throws IOException, NoSuchAlgorithmException {
       this(socket, SecurityUtil.generateKey());
    }

    public SecureWorker(Socket socket, String secretKey) throws IOException {
        super(socket);
        this.secretKey = secretKey;
    }

    @Override
    public void send(String message) throws Exception {
        if (!(socket instanceof SSLSocket))
            message = SecurityUtil.encrypt(message, secretKey);
        super.send(message);
    }

    @Override
    public String receive() throws Exception {
        String receiveMsg = super.receive();
        if (!(socket instanceof SSLSocket))
            return SecurityUtil.decrypt(receiveMsg, secretKey);
        return receiveMsg;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
