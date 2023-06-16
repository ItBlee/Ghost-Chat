package com.itblee.core.impl;

import com.google.gson.JsonParseException;
import com.itblee.core.Controller;
import com.itblee.core.Worker;
import com.itblee.transfer.Packet;
import com.itblee.utils.JsonParser;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.UUID;

public class WorkerImpl extends Thread implements Worker {

    protected UUID uid;
    protected final Socket socket;
    protected final BufferedReader reader;
    protected final BufferedWriter writer;
    protected Controller controller;

    protected String lastMessage;
    protected boolean isListening;
    protected Packet result;

    public WorkerImpl(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        if (controller == null)
            throw new IllegalStateException("Worker run without any method");
        while (!isInterrupted() && !socket.isClosed()) {
            try {
                String message;
                try {
                    message = receive();
                } catch (SocketTimeoutException ignored) {
                    close();
                    System.out.println("Worker " + uid + "time out !");
                    break;
                }

                Packet data = JsonParser.fromJson(message, Packet.class)
                        .orElseThrow(() -> new JsonParseException("Invalid message type (require JSON) !"));

                controller.resolve(this, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void send(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
        lastMessage = message;
    }

    @Override
    public void resend() throws IOException {
        send(lastMessage);
    }

    @Override
    public String receive() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        if (reader != null)
            reader.close();
        if (writer != null)
            writer.close();
        if (socket != null)
            socket.close();
        interrupt();
    }

    @Override
    public synchronized void complete(Packet result) throws InterruptedException {
        Objects.requireNonNull(result);
        if (!isListening())
            return;
        this.result = result;
        stopListening();
        notifyAll();
        Thread.sleep(200);
    }

    @Override
    public Packet await() throws InterruptedException {
        return await(0L);
    }

    @Override
    public synchronized Packet await(long timeout) throws InterruptedException {
        startListening();
        long startTime = System.currentTimeMillis();
        while (isListening()) {
            wait(timeout);
            if (timeout > 0L && (System.currentTimeMillis() - timeout) >= startTime) {
                if (result == null)
                    result = new Packet();
                stopListening();
            }
        }
        return getResult();
    }

    @Override
    public boolean isListening() {
        return isListening && !isInterrupted() && !socket.isClosed();
    }

    @Override
    public Packet getResult() {
        Packet temp = result.clone();
        result = null;
        return temp;
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public UUID getUid() {
        return uid;
    }

    @Override
    public void setUid(UUID uid) {
        this.uid = uid;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void setSoTimeout(int timeout) throws SocketException {
        if (timeout < 0)
            throw new IllegalArgumentException("Timeout can't be negative !");
        this.socket.setSoTimeout(timeout);
    }

    private void startListening() {
        isListening = true;
    }

    private void stopListening() {
        isListening = false;
    }

}
