package com.itblee.gui.component;

import com.itblee.gui.ClientFrame;

import javax.swing.*;

public abstract class AbstractPane extends JLayeredPane {

    private final ClientFrame owner;

    private boolean lock = false;

    public AbstractPane(ClientFrame owner) {
        this.owner = owner;
    }

    public abstract void doIntro();
    public abstract void doOutro();
    public abstract void reset();

    public ClientFrame getOwner() {
        return owner;
    }

    protected void lock() {
        lock = true;
    }

    protected void unlock() {
        lock = false;
    }

    protected boolean isLock() {
        return lock;
    }
}
