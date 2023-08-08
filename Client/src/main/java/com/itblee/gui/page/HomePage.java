package com.itblee.gui.page;

import com.itblee.core.helper.ClientHelper;
import com.itblee.core.helper.TransferHelper;
import com.itblee.core.Worker;
import com.itblee.gui.Alert;
import com.itblee.gui.ClientFrame;
import com.itblee.gui.component.AnimatedImage;
import com.itblee.gui.component.AbstractPane;
import com.itblee.transfer.Packet;
import com.itblee.utils.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;
import static com.itblee.constant.Resource.*;

public class HomePage extends AbstractPane {

    private static final ImageIcon[] COVER_LOGIN_SUCCESS_OUT = IconUtil.loadSequence(RESOURCE_PATH + "images/login/success_out");

    private boolean isFinding = false;

    public HomePage(ClientFrame owner) {
        super(owner);
    }

    @Override
    public void initComponents() {
        JPanel inputPanel = new JPanel();
        btnJoin = new JButton();
        btnQuit = new JButton();

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
                    requireNotLock(e -> new Thread(this::join).start()));
            inputPanel.add(btnJoin);
            btnJoin.setBounds(20, 205, 292, 40);

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
                            new Thread(() -> {
                                if (isFinding)
                                    cancel();
                                else logout();
                            }).start()
                    ));
            inputPanel.add(btnQuit);
            btnQuit.setBounds(20, 245, 292, 40);
        }
        add(inputPanel);
        inputPanel.setBounds(0, 400, 335, 320);

        ImageIcon[] bgHome = IconUtil.loadSequence(RESOURCE_PATH + "images/home");
        bg = new AnimatedImage(bgHome);
        bg.freezeLastFrame(true);
        add(bg);
        bg.setBounds(0, 0, 350, 735);
    }

    private void join() {
        try {
            Worker worker = ClientHelper.getConnection();
            startFinding();
            TransferHelper.requestChat();
            await(worker);
        } catch (InterruptedException e) {
            Alert.showError("Got Error !");
        } catch (IOException | TimeoutException ex) {
            ClientHelper.getFrame().showDisconnect();
        } finally {
            stopFinding();
        }
    }

    private void cancel() {
        try {
            Worker worker = ClientHelper.getConnection();
            if (worker.isListening()) {
                btnQuit.setEnabled(false);
                TransferHelper.stopFindChat();
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Alert.showError("Got Error !");
        } catch (IOException ex) {
            ClientHelper.getFrame().showDisconnect();
        }
    }

    private void logout() {
        try {
            ClientHelper.getConnection();
            TransferHelper.logout();
        } catch (IOException e) {
            ClientHelper.getFrame().showDisconnect();
        } catch (InterruptedException e) {
            Alert.showError("Got Error !");
        }
    }

    private Packet await(Worker worker) throws InterruptedException, TimeoutException {
        return worker.await(() -> {
            btnJoin.setFont(FONT_SEGOE_SEMI_PLAIN_14);
            String[] arr = {"●", "●●", "●●●", "●●●●", "●●●●●"};
            int i = 0;
            while (worker.isListening()) {
                btnJoin.setText(arr[i]);
                if (i == arr.length - 1)
                    i = 0;
                else i++;
                try {
                    //noinspection BusyWait
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
            btnJoin.setFont(FONT_COOPER_BLACK_PLAIN_14);
        });
    }

    private void startFinding() {
        isFinding = true;
        btnJoin.setEnabled(false);
        btnQuit.setText("CANCEL");
        btnQuit.setForeground(COLOR_LIGHT_RED);
    }

    private void stopFinding() {
        isFinding = false;
        btnJoin.setText("JOIN");
        btnQuit.setText("LOGOUT");
        btnJoin.setEnabled(true);
        btnQuit.setEnabled(true);
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

    @Override
    public void doOutro(Runnable runnable) {
        AnimatedImage cover = getCover();
        cover.setImages(COVER_LOADING_INTRO);
        cover.freezeLastFrame(true);
        bg.stopAnimation();
        super.doOutro(runnable);
    }

    @Override
    public void reset() {
        bg.stopAnimation();
        super.reset();
    }

    private AnimatedImage bg;
    private JButton btnJoin;
    private JButton btnQuit;
}
