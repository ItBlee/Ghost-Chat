package Server;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        //Khởi tạo giao diện
        FlatIntelliJLaf.setup();
        ServerManagerGUI.showAuthenticationFrame();
        Server.manager = new ServerManagerGUI();
    }

    public static void run() {
        try {
            Server.open();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //Thread nhận đăng ký UID và secret key (SSL SOCKET - Library: Java Secure Socket Extension)
        {
            Server.verifier = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Server.sslExecutor = Executors.newCachedThreadPool(); //Dùng cached vì verify là task nhỏ xử lý rất nhanh.
                            SSLSocket sslSocket = Server.verifyClient(); //chờ kết nối từ Client để xác minh
                            Server.sslExecutor.execute(new SSLVerifier(sslSocket));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Server.verifier.start();
        }

        //Thread bấm giờ để don dẹp bộ nhớ UID-secretKey theo Session
        {
            Server.timer = new ServerTimer(Server.DEFAULT_TIMER_LOOP, Server.TIMER_SESSION); //run mỗi 10p và thời gian sống của UID client là 60p
            Server.timer.start();
        }

        //Thread accept kết nối từ Client (SOCKET)
        {
            Server.accepter = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Server ready to accept connections.\n");
                    while (true) {
                        try {
                            //Tạo executor quản lý thread pool
                            Server.executor = new ThreadPoolExecutor(
                                    Server.EXECUTOR_CORE,       //Số thread một lúc
                                    Server.EXECUTOR_MAX,        //số thread tối đa khi server quá tải
                                    Server.EXECUTOR_ALIVE_TIME, //thời gian một thread được sống nếu không làm gì
                                    TimeUnit.MINUTES,           //đơn vị phút
                                    new ArrayBlockingQueue<>(Server.EXECUTOR_CAPACITY)); //Blocking queue để cho request đợi

                            Socket socket = Server.acceptClient();
                            Server.executor.execute(new Worker(socket));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Server.accepter.start();
        }
    }
}
