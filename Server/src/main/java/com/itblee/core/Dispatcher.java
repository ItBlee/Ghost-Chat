package com.itblee.core;

public interface Dispatcher extends Runnable {
    void start();
    void setController(Controller controller);
    void setTimeout(int timeout);
    void interrupt();
    boolean isAlive();
    boolean isInterrupted();
}
