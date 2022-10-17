package worker.Impl;

import worker.Worker;

import java.io.*;
import java.net.Socket;

public abstract class AbstractWorker implements Worker {
    protected String uid;
    protected String workerName;
    protected final Socket socket;
    protected final BufferedReader reader;
    protected final BufferedWriter writer;

    public AbstractWorker(Socket socket) throws IOException {
        if (socket == null)
            throw new IllegalArgumentException();
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void send(String message) throws Exception {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    @Override
    public String receive() throws Exception {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getWorkerName() {
        return workerName;
    }

    @Override
    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }
}
