package Client;

import ClientGUI.ClientGUI;
import ClientGUI.Dialog;
import Security.AES_Encryptor;
import Services.FileHandler;
import Services.StringUtils;
import org.openeuler.com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.*;
import java.net.Socket;
import java.net.UnknownServiceException;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.*;

public class Client {
    public static final int MAIN_PORT = 5000;   //Port trao đổi dữ liệu.
    public static final int VERIFY_PORT = 5005; //Port dùng để đăng nhập và xác minh.
    public static final String SERVER_IP = "localhost";

    //SSL Socket
    public static final String TRUST_STORE_NAME = "myTrustStore.jts"; //store lưu public Key + chứng chỉ.
    public static final String FILE_CONFIG_NAME = "system.conf";
    public static final String CLIENT_SIDE_PATH = "workspace/Client.Side/";
    public static final String TRUST_STORE_PASSWORD = "checksyntax";
    public static final boolean SSL_DEBUG_ENABLE = false;

    public static final String FAIL_CONNECT = "Server Closed";

    public static ClientGUI Frame;
    public static ClientWorker worker;

    public static String UID;
    public static String secretKey;
    public static Socket socket;
    public static BufferedReader in;
    public static BufferedWriter out;
    public static SSLSocket sslSocket;
    public static SSLSocketFactory sslsocketfactory;
    public static DataInputStream inSSL;
    public static DataOutputStream outSSL;

    public static List<String> uidStore;
    public static int line;

    /**
     * Load UID và secretKey cho Client tạo mới nếu chưa tồn tại
     * @return True nếu đã có UID và ngược lại
     */
    public static int config(int index) {
        try {
            String config = uidStore.get(index);
            //Định dạng: UID|secretKey|hash|date
            StringTokenizer tokenizer = new StringTokenizer(config,"|",false);
            UID = tokenizer.nextToken();
            secretKey = tokenizer.nextToken();
            String hash = tokenizer.nextToken();

            //hash là hàm băm từ chuỗi UID có salt là secretKey
            //Nếu băm UID + secretKey ra chuỗi hash không chuỗi hash cũ tức là dữ liệu đã bị thay đổi
            // -> không hợp lệ
            // -> tạo UID + secretKey mới với hàm create()
            if (!StringUtils.applySha256(UID,secretKey).equals(hash))
                throw new IllegalArgumentException();

            System.out.println("ClientID: " + UID);
            System.out.println("Secret key: " + secretKey);
            return index;
        } catch (IndexOutOfBoundsException | NoSuchElementException ignored) {
            //List hiện tại hết uid để lấy thì sẽ throw exception này
            UID = UUID.randomUUID().toString(); //Tạo UID cho Client;
            System.out.println("new ClientID: " + UID);
            create(index);
            return -1;
        } catch (IllegalArgumentException ignored) {
            //Uid + secret key không giống hash
            uidStore.remove(index);
            return config(index);
        }
    }

    /**
     * Tạo secretKey cho Client
     */
    public static void create(int index) {
        secretKey = AES_Encryptor.generate();
        System.out.println("new Secret key: " + secretKey);
        String hash = StringUtils.applySha256(UID,secretKey);
        String config = UID + "|" + secretKey  + "|" + hash + "|" + LocalDateTime.now();
        try {
            uidStore.set(index, config);
        } catch (IndexOutOfBoundsException ignored) {
            uidStore.add(config);
        }
    }

    //Trust Store: certificate và public key
    private static void addProvider() {
        /*Adding the JSSE (Java Secure Socket Extension) provider which provides SSL and TLS protocols
        and includes functionality for data encryption, server authentication, message integrity,
        and optional client authentication.*/
        Security.addProvider(new Provider());
        //specifing the trustStore file which contains the certificate & public of the server
        System.setProperty("javax.net.ssl.trustStore", CLIENT_SIDE_PATH + TRUST_STORE_NAME);
        //specifing the password of the trustStore file
        System.setProperty("javax.net.ssl.trustStorePassword", TRUST_STORE_PASSWORD);
        //This optional and it is just to show the dump of the details of the handshake process
        if (SSL_DEBUG_ENABLE)
            System.setProperty("javax.net.debug","all");
    }

