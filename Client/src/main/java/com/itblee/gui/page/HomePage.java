package com.itblee.gui.page;

import com.itblee.core.ClientHelper;
import com.itblee.core.TransferHelper;
import com.itblee.core.Worker;
import com.itblee.gui.Alert;
import com.itblee.gui.ClientFrame;
import com.itblee.gui.component.AnimatedImage;
import com.itblee.gui.component.TransitionPane;
import com.itblee.model.FriendInfo;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Packet;
import com.itblee.transfer.StatusCode;
import com.itblee.utils.StringUtil;
import com.itblee.utils.ValidateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

import static com.itblee.constant.Resource.*;

public class HomePage extends TransitionPane {

    private State state;

    enum State {
        NORMAL,
        LOADING,
        DISCONNECT
    }

    public HomePage() {
        initComponents();
        state = State.NORMAL;
    }

    private void initComponents() {
        lbAPPIcon = new JLabel();
        JLabel lbAPPName = new JLabel();
        JPanel inputPanel = new JPanel();
        lbWelcome = new JLabel();
        lbTitles = new JLabel();
        txtName = new JTextField();
        btnJoin = new JButton("JOIN");
        btnQuit = new JButton("QUIT");

        setBackground(Color.WHITE);
        setOpaque(true);
        setFocusable(true);
        requestFocus();

        cover = new AnimatedImage(BG_HOME_INTRO, 45, 1);
        this.add(cover, JLayeredPane.DEFAULT_LAYER);
        cover.setBounds(0, 0, 365, 735);
        cover.setVisible(false);

        //---- lbAPPIcon ----
        lbAPPIcon.setBackground(Color.WHITE);
        lbAPPIcon.setIcon(IMAGE_ICON);
        //this.add(lbAPPIcon, JLayeredPane.DEFAULT_LAYER);
        lbAPPIcon.setBounds(105, 70, 134, 139);

        //======== inputNamePanel ========
        {
            inputPanel.setBackground(COLOR_LIGHT_BLUE);
            inputPanel.setOpaque(false);
            inputPanel.setLayout(null);

            //---- lbWelcome ----
            lbWelcome.setText("   Let's Chat Together");
            lbWelcome.setFont(FONT_SEGOE_BOLD_27);
            lbWelcome.setForeground(Color.WHITE);
            //inputPanel.add(lbWelcome);
            lbWelcome.setBounds(new Rectangle(new Point(25, 40), lbWelcome.getPreferredSize()));

            //---- lbTitles ----
            lbTitles.setText("");
            lbTitles.setFont(FONT_ARIA_PLAIN_14);
            lbTitles.setForeground(Color.WHITE);
            lbTitles.setLabelFor(txtName);
            inputPanel.add(lbTitles);
            lbTitles.setBounds(25, 86, 295, 25);

            //---- txtName ----
            txtName.setBorder(new EmptyBorder(5,15,5,5));
            txtName.setBackground(COLOR_DARK_DEEP_BLUE);
            txtName.setForeground(Color.white);
            txtName.setHorizontalAlignment(SwingConstants.CENTER);
            txtName.setText("WHAT'S YOUR NAME ?");

            txtName.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (txtName.getText().equalsIgnoreCase("WHAT'S YOUR NAME ?")){
                        txtName.setText("");
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if(StringUtil.isBlank(txtName.getText())){
                        txtName.setText("WHAT'S YOUR NAME ?");
                    }
                }
            });
            //inputPanel.add(txtName);
            //txtName.setBounds(25, 110, 290, 40);
            txtName.setBounds(30, 175, 290, 40);

            //---- btnJoin ----
            btnJoin.setText("JOIN");
            btnJoin.setBorderPainted(false);
            btnJoin.setOpaque(false);
            btnJoin.setFocusPainted(false);
            btnJoin.setBackground(COLOR_ORANGE);
            btnJoin.setFont(FONT_COOPER_BLACK_PLAIN_14);
            btnJoin.setForeground(Color.WHITE);
            btnJoin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnJoin.addActionListener(e -> {
                if (state == State.NORMAL) {
                    joinChat();
                }
            });
            inputPanel.add(btnJoin);
            //btnJoin.setBounds(24, 170, 292, 39);
            btnJoin.setBounds(30, 225, 292, 39);

