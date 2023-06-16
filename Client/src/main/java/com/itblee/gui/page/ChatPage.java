package com.itblee.gui.page;

import com.itblee.gui.component.TransitionPane;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.gui.Alert;
import com.itblee.gui.ChatUtil;
import com.itblee.gui.ClientFrame;
import com.itblee.gui.component.RoundJTextField;
import com.itblee.core.ClientHelper;
import com.itblee.core.TransferHelper;
import com.itblee.transfer.*;
import com.itblee.utils.IconUtil;
import com.itblee.utils.ObjectUtil;
import com.itblee.utils.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.itblee.constant.ClientConstant.CHAT_AUTO_LEFT_TIME;
import static com.itblee.constant.ClientConstant.LIMIT_INPUT_LINE;
import static com.itblee.constant.Resource.*;

public class ChatPage extends TransitionPane {

    private FriendInfo info;
    private Thread leaveTimer;

    private int scrollBarMaxValue;
    private int messageCount;

    public ChatPage() {
        initComponents();
        messageCount = 0;
    }

    private void initComponents() {
        JPanel chatHeaderPanel = new JPanel();
        btnBack = new JButton();
        lbStatus = new JLabel();
        lbFriendAvatar = new JLabel();
        lbFriendName = new JLabel();
        lbFriendStatus = new JLabel();
        JButton btnInfo = new JButton();
        btnSend = new JButton();
        txtInput = new RoundJTextField(50);
        txtAreaScrollPane = new JScrollPane();
        chatScrollPane = new JScrollPane();
        JPanel jChatBoxPanel = new JPanel();
        jChatPanel = new JPanel();
        inputArea = new JTextArea();

        this.setBackground(Color.WHITE);
        this.setOpaque(true);

        //======== chatHeaderPanel ========
        {
            chatHeaderPanel.setBackground(Color.WHITE);
            chatHeaderPanel.setLayout(null);

            //---- btnBack ----
            btnBack.setBackground(Color.WHITE);
            btnBack.setFocusPainted(false);
            btnBack.setBorderPainted(false);
            btnBack.setIcon(IMAGE_BACK);
            btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnBack.addActionListener(e -> backToHome());
            chatHeaderPanel.add(btnBack);
            btnBack.setBounds(10, 20, 27, 35);

            //---- lbStatus ----
            lbStatus.setIcon(IMAGE_CHECKED);
            chatHeaderPanel.add(lbStatus);
            lbStatus.setBounds(new Rectangle(new Point(75, 40), lbStatus.getPreferredSize()));

            //---- lbPairAvatar ----
            lbFriendAvatar.setIcon(IMAGE_USER);
            chatHeaderPanel.add(lbFriendAvatar);
            lbFriendAvatar.setBounds(new Rectangle(new Point(54, 20), lbFriendAvatar.getPreferredSize()));

            //---- lbPairName ----
            lbFriendName.setText("FRIEND NAME");
            lbFriendName.setFont(FONT_ARIA_BOLD_11);
            lbFriendName.setForeground(COLOR_DARK_BLUE);
            chatHeaderPanel.add(lbFriendName);
            lbFriendName.setBounds(new Rectangle(new Point(100, 24), lbFriendName.getPreferredSize()));

            //---- lbPairName2 ----
            lbFriendStatus.setText("Online");
            lbFriendStatus.setFont(FONT_ARIA_PLAIN_11);
            lbFriendStatus.setBackground(COLOR_LIGHT_GREY);
            lbFriendStatus.setForeground(Color.LIGHT_GRAY);
            chatHeaderPanel.add(lbFriendStatus);
            lbFriendStatus.setBounds(100, 38, 59, 13);

            //---- btnInfo ----
            btnInfo.setBorderPainted(false);
            btnInfo.setFocusPainted(false);
            btnInfo.setBackground(Color.WHITE);
            btnInfo.setIcon(IMAGE_INFO);
            btnInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnInfo.addActionListener(e -> showFriendInfo());
            chatHeaderPanel.add(btnInfo);
            btnInfo.setBounds(302, 21, 28, 29);
        }
        this.add(chatHeaderPanel, JLayeredPane.DEFAULT_LAYER);
        chatHeaderPanel.setBounds(0, 0, 365, 75);

        //---- btnSend ----
        btnSend.setBackground(COLOR_WHITE_DEEP_GREY);
        btnSend.setBorderPainted(false);
        btnSend.setIcon(IMAGE_SEND);
        btnSend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSend.setFocusPainted(false);

        btnSend.addActionListener(e -> sendMessage());
        this.add(btnSend, JLayeredPane.DEFAULT_LAYER);
        btnSend.setBounds(300, 640, 40, 40);

        //---- txtInput ----
        txtInput.setBorder(new EmptyBorder(7,10,5,10));
        txtInput.setBackground(COLOR_WHITE_GREY);
        txtInput.setFont(FONT_ARIA_PLAIN_14);
        txtInput.setForeground(Color.LIGHT_GRAY);
        txtInput.setText("Type message here...");
        this.add(txtInput, JLayeredPane.DEFAULT_LAYER);
        txtInput.setBounds(20, 640, 275, 40);
        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                change();
            }
            public void removeUpdate(DocumentEvent e) {
                change();
            }
            public void insertUpdate(DocumentEvent e) {
                change();
            }