    public static void connect() throws IOException {
        socket = new Socket(SERVER_IP, MAIN_PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Thực hiện kết nối đến Server qua nhiều bước.
     */
    public static void connectServer() throws IOException, NullPointerException, UnknownError {
        connect();
        socket.setSoTimeout(10 * 1000);
        verify();
        socket.setSoTimeout(0);
        //sau khi kết nối thành công -> Tạo Thread lắng nghe thông điệp từ server
        worker = new ClientWorker();
        worker.start();
    }

    /**
     * Thực hiện xác minh
     */
    public static void verify() throws IOException, NullPointerException, UnknownError {
        //Đọc list UID từ file system.conf
        String config = FileHandler.read(CLIENT_SIDE_PATH + FILE_CONFIG_NAME);
        //Truyền list đã đọc vào uidStore
        uidStore = new LinkedList<>(List.of(config.split("\n")));

        line = config(0); //lấy uid đầu tiên trong list
        boolean isVerified = false; //Đã đăng ký phiên làm việc thành công chưa
        do {
            send(UID); //Gửi UID để server kiểm tra

            String verify = receive(); //Nhận kết quả kiểm tra từ Server
            if (verify.equalsIgnoreCase("Banned")) {
                Client.close();
                Dialog.newAlertDialog(Frame,"Got Banned");
                throw new UnknownError("Got Banned Exception !");
            }
            if (verify.equalsIgnoreCase("verified")) { //Thông qua có thể dùng UID và key hiện có
                System.out.println(verify + ": " + UID + " - Key: " + secretKey);
                isVerified = true;
            }
            else { //Ko thông qua -> tạo UID và key mới thử lại
                if (line != -1) { //Lấy được UID từ list, -1 khi list đã hết UID
                    System.out.println(verify + ": " + UID + " - Key: " + secretKey);
                    if (verify.equalsIgnoreCase("duplicated")) {
                        line++;
                        config(line);
                    } else create(line);
                } else line = uidStore.size(); //list hết UID vd có 3 uid 0,1,2 thì set index line ở 3(theo size())

                System.out.println("Sent " + UID + "|" + secretKey + " to server.");
                try {
                    //Thực hiện kết nối SSL socket tới server verifier.
                    openVerify();
                    sendVerify(); //Gửi lại UID + key
                    waitVerify(); //chờ phản hồi ""
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Server verifier not reply !");
                    socket = null;
                    throw new IOException();
                }
            }
        } while (!isVerified);

        //Sau khi xác minh thì viết lại list uid vào system.conf
        FileHandler.write(CLIENT_SIDE_PATH + FILE_CONFIG_NAME, "", false); //clear file trước.
        for (String s : uidStore)
            FileHandler.write(CLIENT_SIDE_PATH + FILE_CONFIG_NAME, s + "\n", true);
    }

    /**
     * Kiểm tra kết nối
     */
    public static boolean checkConnection() {
        if (socket != null)
            return !socket.isClosed();
        return false;
    }

    /**
     * Mở cổng SSL Socket để thực hiện xác minh
     */
    public static void openVerify() throws IOException, NullPointerException {
        //Tạo SSL socket để gửi UID và secretKey một cách an toàn
        addProvider();
        //SSLSSocketFactory thiết lập the ssl context and tạo SSLSocket
        sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        //Tạo SSLSocket bằng SSLServerFactory đã thiết lập ssl context và kết nối tới server
        sslSocket = (SSLSocket) sslsocketfactory.createSocket(SERVER_IP, VERIFY_PORT);
        sslSocket.setSoTimeout(10 * 1000); //set timeout 10s cho read() của socket
        inSSL = new DataInputStream(sslSocket.getInputStream());
        outSSL = new DataOutputStream(sslSocket.getOutputStream());
    }

    /**
     * Gửi UID + secret key cho server để đăng ký phiên làm việc
     */
    public static void sendVerify() throws IOException, NullPointerException {
        outSSL.writeUTF(UID + "|" + secretKey);
    }

    /**
     * Gửi UID + secret key cho server để đăng ký phiên làm việc
     */
    public static void waitVerify() throws IOException, NullPointerException {
        try {
            inSSL.readUTF();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    public static void send(String data) throws IOException, NullPointerException {
        out.write(data);
        out.newLine();
        out.flush();
    }

    public static String receive() throws IOException, NullPointerException {
        return in.readLine();
    }

    /**
     * Đóng kết nối
     */
    public static void close() {
        try {
            in.close();
            out.close();
            socket.close();
            System.out.println(Client.FAIL_CONNECT);
        } catch (Exception ignored) {}
    }
}
