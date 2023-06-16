package com.itblee.gui.page;

import com.itblee.gui.component.AnimatedImage;
import com.itblee.gui.component.TransitionPane;
import com.itblee.utils.StringUtil;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.itblee.constant.Resource.*;

public class LoginPage extends TransitionPane {

    boolean isHidePassword = true;
    Boolean isLogin;

    public LoginPage() {
        initComponents();
        toLogin();
    }

    private void initComponents() {
        setOpaque(true);
        setFocusable(true);

        JLabel bg = new JLabel();
        title = new JLabel();
        title2 = new JLabel();
        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        lbForgetPwd = new JLabel();
        btnLogin = new JButton();
        lbSignup = new JLabel();
        btnSignup = new JButton();
        JLabel lbEye = new JLabel();

        cover = new AnimatedImage(BG_LOADING_OUTRO, 33, 1);
        success = new AnimatedImage(BG_LOGIN_SUCCESS, 33, 1);

        cover.setVisible(false);
        this.add(cover, JLayeredPane.DEFAULT_LAYER);
        cover.setBounds(0, -38, 365, 735);

        success.setVisible(false);
        this.add(success, JLayeredPane.DEFAULT_LAYER);
        success.setBounds(0, 0, 365, 735);

        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(COLOR_DARK_BLUE);
        this.add(title, JLayeredPane.DEFAULT_LAYER);
        title.setBounds(27, 170, 220, 40);

        title2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title2.setForeground(COLOR_DARK_BLUE);
        this.add(title2, JLayeredPane.DEFAULT_LAYER);
        title2.setBounds(27, 130, 220, 40);

        txtUsername.setFont(FONT_SEGOE_BOLD_16);
        txtUsername.setForeground(COLOR_DARK_BLUE);
        txtUsername.setText("Username");
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 0, 0, 25)));
        txtUsername.setCaretColor(COLOR_DARK_BLUE);
        txtUsername.addActionListener(evt -> {});
        this.add(txtUsername, JLayeredPane.DEFAULT_LAYER);
        txtUsername.setBounds(30, 230, 290, 40);
        txtUsername.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtUsername.getText().equals("Username")) {
                    txtUsername.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isBlank(txtUsername.getText())) {
                    txtUsername.setText("Username");
                }
            }
        });

        char hideChar = txtPassword.getEchoChar();
        lbEye.setIcon(IMAGE_VIEW);
        lbEye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.add(lbEye);
        lbEye.setBounds(300, 325, 16, 16);
        lbEye.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (String.valueOf(txtPassword.getPassword()).equals("Password"))
                    return;
                if (isHidePassword) {
                    lbEye.setIcon(IMAGE_HIDDEN);
                    txtPassword.setEchoChar((char) 0);
                } else {
                    lbEye.setIcon(IMAGE_VIEW);
                    txtPassword.setEchoChar(hideChar);
                }
                isHidePassword = !isHidePassword;
            }
        });

        txtPassword.setFont(FONT_SEGOE_BOLD_16);
        txtPassword.setForeground(COLOR_DARK_BLUE);
        txtPassword.setText("Password");
        txtPassword.setEchoChar((char) 0);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 0, 0, 25)));
        txtPassword.setCaretColor(COLOR_DARK_BLUE);
        txtPassword.addActionListener(evt -> {});
        this.add(txtPassword, JLayeredPane.DEFAULT_LAYER);
        txtPassword.setBounds(30, 310, 290, 40);
        txtPassword.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(txtPassword.getPassword()).equals("Password")) {
                    txtPassword.setText("");
                    if (isHidePassword)
                        txtPassword.setEchoChar(hideChar);
                    else txtPassword.setEchoChar((char) 0);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isBlank(String.valueOf(txtPassword.getPassword()))) {
                    txtPassword.setText("Password");
                    txtPassword.setEchoChar((char) 0);
                    lbEye.setIcon(IMAGE_VIEW);
                    isHidePassword = true;
                }
            }
        });

        lbForgetPwd.setFont(FONT_SEGOE_BOLD_12);
        lbForgetPwd.setForeground(COLOR_GRAY_BLUE);
        lbForgetPwd.setText("Forget  password?");
        lbForgetPwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.add(lbForgetPwd, JLayeredPane.DEFAULT_LAYER);
        lbForgetPwd.setBounds(220, 360, 100, 20);

        btnLogin.setBackground(COLOR_BLUE);
        btnLogin.setFont(FONT_SEGOE_BOLD_16);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.add(btnLogin, JLayeredPane.DEFAULT_LAYER);
        btnLogin.setBounds(30, 430, 290, 50);
        btnLogin.addActionListener(e -> complete());

        lbSignup.setFont(FONT_SEGOE_BOLD_17);
        lbSignup.setForeground(Color.WHITE);
        this.add(lbSignup, JLayeredPane.DEFAULT_LAYER);
        lbSignup.setBounds(85, 600, 250, 30);

        btnSignup.setFont(FONT_SEGOE_BOLD_16);
        btnSignup.setForeground(COLOR_ORANGE);
        btnSignup.setText("Sign up here");
        btnSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSignup.setOpaque(false);
        btnSignup.setContentAreaFilled(false);
        btnSignup.setBorderPainted(false);
        this.add(btnSignup, JLayeredPane.DEFAULT_LAYER);
        btnSignup.setBounds(78, 630, 200, 30);
        btnSignup.addActionListener(e -> switchPage());

        bg.setIcon(BG_LOGIN);
        this.add(bg, JLayeredPane.DEFAULT_LAYER);
        bg.setBounds(0, 0, 365, 735);
    }

    private void switchPage() {
        if (!isLogin)
            toLogin();
        else toSignup();
    }

    private void toLogin() {
        isLogin = true;
        title.setText("Welcome Back");
        title2.setText("Hello,  :)");
        lbForgetPwd.setVisible(true);
        btnLogin.setText("Log In");
        lbSignup.setText("Don't have an account?");
        btnSignup.setText("Sign up here");
    }

    private void toSignup() {
        isLogin = false;
        title.setText("Get Started");
        title2.setText("Let's");
        lbForgetPwd.setVisible(false);
        btnLogin.setText("Sign Up");
        lbSignup.setText("Already have an account?");
        btnSignup.setText("Log in here");
    }

    @Override
    public void doIntro() {
        setFocusable(false);
        cover.setVisible(true);
        cover.startAnimation();
        cover.waitFinish();
        cover.setVisible(false);
        setFocusable(true);
    }

    @Override
    public void doOutro() {

    }

    public void complete() {
        setEnabled(false);
        success.setVisible(true);
        success.startAnimation();
        success.waitFinish();
        success.setVisible(false);
        setEnabled(true);
        //ClientHelper.getFrame().showHome();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Component component : this.getComponents()) {
            component.setEnabled(enabled);
        }
    }

    private AnimatedImage cover;
    private JLabel title;
    private JLabel title2;
    private JLabel lbForgetPwd;
    private JButton btnLogin;
    private JLabel lbSignup;
    private JButton btnSignup;
    private AnimatedImage success;
}
