package com.itblee.core.Impl;

import com.itblee.core.Controller;
import com.itblee.core.Dispatcher;
import com.itblee.core.Gate;
import com.itblee.core.Worker;
import com.itblee.core.impl.WorkerImpl;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.rmi.server.ServerNotActiveException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DispatcherImpl extends Thread implements Dispatcher {

    public static final int EXECUTOR_CORE = 3;

    public static final int EXECUTOR_MAX = 5;

    public static final int EXECUTOR_CAPACITY = 10;

    public static final int EXECUTOR_ALIVE_TIME = 1;

    public static final TimeUnit EXECUTOR_TIME_UNIT = TimeUnit.MINUTES;

    private final Gate parentGate;
    private final ExecutorService executor;
    private Controller controller;
    private int timeout;

    public DispatcherImpl(Gate parentGate) {
        this(parentGate, 0);
    }

    public DispatcherImpl(Gate parentGate, int workerTimeout) {
        this.parentGate = parentGate;
        this.executor = new ThreadPoolExecutor(
                EXECUTOR_CORE,
                EXECUTOR_MAX,
                EXECUTOR_ALIVE_TIME,
                EXECUTOR_TIME_UNIT,
                new ArrayBlockingQueue<>(EXECUTOR_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.timeout = workerTimeout;
    }

    @Override
    public void run() {
        while (!this.isInterrupted() && !parentGate.isClosed()) {
            try {
                Socket socket;
                synchronized (parentGate) {
                    socket = parentGate.accept();
                }
                Worker worker = socket instanceof SSLSocket ? new WorkerImpl(socket) : new SecureWorker(socket);
                worker.setController(controller);
                worker.setSoTimeout(timeout);
                executor.execute(worker);
            } catch (SecurityException | ServerNotActiveException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setTimeout(int timeout) {
        if (timeout < 0)
            throw new IllegalArgumentException("Timeout can't be negative !");
        this.timeout = timeout;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        executor.shutdownNow();
    }
}
