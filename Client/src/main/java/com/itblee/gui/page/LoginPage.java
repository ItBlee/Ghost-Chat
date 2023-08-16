package com.itblee.gui.page;

import com.itblee.core.helper.ClientHelper;
import com.itblee.core.Worker;
import com.itblee.core.helper.TransferHelper;
import com.itblee.gui.Alert;
import com.itblee.gui.ClientFrame;
import com.itblee.gui.component.AbstractPane;
import com.itblee.transfer.Packet;
import com.itblee.utils.IconUtil;
import com.itblee.utils.PropertyUtil;
import com.itblee.utils.StringUtil;
import com.itblee.utils.ValidateUtil;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;
import static com.itblee.constant.Resource.*;

public class LoginPage extends AbstractPane {

    private static final ImageIcon[] COVER_LOGIN_SUCCESS_IN = IconUtil.loadSequence(RESOURCE_PATH + "images/login/success_in");
    private static final String USERNAME_PLACEHOLDER = "Username";
    private static final String PASSWORD_PLACEHOLDER = "Password";

    boolean isHidePassword = true;
    boolean isLogin = true;

    public LoginPage(ClientFrame owner) {
        super(owner);
    }

    @Override
    public void load() {
        super.load();
        toLogin();
    }

    @Override
    public void initComponents() {
        setOpaque(true);
        setFocusable(true);

        JLabel bg = new JLabel();
        title = new JLabel();
        title2 = new JLabel();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        lbForgetPwd = new JLabel();
        btnLogin = new JButton();
        lbSignup = new JLabel();
        btnSignup = new JButton();
        lbEye = new JLabel();

        final ImageIcon imgView = new ImageIcon(RESOURCE_PATH + "images/view.png");
        final ImageIcon imgHide = new ImageIcon(RESOURCE_PATH + "images/hidden.png");

        title.setFont(FONT_SEGOE_BOLD_26);
        title.setForeground(COLOR_DARK_BLUE);
        add(title);
        title.setBounds(27, 170, 220, 40);

        title2.setFont(FONT_SEGOE_BOLD_26);
        title2.setForeground(COLOR_DARK_BLUE);
        add(title2);
        title2.setBounds(27, 130, 220, 40);

        txtUsername.setFont(FONT_SEGOE_BOLD_16);
        txtUsername.setForeground(COLOR_DARK_BLUE);
        txtUsername.setText(USERNAME_PLACEHOLDER);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 0, 0, 25)));
        txtUsername.setCaretColor(COLOR_DARK_BLUE);
        //txtUsername.addActionListener(evt -> {});
        add(txtUsername);
        txtUsername.setBounds(25, 230, 290, 40);
        txtUsername.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isLock())
                    return;
                if (txtUsername.getText().equals(USERNAME_PLACEHOLDER)) {
                    txtUsername.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (isLock())
                    return;
                if (StringUtil.isBlank(txtUsername.getText())) {
                    txtUsername.setText(USERNAME_PLACEHOLDER);
                }
            }
        });

        char hideChar = txtPassword.getEchoChar();
        lbEye.setIcon(imgView);
        lbEye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(lbEye);
        lbEye.setBounds(295, 325, 16, 16);
        lbEye.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isLock())
                    return;
                if (String.valueOf(txtPassword.getPassword()).equals(PASSWORD_PLACEHOLDER))
                    return;
                if (isHidePassword) {
                    lbEye.setIcon(imgHide);
                    txtPassword.setEchoChar((char) 0);
                } else {
                    lbEye.setIcon(imgView);
                    txtPassword.setEchoChar(hideChar);
                }
                isHidePassword = !isHidePassword;
            }
        });

        txtPassword.setFont(FONT_SEGOE_BOLD_16);
        txtPassword.setForeground(COLOR_DARK_BLUE);
        txtPassword.setText(PASSWORD_PLACEHOLDER);
        txtPassword.setEchoChar((char) 0);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 0, 0, 25)));
        txtPassword.setCaretColor(COLOR_DARK_BLUE);
        //txtPassword.addActionListener(evt -> {});
        add(txtPassword);
        txtPassword.setBounds(25, 310, 290, 40);
        txtPassword.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isLock())
                    return;
                if (String.valueOf(txtPassword.getPassword()).equals(PASSWORD_PLACEHOLDER)) {
                    txtPassword.setText("");
                    if (isHidePassword)
                        txtPassword.setEchoChar(hideChar);
                    else txtPassword.setEchoChar((char) 0);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (isLock())
                    return;
                if (StringUtil.isBlank(String.valueOf(txtPassword.getPassword()))) {
                    txtPassword.setText(PASSWORD_PLACEHOLDER);
                    txtPassword.setEchoChar((char) 0);
                    lbEye.setIcon(imgView);
                    isHidePassword = true;
                }
            }
        });

        lbForgetPwd.setFont(FONT_SEGOE_BOLD_12);
        lbForgetPwd.setForeground(COLOR_GRAY_BLUE);
        lbForgetPwd.setText("Forget  password?");
        lbForgetPwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(lbForgetPwd);
        lbForgetPwd.setBounds(215, 360, 100, 20);

        btnLogin.setBackground(COLOR_BLUE);
        btnLogin.setFont(FONT_SEGOE_BOLD_16);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnLogin);
        btnLogin.setBounds(25, 430, 290, 50);
        btnLogin.addActionListener(requireNotLock(e -> new Thread(() -> {
            if (isLogin)
                login();
            else signUp();
        }).start()));

        lbSignup.setFont(FONT_SEGOE_BOLD_17);
        lbSignup.setForeground(Color.WHITE);
        add(lbSignup);
        lbSignup.setBounds(80, 600, 250, 30);

        btnSignup.setFont(FONT_SEGOE_BOLD_16);
        btnSignup.setForeground(COLOR_ORANGE);
        btnSignup.setText("Sign up here");
        btnSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSignup.setOpaque(false);
        btnSignup.setContentAreaFilled(false);
        btnSignup.setBorderPainted(false);
        add(btnSignup);
        btnSignup.setBounds(73, 630, 200, 30);
        btnSignup.addActionListener(requireNotLock(e -> switchPage()));

        bg.setIcon(new ImageIcon(RESOURCE_PATH + "images/login/login.png"));
        add(bg);
        bg.setBounds(0, 0, 350, 735);
    }

    private boolean isNotValidInput(String username, String password) {
        if (!ValidateUtil.isValidUsername(username)
                || username.equalsIgnoreCase(USERNAME_PLACEHOLDER)) {
            Alert.showError("Invalid Name:\nmore than 10 characters");
            return true;
        }
        if (!ValidateUtil.isValidPassword(password)
            || password.equalsIgnoreCase(PASSWORD_PLACEHOLDER)) {
            Alert.showError("Empty Password");
            return true;
        }
        return false;
    }

    private void login() {
        String username = txtUsername.getText();
        String password = String.valueOf(txtPassword.getPassword());
        if (isNotValidInput(username, password))
            return;

        enableInput(false);
        new Thread(() -> {
            try {
                Worker worker = ClientHelper.getConnection();
                TransferHelper.login(username, password);
                await(worker);
            } catch (IOException | InterruptedException e) {
                ClientHelper.getFrame().showDisconnect();
            } finally {
                enableInput(true);
            }
        }).start();
    }

    private void signUp() {
        String username = txtUsername.getText();
        String password = String.valueOf(txtPassword.getPassword());
        if (isNotValidInput(username, password))
            return;

        enableInput(false);
        new Thread(() -> {
            try {
                Worker worker = ClientHelper.getConnection();
                TransferHelper.register(username, password);
                await(worker);
            } catch (IOException | InterruptedException e) {
                ClientHelper.getFrame().showDisconnect();
            } finally {
                enableInput(true);
            }
        }).start();
    }

    private Packet await(Worker worker) throws InterruptedException {
        return worker.await(() -> {
            String temp = btnLogin.getText();
            String[] arr = {"●", "●●", "●●●", "●●●●", "●●●●●"};
            int i = 0;
            long timeout = PropertyUtil.getInt("request.timeout");
            while (timeout > 0 && worker.isListening()) {
                btnLogin.setText(arr[i]);
                if (i == 4) i = 0;
                else i++;
                try {
                    //noinspection BusyWait
                    Thread.sleep(200);
                    timeout -= 200;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            btnLogin.setText(temp);
        });
    }

    private void enableInput(boolean b) {
        btnLogin.setEnabled(b);
        btnSignup.setEnabled(b);
        lbForgetPwd.setEnabled(b);
        lbEye.setEnabled(b);
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
    public void reset() {
        super.reset();
        txtUsername.setText(USERNAME_PLACEHOLDER);
        txtPassword.setText(PASSWORD_PLACEHOLDER);
        txtPassword.setEchoChar((char) 0);
    }

    @Override
    public void doIntro() {
        getCover().setImages(COVER_LOADING_OUTRO);
        getCover().freezeLastFrame(false);
        super.doIntro();
    }

    @Override
    public void doOutro(Runnable runnable) {
        getCover().setImages(COVER_LOGIN_SUCCESS_IN);
        getCover().freezeLastFrame(true);
        super.doOutro(runnable);
    }

    @Override
    protected void lock() {
        super.lock();
        txtUsername.setFocusable(false);
        txtPassword.setFocusable(false);
    }

    @Override
    protected void unlock() {
        super.unlock();
        txtUsername.setFocusable(true);
        txtPassword.setFocusable(true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Component component : this.getComponents()) {
            component.setEnabled(enabled);
        }
    }

    private JLabel title;
    private JLabel title2;
    private JLabel lbForgetPwd;
    private JButton btnLogin;
    private JLabel lbSignup;
    private JButton btnSignup;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lbEye;
}