            //---- btnQuit ----
            btnQuit.setText("QUIT");
            btnQuit.setBorderPainted(false);
            btnQuit.setOpaque(false);
            btnQuit.setFocusPainted(false);
            btnQuit.setBackground(COLOR_LIGHT_RED);
            btnQuit.setFont(FONT_SEGOE_SEMI_PLAIN_14);
            btnQuit.setForeground(Color.WHITE);
            btnQuit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnQuit.addActionListener(e -> {
                switch (state) {
                    case NORMAL:
                    case DISCONNECT:
                        quit();
                        break;
                    case LOADING: cancel();
                }
            });
            //inputPanel.add(btnQuit);
            btnQuit.setBounds(24, 215, 292, 39);
        }
        this.add(inputPanel, JLayeredPane.DEFAULT_LAYER);
        inputPanel.setBounds(0, 400, 335, 320);

        //---- lbAPPName ----
        lbAPPName.setBackground(Color.WHITE);
        lbAPPName.setIcon(IMAGE_TITLE);
        //this.add(lbAPPName, JLayeredPane.DEFAULT_LAYER);
        //lbAPPName.setBounds(44, 180, 255, 148);
        lbAPPName.setBounds(0, 0, 365, 735);

        bg = new AnimatedImage(BG_HOME);
        this.add(bg, JLayeredPane.DEFAULT_LAYER);
        bg.setBounds(0, 0, 365, 735);
        bg.startAnimation();
    }

    @Override
    public void doIntro() {
        bg.stopAnimation();
        cover.setVisible(true);
        cover.startAnimation();
        cover.waitFinish();
        bg.startAnimation();
        cover.setVisible(false);
    }

    @Override
    public void doOutro() {

    }

    private void joinChat() {
        String username = txtName.getText();
        if (ValidateUtil.isValidUserName(username)
            || username.equalsIgnoreCase("WHAT'S YOUR NAME ?")) {
            Alert.showError("Invalid Name");
            lbTitles.setText("Must less than 10 characters !");
            return;
        }
        disableComponents();
        new Thread(() -> load(username)).start();
    }

    private void load(String username) {
        try {
            Worker worker = ClientHelper.requireConnection();
            toLoadingState("Checking", "WAIT");

            TransferHelper.registerName(username);
            Packet response = await(worker);
            boolean isNameAvailable = response.is(StatusCode.OK);
            if (!isNameAvailable) {
                Alert.showError("Name Used");
                toNormalState();
                return;
            }
            ClientHelper.getUser().setUsername(username);
            lbTitles.setText("Hi " + username + ", please wait :)");
            lbWelcome.setText("Finding");

            TransferHelper.requestChat();
            waitChat:
            while (true) {
                response = await(worker);
                btnJoin.setText("ASKING");
                String friendName = response.get(DataKey.FRIEND_NAME);
                String message;
                switch (response.getHeader()) {
                    case INVITE_CHAT:
                        message = "Invite friend:\n" + friendName + " ?";
                        Alert.showInvite(message, TransferHelper::responseInvite);
                        break;
                    case CONFIRM_CHAT:
                        message = "Accept invitation:\n" + friendName + " ?";
                        Alert.showConfirm(message, TransferHelper::responseConfirm);
                        break;
                    case CHAT_INFO:
                        switch (response.getCode()) {
                            case FORBIDDEN:
                                Alert.showError(friendName + "\ndeclined your invitation");
                                break;
                            case NOT_FOUND:
                                Alert.showError(friendName + "\nis offline");
                                break;
                            case CREATED:
                                FriendInfo info = response.get(DataKey.FRIEND_INFO);
                                ClientHelper.getFrame().goTo(ClientFrame.Page.CHAT, info);
                                break waitChat;
                        }
                    case STOP_FIND:
                        break waitChat;
                }
            }
        } catch (InterruptedException e) {
            Alert.showError("Got ERROR");
            toNormalState();
        } catch (IOException ex) {
            Alert.showError("Server Closed");
            toDisconnectState();
        }
    }

    private Packet await(Worker worker) throws InterruptedException {
        int i = 0;
        while (worker.isListening()) {
            String[] arr = {"●", "●●", "●●●", "●●●●", "●●●●●"};
            btnJoin.setText(("" + arr[i]));
            if (i == 4)
                i = 0;
            else i++;
            try {
                //noinspection BusyWait
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
        }
        return worker.getResult();
    }

    private void quit() {
        ClientHelper.getFrame().dispose();
        System.exit(0);
    }

    private void cancel() {
        try {
            TransferHelper.stopFindChat();
            Thread.sleep(500);
            toNormalState();
        } catch (IOException | InterruptedException ex) {
            Alert.showError("Server Closed");
            toDisconnectState();
        }
    }

    public void toLoadingState(String alert, String clickTitle) {
        if (state == State.LOADING)
            return;
        disableComponents();
        btnJoin.setText(clickTitle);
        btnJoin.setFont(btnJoin.getFont().deriveFont(Font.BOLD, 24));
        btnQuit.setText("CANCEL");
        lbWelcome.setText(alert);
        lbAPPIcon.setIcon(IMAGE_LOADING);
        state = State.LOADING;
    }

    public void toNormalState() {
        toNormalState("   Let's Chat Together", "JOIN");
    }

    public void toNormalState(String alert, String clickTitle) {
        if (state == State.NORMAL)
            return;
        lbAPPIcon.setIcon(IMAGE_ICON);
        enableComponents();
        btnJoin.setText(clickTitle);
        btnJoin.setFont(FONT_SEGOE_SEMI_PLAIN_14);
        btnQuit.setText("QUIT");
        lbWelcome.setText(alert);
        lbTitles.setText("");
        state = State.NORMAL;
    }

    public void toDisconnectState() {
        if (state == State.DISCONNECT)
            return;
        lbAPPIcon.setIcon(IMAGE_ICON);
        enableComponents();
        btnJoin.setText("RECONNECT");
        btnJoin.setFont(new Font("Cooper Black", Font.PLAIN, 14));
        btnQuit.setText("QUIT");
        lbWelcome.setText("Disconnected");
        lbTitles.setText("");
        state = State.DISCONNECT;
    }

    public void disableComponents() {
        txtName.setEnabled(false);
        btnJoin.setEnabled(false);
    }

    public void enableComponents() {
        txtName.setEnabled(true);
        btnJoin.setEnabled(true);
    }

    private AnimatedImage bg;
    private AnimatedImage cover;
    private JLabel lbAPPIcon;
    private JLabel lbWelcome;
    private JLabel lbTitles;
    private JTextField txtName;
    private JButton btnJoin;
    private JButton btnQuit;
}
