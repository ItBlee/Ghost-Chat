package com.itblee.gui;

import com.itblee.utils.StringUtil;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI {

    /**
     * Giao diện nhập và xử lý password khi chạy server
     */
    public static void showAuthenticationFrame() {
        JFrame frame = new JFrame("Authenticator");
        JLabel lblPassword = new JLabel("Password: ");
        JPasswordField pfPassword = new JPasswordField(19);
        DefaultCaret caret = (DefaultCaret) pfPassword.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        pfPassword.setText("hellokitty"); // << password ở đây << Nhập sẵn để demo cho tiện.

        lblPassword.setLabelFor(pfPassword);
        JButton btnGet = new JButton("Start");
        btnGet.setMargin(new Insets(5,30,5,30));
        btnGet.setFont(new Font("Roboto", Font.BOLD, 14));
        btnGet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGet.setFocusPainted(false);
        btnGet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Server.keyStore_password = new String(pfPassword.getPassword());
                //hash password nhập vào và kiểm tra
                String hash = StringUtil.applySha256(Server.keyStore_password, Server.KEY_STORE_PASSWORD_SALT);
                if (hash.equals(Server.KEY_STORE_PASSWORD_HASH)) {
                    frame.dispose();
                    Server.manager.setVisible(true);
                    Main.run();
                } else {
                    pfPassword.setText("");
                    lblPassword.setText("Wrong! ");
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(lblPassword);
        panel.add(pfPassword);
        panel.add(btnGet);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(300, 140);
        frame.setIconImage(icon);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

}
