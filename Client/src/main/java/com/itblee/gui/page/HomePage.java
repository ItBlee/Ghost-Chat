package com.itblee.gui.page;

import com.itblee.core.ClientHelper;
import com.itblee.core.TransferHelper;
import com.itblee.core.Worker;
import com.itblee.gui.Alert;
import com.itblee.gui.ClientFrame;
import com.itblee.gui.component.AnimatedImage;
import com.itblee.gui.component.AbstractPane;
import com.itblee.model.FriendInfo;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Packet;
import com.itblee.transfer.StatusCode;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static com.itblee.constant.Resource.*;

public class HomePage extends AbstractPane {

    private State state;

    enum State {
        NORMAL,
        LOADING,
        DISCONNECT
    }

    public HomePage(ClientFrame owner) {
        super(owner);
        initComponents();
        state = State.NORMAL;
    }

    private void initComponents() {
        JPanel inputPanel = new JPanel();
        btnJoin = new JButton("JOIN");
        btnQuit = new JButton("QUIT");

        setBackground(Color.WHITE);
        setOpaque(true);
        setFocusable(true);
        requestFocus();

        //======== inputNamePanel ========
        {
            inputPanel.setBackground(COLOR_LIGHT_BLUE);
            inputPanel.setOpaque(false);
            inputPanel.setLayout(null);

            //---- btnJoin ----
            btnJoin.setText("JOIN");
            btnJoin.setBorderPainted(false);
            btnJoin.setOpaque(false);
            btnJoin.setFocusPainted(false);
            btnJoin.setBackground(COLOR_ORANGE);
            btnJoin.setFont(FONT_COOPER_BLACK_PLAIN_14);
            btnJoin.setForeground(Color.WHITE);
            btnJoin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnJoin.addActionListener(
                    requireNotLock(e ->
                            quit(() -> getOwner().showChat())
                    ));
            inputPanel.add(btnJoin);
            btnJoin.setBounds(30, 205, 292, 40);

            //---- btnQuit ----
            btnQuit.setText("LOGOUT");
            btnQuit.setContentAreaFilled(false);
            btnQuit.setBorderPainted(false);
            btnQuit.setOpaque(false);
            btnQuit.setFocusPainted(false);
            btnQuit.setFont(FONT_COOPER_BLACK_PLAIN_14);
            btnQuit.setForeground(COLOR_ORANGE);
            btnQuit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnQuit.addActionListener(
                    requireNotLock(e ->
                            quit(() -> getOwner().showLogin())
                    ));
            inputPanel.add(btnQuit);
            btnQuit.setBounds(30, 245, 292, 40);
        }
        add(inputPanel);
        inputPanel.setBounds(0, 400, 335, 320);

        bg = new AnimatedImage(BG_HOME);
        bg.freezeLastFrame(true);
        add(bg);
        bg.setBounds(0, 0, 350, 735);
    }

    @Override
    public void doIntro() {
        AnimatedImage cover = getCover();
        if (getOldPage() == ClientFrame.Page.LOGIN)
            cover.setImages(COVER_LOGIN_SUCCESS_OUT);
        else cover.setImages(COVER_LOADING_OUTRO);
        cover.freezeLastFrame(false);
        super.doIntro();
        bg.startAnimation();
    }

    private void quit(Runnable runnable) {
        AnimatedImage cover = getCover();
        cover.setImages(COVER_LOADING_INTRO);
        cover.freezeLastFrame(true);
        bg.stopAnimation();
        doOutro(runnable);
    }

    @Override
    public void reset() {
        bg.stopAnimation();
        super.reset();
    }

    private void joinChat() {
        /*String username = txtName.getText();
        if (ValidateUtil.isValidUserName(username)
            || username.equalsIgnoreCase("WHAT'S YOUR NAME ?")) {
            Alert.showError("Invalid Name");
            lbTitles.setText("Must less than 10 characters !");
            return;
        }*/
        disableComponents();
        //new Thread(() -> load(username)).start();
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
            /*lbTitles.setText("Hi " + username + ", please wait :)");
            lbWelcome.setText("Finding");*/

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
        /*lbWelcome.setText(alert);
        lbAPPIcon.setIcon(IMAGE_LOADING);*/
        state = State.LOADING;
    }

    public void toNormalState() {
        toNormalState("   Let's Chat Together", "JOIN");
    }

    public void toNormalState(String alert, String clickTitle) {
        if (state == State.NORMAL)
            return;
        //lbAPPIcon.setIcon(IMAGE_ICON);
        enableComponents();
        btnJoin.setText(clickTitle);
        btnJoin.setFont(FONT_SEGOE_SEMI_PLAIN_14);
        btnQuit.setText("QUIT");
        /*lbWelcome.setText(alert);
        lbTitles.setText("");*/
        state = State.NORMAL;
    }

    public void toDisconnectState() {
        if (state == State.DISCONNECT)
            return;
        //lbAPPIcon.setIcon(IMAGE_ICON);
        enableComponents();
        btnJoin.setText("RECONNECT");
        btnJoin.setFont(new Font("Cooper Black", Font.PLAIN, 14));
        btnQuit.setText("QUIT");
        /*lbWelcome.setText("Disconnected");
        lbTitles.setText("");*/
        state = State.DISCONNECT;
    }

    public void disableComponents() {
        btnJoin.setEnabled(false);
    }

    public void enableComponents() {
        btnJoin.setEnabled(true);
    }

    private AnimatedImage bg;
    private JButton btnJoin;
    private JButton btnQuit;
}
