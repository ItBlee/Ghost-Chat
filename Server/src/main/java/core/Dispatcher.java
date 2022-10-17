package core;

import constant.SystemConstant;
import tranfer.Executable;
import worker.Impl.WorkerImpl;
import worker.Worker;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static constant.ServerConstant.*;

public class Dispatcher extends Thread {
    private final Server server;
    private final ExecutorService executor;
    private final Map<String, Executable> methods;

    public Dispatcher(Server server,  Map<String, Executable> methods) {
        this.server = server;
        this.executor = new ThreadPoolExecutor(
                EXECUTOR_CORE,
                EXECUTOR_MAX,
                EXECUTOR_ALIVE_TIME,
                EXECUTOR_TIME_UNIT,
                new ArrayBlockingQueue<>(EXECUTOR_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.methods = methods;
    }

    @Override
    public void run() {
        while (!this.isInterrupted() && !server.isClosed()) {
            try {
                synchronized (server) {
                    Socket socket = server.accept();
                    Worker worker = new WorkerImpl<Worker>(socket, methods, SystemConstant.DEFAULT_SOCKET_TIMEOUT);
                    executor.execute(worker);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        executor.shutdownNow();
    }
}
