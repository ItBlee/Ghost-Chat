package Server;

import Security.AES_Encryptor;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownServiceException;
import java.time.LocalDateTime;
import java.util.StringTokenizer;
import java.util.UUID;

public class SSLVerifier extends Thread implements Runnable {
    protected final SSLSocket sslSocket;
    private final DataInputStream inSSL;
    private final DataOutputStream outSSL;

    /**
     * Thread nhận UID và secret key từ Client và lưu trữ nếu đúng định dạng.
     * @param sslClientSocket truyền SSL socket kết nối Client vào
     */
    public SSLVerifier(SSLSocket sslClientSocket) throws IOException {
        sslSocket = sslClientSocket;
        inSSL = new DataInputStream(sslSocket.getInputStream());
        outSSL = new DataOutputStream(sslClientSocket.getOutputStream());
    }

    public static class VerifyDTO {
        private UUID uid;
        private String secretKey;

        public VerifyDTO(String dto) throws UnknownServiceException {
            parseDTO(dto);
        }

        public UUID getUid() {
            return uid;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void parseDTO(String dto) throws UnknownServiceException {
            if (!dto.contains("|"))
                throw new UnknownServiceException();
            StringTokenizer tokenizer = new StringTokenizer(dto,"|",false);
            if (tokenizer.countTokens() != 2)
                throw new UnknownServiceException();
            this.uid = UUID.fromString(tokenizer.nextToken());
            this.secretKey = tokenizer.nextToken();
            if (secretKey.length() != AES_Encryptor.KEY_BIT_LENGTH) //secretKey phải 16 bit
                throw new UnknownServiceException();
        }
    }

    @Override
    public void run() {
        try {
            String data = receiveRequest();
            //Kiếm tra định dạng: UID|secretKey <- Lưu ý !!!
            VerifyDTO dto = new VerifyDTO(data);

            //Tạo đối tượng user với uid và key vừa nhận
            User user = new User(dto.getUid().toString());
            user.setSecretKey(dto.getSecretKey());
            user.setModifiedDate(LocalDateTime.now().toString());
            user.setSessionTime(System.currentTimeMillis());
            user.setStatus("offline");
            //Thêm mới user với uid và secretKey vừa nhận tùy vào user đó tồn tại hay chưa
            if (!Server.users.add(user)) {
                for (User u : Server.users) {
                    if (u.equals(user)) {
                        u.setSecretKey(user.getSecretKey());
                        u.setSessionTime(user.getSessionTime());
                        break;
                    }
                }
            }

            //phản hồi cho client là đã hoàn tất
            reply("");

            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String receiveRequest() throws IOException {
        return inSSL.readUTF();
    }

    public void reply(String message ) throws IOException {
        outSSL.writeUTF(message);
        outSSL.flush();
    }

    public void close() throws IOException {
        inSSL.close();
        outSSL.close();
        sslSocket.close();
    }
}

