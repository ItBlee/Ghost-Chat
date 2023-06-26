package com.itblee.gui.component;

import com.itblee.gui.ClientFrame;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class AbstractPane extends JLayeredPane {

    private final ClientFrame owner;
    private ClientFrame.Page oldPage;

    private final AnimatedImage cover;

    private boolean lock = false;

    public AbstractPane(ClientFrame owner) {
        this.owner = owner;

        cover = new AnimatedImage(new ImageIcon[] {}, 33, 1);
        cover.setFocusable(false);
        cover.setVisible(false);
        add(cover);
        cover.setBounds(0, 0, 350, 735);
    }

    public void from(ClientFrame.Page oldPage) {
        this.oldPage = oldPage;
    }

    public void doIntro() {
        lock();
        cover.setVisible(true);
        cover.startAnimation();
        cover.waitFinish();
        cover.setVisible(false);
        unlock();
    }

    public void doOutro() {
    }

    public void doOutro(Runnable runnable) {
        lock();
        cover.setVisible(true);
        cover.startAnimation();
        new Thread(() -> {
            cover.waitFinish();
            runnable.run();
            unlock();
        }).start();
    }

    public void reset() {
        cover.setVisible(false);
        unlock();
    }

    public ClientFrame getOwner() {
        return owner;
    }

    public ClientFrame.Page getOldPage() {
        return oldPage;
    }

    public AnimatedImage getCover() {
        return cover;
    }

    protected ActionListener requireNotLock(ActionListener actionListener) {
        return e -> {
            if (!lock)
                actionListener.actionPerformed(e);
        };
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
