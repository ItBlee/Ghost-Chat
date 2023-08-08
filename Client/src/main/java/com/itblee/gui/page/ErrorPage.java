package com.itblee.gui.page;

import com.itblee.core.ClientContainer;
import com.itblee.gui.Alert;
import com.itblee.gui.ClientFrame;
import com.itblee.gui.component.AbstractPane;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;
import static com.itblee.constant.Resource.*;

public class ErrorPage extends AbstractPane {

    public ErrorPage(ClientFrame owner) {
        super(owner);
    }

    @Override
    public void initComponents() {
        setOpaque(true);
        setFocusable(true);

        icon = new JLabel();
        icon.setIcon(new ImageIcon(RESOURCE_PATH + "images/loading.gif"));
        add(icon);
        icon.setBounds(65, 140, 226, 226);
        icon.setVisible(false);

        btnReconnect = new JButton();
        btnReconnect.setText("RECONNECT");
        btnReconnect.setBorderPainted(false);
        btnReconnect.setOpaque(false);
        btnReconnect.setFocusPainted(false);
        btnReconnect.setBackground(COLOR_LIGHT_RED);
        btnReconnect.setFont(FONT_COOPER_BLACK_PLAIN_14);
        btnReconnect.setForeground(Color.WHITE);
        btnReconnect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReconnect.addActionListener(e -> reconnect());
        add(btnReconnect);
        btnReconnect.setBounds(79, 555, 200, 40);

        JLabel bg = new JLabel();
        bg.setIcon(new ImageIcon(RESOURCE_PATH + "images/disconnect.png"));
        add(bg);
        bg.setBounds(0, -20, 350, 735);
    }

    private void reconnect() {
        btnReconnect.setEnabled(false);
        icon.setVisible(true);
        new Thread(() -> {
            try {
                ClientContainer.client.getConnection();
                ClientContainer.frame.showLogin();
            } catch (IOException ignored) {
            } catch (InterruptedException e) {
                Alert.showError("Got Error !");
            } finally {
                btnReconnect.setEnabled(true);
                icon.setVisible(false);
            }
        }).start();
    }

    private JButton btnReconnect;
    private JLabel icon;

}
