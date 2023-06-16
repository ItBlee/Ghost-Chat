package com.itblee.gui.page;

import com.itblee.core.Client;
import com.itblee.gui.component.TransitionPane;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static com.itblee.constant.Resource.*;

public class ErrorPage extends TransitionPane {

    public ErrorPage() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(true);
        setFocusable(true);

        icon = new JLabel();
        icon.setIcon(IMAGE_LOADING);
        this.add(icon, JLayeredPane.DEFAULT_LAYER);
        icon.setBounds(65, 140, 226, 226);
        icon.setVisible(false);

        btnReconnect = new JButton();
        btnReconnect.setText("RECONNECT");
        btnReconnect.setBorderPainted(false);
        btnReconnect.setOpaque(false);
        btnReconnect.setFocusPainted(false);
        btnReconnect.setBackground(COLOR_LIGHT_RED);
        btnReconnect.setFont(new Font("Cooper Black", Font.PLAIN, 14));
        btnReconnect.setForeground(Color.WHITE);
        btnReconnect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReconnect.addActionListener(e -> reconnect());
        this.add(btnReconnect, JLayeredPane.DEFAULT_LAYER);
        btnReconnect.setBounds(79, 555, 200, 40);
        btnReconnect.addActionListener(e -> reconnect());

        JLabel bg = new JLabel();
        bg.setIcon(BG_ERROR);
        this.add(bg, JLayeredPane.DEFAULT_LAYER);
        bg.setBounds(0, -20, 365, 735);
    }

    private void reconnect() {
        btnReconnect.setEnabled(false);
        icon.setVisible(true);
        new Thread(() -> {
            try {
                Client client = Client.getInstance();
                client.connect();
                client.getFrame().showLogin();
            } catch (IOException ignored) {
            } finally {
                btnReconnect.setEnabled(true);
                icon.setVisible(false);
            }
        }).start();
    }

    private JButton btnReconnect;
    private JLabel icon;

    @Override
    public void doIntro() {

    }

    @Override
    public void doOutro() {

    }
}
