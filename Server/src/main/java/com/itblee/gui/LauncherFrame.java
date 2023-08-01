package com.itblee.gui;

import com.itblee.core.Server;
import com.itblee.security.HashUtil;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.IOException;

import static com.itblee.constant.Resource.ICON;
import static com.itblee.constant.ServerConstant.*;

public class LauncherFrame extends JFrame {

    public LauncherFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Launcher");

        lblPassword = new JLabel("Password: ");
        pfPassword = new JPasswordField(19);
        DefaultCaret caret = (DefaultCaret) pfPassword.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        pfPassword.setText("hellokitty");

        lblPassword.setLabelFor(pfPassword);
        JButton btnLaunch = new JButton("Launch");
        btnLaunch.setMargin(new Insets(5,30,5,30));
        btnLaunch.setFont(new Font("Roboto", Font.BOLD, 14));
        btnLaunch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLaunch.setFocusPainted(false);
        btnLaunch.addActionListener(e -> verify());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(lblPassword);
        panel.add(pfPassword);
        panel.add(btnLaunch);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(300, 140);
        setIconImage(ICON);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        getContentPane().add(panel);
    }

    private void verify() {
        String password = String.valueOf(pfPassword.getPassword());
        String hash = HashUtil.applySha256(password, KEY_STORE_PWD_SALT);
        if (!hash.equals(KEY_STORE_PWD_HASH)) {
            pfPassword.setText("");
            lblPassword.setText("Wrong! ");
        } else {
            try {
                dispose();
                EventQueue.invokeLater(() -> new ServerManagerFrame().setVisible(true));
                Server.getInstance().launch(password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    JLabel lblPassword;
    JPasswordField pfPassword;
}