            public void change() {
                if (txtInput.getText().length() > LIMIT_INPUT_LINE || txtInput.getText().endsWith("\n")) {
                    txtInput.setVisible(false);
                    inputArea.setText(txtInput.getText());
                    txtAreaScrollPane.setVisible(true);
                    inputArea.requestFocus();
                    revalidate();
                }
            }
        });
        txtInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtInput.getText().equals("Type message here...")) {
                    txtInput.setText("");
                    txtInput.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isBlank(txtInput.getText())) {
                    txtInput.setText("Type message here...");
                    txtInput.setForeground(Color.LIGHT_GRAY);
                }
            }
        });

        txtInput.addActionListener(e -> txtInput.setText(txtInput.getText() + "\n"));

        //======== scrollPane1 ========
        {

            //---- inputArea ----
            inputArea.setBorder(new EmptyBorder(7,5,5,5));
            inputArea.setBackground(COLOR_WHITE_GREY);
            inputArea.setFont(FONT_ARIA_PLAIN_14);
            inputArea.setLineWrap(true);
            inputArea.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    change();
                }
                public void removeUpdate(DocumentEvent e) {
                    change();
                }
                public void insertUpdate(DocumentEvent e) {
                    change();
                }

                public void change() {
                    if (inputArea.getText().length() <= LIMIT_INPUT_LINE) {
                        txtAreaScrollPane.setVisible(false);
                        txtInput.setText(inputArea.getText());
                        txtInput.setVisible(true);
                        txtInput.requestFocus();
                        revalidate();
                    }
                }
            });
            txtAreaScrollPane.setViewportView(inputArea);
        }
        this.add(txtAreaScrollPane, JLayeredPane.DEFAULT_LAYER);
        txtAreaScrollPane.setBounds(20, 640, 275, 40);
        txtAreaScrollPane.setVisible(false);

        //======== scrollPane2 ========
        {
            //---- jChatBoxPanel ----
            jChatBoxPanel.setBackground(COLOR_WHITE_DEEP_GREY);
            jChatBoxPanel.setOpaque(true);
            jChatBoxPanel.setLayout(new BorderLayout());

            //---- jChatBoxPanel ----
            jChatPanel.setBackground(COLOR_WHITE_DEEP_GREY);
            jChatPanel.setOpaque(true);
            jChatPanel.setLayout(new BoxLayout(jChatPanel, BoxLayout.Y_AXIS));
            jChatPanel.add(Box.createRigidArea(new Dimension(0,10)));

            jChatBoxPanel.add(jChatPanel, BorderLayout.NORTH);
            chatScrollPane.setViewportView(jChatBoxPanel);
            chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);
            chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollBarMaxValue = chatScrollPane.getVerticalScrollBar().getMaximum();
            chatScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
                if ((scrollBarMaxValue - e.getAdjustable().getMaximum()) == 0)
                    return;
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                scrollBarMaxValue = chatScrollPane.getVerticalScrollBar().getMaximum();
            });
        }
        this.add(chatScrollPane, JLayeredPane.DEFAULT_LAYER);
        chatScrollPane.setBounds(-2, 72, 365, 550);
    }

    public void updateInfo(FriendInfo info) {
        ObjectUtil.requireNonNull(info);
        this.info = info;
        lbFriendName.setText(info.getName());
        lbFriendStatus.setText(info.getStatus());

        Icon icon = IconUtil.decode(info.getAvatar());
        if (icon != null)
            lbFriendAvatar.setIcon(icon);

        if (StringUtil.containsIgnoreCase(info.getStatus(), "Offline")) {
            lbStatus.setIcon(IMAGE_REMOVE);
            appendMessage(ChatUtil.renderAlert(info.getName() + " left the chat", true));
            startLeaveTimer();
        } else {
            lbStatus.setIcon(IMAGE_CHECKED);
            appendMessage(ChatUtil.renderAlert(info.getName() + " join the chat", false));
            stopLeaveTimer();
        }
    }

    public void reset() {
        jChatPanel.removeAll();
        jChatPanel.revalidate();
    }

    public void appendMessage(JPanel messagePanel) {
        ObjectUtil.requireNonNull(messagePanel);
        jChatPanel.add(messagePanel);
        jChatPanel.add(Box.createRigidArea(new Dimension(5,0)));
        jChatPanel.revalidate();
        messageCount++;
    }

    private void showFriendInfo() {
        JOptionPane.showMessageDialog(
                ChatPage.this,
                info.toString(),
                "Friend Info",
                JOptionPane.PLAIN_MESSAGE);
    }

    private void sendMessage() {
        btnSend.setEnabled(false);
        String input;
        if (txtAreaScrollPane.isVisible()) {
            input = inputArea.getText();
            txtAreaScrollPane.setVisible(false);
            txtInput.setVisible(true);
            txtInput.requestFocus();
        } else input = txtInput.getText();

        if (StringUtil.isBlank(input))
            return;
        txtInput.setText("");
        inputArea.setText("");
        revalidate();

        try {
            String now = LocalDateTime.now().toString();
            if (isFirstMessage())
                appendMessage(ChatUtil.renderTime(now));
            if (StringUtil.containsIgnoreCase(info.getStatus(), "Online")) {
                Message message = new Message();
                message.setBody(input);
                message.setSentDate(now);
                TransferHelper.sendMessage(message);
                Packet serverRsp = ClientHelper.await();
                if (serverRsp.is(Header.SEND_MESSAGE) && serverRsp.is(StatusCode.CREATED)) {
                    appendMessage(ChatUtil.renderSendMsg(input, now));
                    return;
                }
            }
            appendMessage(ChatUtil.renderSendMsgFail(input, now));
        } catch (IOException | InterruptedException ex) {
            appendMessage(ChatUtil.renderAlert("Disconnected!", true));
        }
        btnSend.setEnabled(true);
    }

    private void backToHome() {
        try {
            TransferHelper.leaveChat();
        } catch (IOException e) {
            Alert.showError("Disconnected");
        }
        ClientHelper.getFrame().goTo(ClientFrame.Page.HOME);
    }

    public void startLeaveTimer() {
        leaveTimer = new Thread(() -> {
            try {
                /*int time = CHAT_AUTO_LEFT_TIME;
                while (time > 0) {
                    Thread.sleep(CHAT_AUTO_LEFT_TIME / (CHAT_AUTO_LEFT_TIME/1000));
                    time -= (CHAT_AUTO_LEFT_TIME / (CHAT_AUTO_LEFT_TIME/1000));
                    //lbLeftAlert.setText(text + (time/1000) + "s");
                }*/
                Thread.sleep(CHAT_AUTO_LEFT_TIME);
                btnBack.doClick();
            } catch (InterruptedException ignored) {}
        });
        leaveTimer.start();
    }

    public void stopLeaveTimer() {
        if (leaveTimer != null) {
            leaveTimer.interrupt();
            leaveTimer = null;
        }
    }

    private boolean isFirstMessage() {
        return messageCount == 0;
    }

    private JButton btnBack;
    private JLabel lbStatus;
    private JLabel lbFriendAvatar;
    private JLabel lbFriendName;
    private JLabel lbFriendStatus;
    private JButton btnSend;
    private JTextField txtInput;
    private JPanel jChatPanel;
    private JScrollPane txtAreaScrollPane;
    private JScrollPane chatScrollPane;
    private JTextArea inputArea;

    @Override
    public void doIntro() {

    }

    @Override
    public void doOutro() {

    }
}
