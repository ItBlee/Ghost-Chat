package core;

import constant.ServerMethods;
import worker.Impl.WorkerImpl;
import worker.Impl.PairWorker;
import worker.Impl.SecureWorker;
import worker.Worker;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.rmi.ServerException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static constant.ServerConstant.*;

public class Launcher {
    private static Launcher launcher;
    private final Server server;
    private final Server secureServer;

    private final Set<PairWorker<Worker>> listenWorkers;
    private final Set<SecureWorker> sessionWorkers;
    private final Map<UUID, String> sessions;

    private Thread accepter;
    private Thread secureAccepter;

    private Launcher(Server server, Server serverSecure) {
        this.server = server;
        this.secureServer = serverSecure;
        listenWorkers = Collections.synchronizedSet(new HashSet<PairWorker<Worker>>());
        sessionWorkers = Collections.synchronizedSet(new HashSet<SecureWorker>());
        sessions = Collections.synchronizedMap(new HashMap<UUID, String>());
    }

    public static synchronized Launcher init(Server server, Server serverSecure) {
        if (launcher != null)
            throw new AssertionError("Already initialized launcher !");

        if (server.isOpened() && server.isSecured())
            throw new IllegalStateException("invalid main Server !");

        if (serverSecure.isOpened() && !serverSecure.isSecured())
            throw new IllegalStateException("invalid security Server !");

        launcher = new Launcher(server, serverSecure);
        return launcher;
    }

    public static synchronized Launcher getInstance() {
        if (launcher == null)
            throw new AssertionError("You have to call init first");
        return launcher;
    }

    public void setup(String password) throws IOException {
        try {
            secureServer.openSecure(password);
        } catch (ServerException ignored) {}
        {
            secureAccepter = new Thread(new Runnable() {
                @Override
                public void run() {
                    ExecutorService executor = new ThreadPoolExecutor(
                            EXECUTOR_CORE,       //Số thread một lúc
                            EXECUTOR_MAX,        //số thread tối đa khi server quá tải
                            EXECUTOR_ALIVE_TIME, //thời gian một thread được sống nếu không làm gì
                            TimeUnit.MINUTES,    //đơn vị phút
                            new ArrayBlockingQueue<>(EXECUTOR_CAPACITY),
                            new ThreadPoolExecutor.CallerRunsPolicy()
                    );
                    while (!secureAccepter.isInterrupted() && !secureServer.isClosed()) {
                        try {
                            synchronized (secureServer) {
                                SSLSocket sslSocket = (SSLSocket) secureServer.accept();
                                SecureWorker worker = new WorkerImpl<Worker>(sslSocket, ServerMethods.methods);
                                sessionWorkers.add(worker);
                                executor.execute(worker);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            secureAccepter.start();
        }

        try {
            server.open();
        } catch (ServerException ignored) {}
        {
            accepter = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Server ready to accept connections.\n");
                    ExecutorService executor = new ThreadPoolExecutor(
                            EXECUTOR_CORE,       //Số thread một lúc
                            EXECUTOR_MAX,        //số thread tối đa khi server quá tải
                            EXECUTOR_ALIVE_TIME, //thời gian một thread được sống nếu không làm gì
                            TimeUnit.MINUTES,    //đơn vị phút
                            new ArrayBlockingQueue<>(EXECUTOR_CAPACITY),
                            new ThreadPoolExecutor.CallerRunsPolicy()
                    ); //Blocking queue để cho request đợi
                    while (!accepter.isInterrupted() && !server.isClosed()) {
                        try {
                            synchronized (server) {
                                Socket socket = server.accept();
                                PairWorker<Worker> worker = new WorkerImpl<>(socket, ServerMethods.methods);
                                listenWorkers.add(worker);
                                executor.execute(worker);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            accepter.start();
        }
    }

    public void close() throws IOException {
        if (listenWorkers != null && !listenWorkers.isEmpty()) {
            listenWorkers.forEach(Thread::interrupt);
            listenWorkers.clear();
        }
        if (sessionWorkers != null && !sessionWorkers.isEmpty()) {
            sessionWorkers.forEach(Thread::interrupt);
            sessionWorkers.clear();
        }
        if (sessions != null && !sessions.isEmpty())
            sessions.clear();
        if (accepter != null && accepter.isAlive())
            accepter.interrupt();
        if (secureAccepter != null && secureAccepter.isAlive())
            secureAccepter.interrupt();
        if (server != null)
            server.close();
        if (secureServer != null)
            secureServer.close();
    }

    public synchronized PairWorker<Worker> getListenWorker(String name) {
        for (PairWorker<Worker> worker:listenWorkers) {
            if (worker.getWorkerName().equals(name))
                return worker;
        }
        return null;
    }

    public synchronized SecureWorker getSessionWorker(String name) {
        for (SecureWorker worker:sessionWorkers) {
            if (worker.getWorkerName().equals(name))
                return worker;
        }
        return null;
    }

    public synchronized Map<UUID, String> getSessions() {
        return sessions;
    }
}
