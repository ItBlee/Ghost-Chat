package com.itblee.core.Impl;

import com.itblee.core.Controller;
import com.itblee.core.Dispatcher;
import com.itblee.core.Gate;
import com.itblee.core.Worker;
import com.itblee.core.impl.WorkerImpl;
import com.itblee.utils.PropertyUtil;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.rmi.server.ServerNotActiveException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DispatcherImpl extends Thread implements Dispatcher {

    private final Gate gate;
    private final ExecutorService executor;
    private Controller controller;
    private int timeout;

    public DispatcherImpl(Gate gate) {
        this(gate, 0);
    }

    public DispatcherImpl(Gate gate, int workerTimeout) {
        this.gate = gate;
        this.executor = new ThreadPoolExecutor(
                PropertyUtil.getInt("pool.min"),
                PropertyUtil.getInt("pool.max"),
                PropertyUtil.getInt("pool.alive"),
                TimeUnit.valueOf(PropertyUtil.getString("pool.time.unit")),
                new ArrayBlockingQueue<>(PropertyUtil.getInt("pool.queue.capacity")),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.timeout = workerTimeout;
    }

    @Override
    public void run() {
        while (!this.isInterrupted() && !gate.isClosed()) {
            try {
                Socket socket;
                synchronized (gate) {
                    socket = gate.accept();
                }
                Worker worker;
                if (socket instanceof SSLSocket) {
                    worker = new WorkerImpl(socket);
                    System.out.println(worker.getIp() + " connected verify");
                } else {
                    worker = new SecureWorker(socket);
                    System.out.println(worker.getIp() + " connected");
                }
                worker.setController(controller);
                worker.setSoTimeout(timeout);
                executor.execute(worker);
            } catch (SecurityException | ServerNotActiveException | IOException ignored) {}
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
