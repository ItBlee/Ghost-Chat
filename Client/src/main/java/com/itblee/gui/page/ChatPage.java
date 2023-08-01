package com.itblee.gui.page;

import com.itblee.gui.component.AbstractPane;
import com.itblee.gui.component.MessageBox;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.gui.ChatUtil;
import com.itblee.gui.ClientFrame;
import com.itblee.gui.component.RoundJTextField;
import com.itblee.core.helper.ClientHelper;
import com.itblee.core.helper.TransferHelper;
import com.itblee.transfer.*;
import com.itblee.utils.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.Date;

import static com.itblee.constant.ClientConstant.*;
import static com.itblee.constant.Resource.*;

public class ChatPage extends AbstractPane {

    private static final String PLACE_HOLDER = "Type message here...";
    private static final int LIMIT_INPUT_LINE = PropertyUtil.getInt("validate.input.message.line.limit");
    private static final int AUTO_LEFT_TIME = PropertyUtil.getInt("Chatroom.auto.leave.time");
    private static final int SPACE_TIME = PropertyUtil.getInt("Chatroom.space.time");

    private FriendInfo info;
    private Thread leaveTimer;

    private int scrollBarMaxValue;
    private Date latestMsgDate;

    public ChatPage(ClientFrame owner) {
        super(owner);
    }

    @Override
    public void initComponents() {
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
        chatPanel = new JPanel();
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
            btnBack.setIcon(new ImageIcon(RESOURCE_PATH + "images/back.png"));
            btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnBack.addActionListener(requireNotLock(e -> backToHome()));
            chatHeaderPanel.add(btnBack);
            btnBack.setBounds(10, 20, 27, 35);

            //---- lbStatus ----
            chatHeaderPanel.add(lbStatus);
            lbStatus.setBounds(new Rectangle(new Point(75, 40), lbStatus.getPreferredSize()));

            //---- lbPairAvatar ----
            lbFriendAvatar.setIcon(new ImageIcon(RESOURCE_PATH + "images/user.png"));
            chatHeaderPanel.add(lbFriendAvatar);
            lbFriendAvatar.setBounds(new Rectangle(new Point(54, 20), lbFriendAvatar.getPreferredSize()));

            //---- lbPairName ----
            lbFriendName.setText("FRIEND NAME");
            lbFriendName.setFont(FONT_ARIA_BOLD_12);
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
            btnInfo.setIcon(new ImageIcon(RESOURCE_PATH + "images/info.png"));
            btnInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnInfo.addActionListener(requireNotLock(e -> showFriendInfo()));
            chatHeaderPanel.add(btnInfo);
            btnInfo.setBounds(302, 21, 28, 29);
        }
        add(chatHeaderPanel);
        chatHeaderPanel.setBounds(0, 0, 350, 75);

        //---- btnSend ----
        btnSend.setBackground(COLOR_WHITE_DEEP_GREY);
        btnSend.setBorderPainted(false);
        btnSend.setIcon(new ImageIcon(RESOURCE_PATH + "images/send.png"));
        btnSend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSend.setFocusPainted(false);
        btnSend.addActionListener(requireNotLock(e -> sendMessage()));
        add(btnSend);
        btnSend.setBounds(290, 640, 40, 40);

        //---- txtInput ----
        txtInput.setBorder(new EmptyBorder(7,10,5,10));
        txtInput.setBackground(COLOR_WHITE_GREY);
        txtInput.setFont(FONT_ARIA_PLAIN_14);
        txtInput.setForeground(Color.LIGHT_GRAY);
        txtInput.setText(PLACE_HOLDER);
        add(txtInput);
        txtInput.setBounds(10, 640, 275, 40);
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
                String input = txtInput.getText();
                if (input.length() > LIMIT_INPUT_LINE
                        || input.contains(System.lineSeparator())) {
                    txtInput.setVisible(false);
                    inputArea.setText(input);
                    txtAreaScrollPane.setVisible(true);
                    inputArea.requestFocus();
                    revalidate();
                }

            }
        });
        txtInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isLock())
                    return;
                if (txtInput.getText().equals(PLACE_HOLDER)) {
                    txtInput.setText("");
                    txtInput.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (isLock())
                    return;
                if (StringUtil.isBlank(txtInput.getText())) {
                    txtInput.setText(PLACE_HOLDER);
                    txtInput.setForeground(Color.LIGHT_GRAY);
                }
            }
        });
        //txtInput.addActionListener(e -> txtInput.setText(txtInput.getText() + System.lineSeparator()));

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
                    String input = inputArea.getText();
                    if (input.length() <= LIMIT_INPUT_LINE) {
                        txtAreaScrollPane.setVisible(false);
                        txtInput.setText(input);
                        txtInput.setVisible(true);
                        txtInput.requestFocus();
                        revalidate();
                    }
                }
            });
            txtAreaScrollPane.setViewportView(inputArea);
        }
        add(txtAreaScrollPane);
        txtAreaScrollPane.setBounds(10, 640, 275, 40);
        txtAreaScrollPane.setVisible(false);

        //======== scrollPane2 ========
        {
            //---- jChatBoxPanel ----
            jChatBoxPanel.setBackground(COLOR_WHITE_DEEP_GREY);
            jChatBoxPanel.setOpaque(true);
            jChatBoxPanel.setLayout(new BorderLayout());

            //---- jChatBoxPanel ----
            chatPanel.setBackground(COLOR_WHITE_DEEP_GREY);
            chatPanel.setOpaque(true);
            chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
            chatPanel.add(Box.createRigidArea(new Dimension(0,10)));

            jChatBoxPanel.add(chatPanel, BorderLayout.NORTH);
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
        add(chatScrollPane);
        chatScrollPane.setBounds(-2, 72, 340, 550);
    }

    public void updateInfo(FriendInfo info) {
        ObjectUtil.requireNonNull(info);
        this.info = info;
        lbFriendName.setText(info.getName());
        lbFriendStatus.setText(info.getStatus());
        Icon icon = IconUtil.decode(info.getAvatar());
        if (icon != null)
            lbFriendAvatar.setIcon(icon);

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
        if (StringUtil.containsIgnoreCase(info.getStatus(), "Offline")) {
            lbStatus.setIcon(new ImageIcon(RESOURCE_PATH + "images/remove.png"));
            MessageBox box = ChatUtil.renderAlert(info.getName() + " left the chat", true);
            appendMessage(box);
            startLeaveTimer(box);
        } else {
            lbStatus.setIcon(new ImageIcon(RESOURCE_PATH + "images/checked.png"));
            appendMessage(ChatUtil.renderAlert(info.getName() + " join the chat", false));
            stopLeaveTimer();
        }
    }

    public void appendMessage(MessageBox box) {
        ObjectUtil.requireNonNull(box);
        if (!box.isAlert()) {
            if (latestMsgDate == null) {
                chatPanel.add(ChatUtil.renderTime(box.getDate()));
            } else  {
                if (DateUtil.between(box.getDate(), latestMsgDate) >= SPACE_TIME) {
                    chatPanel.add(ChatUtil.renderTime(box.getDate()));
                }
            }
            latestMsgDate = box.getDate();
        }
        chatPanel.add(box);
        chatPanel.add(Box.createRigidArea(new Dimension(5,0)));
        chatPanel.revalidate();
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
        try {
            String input = getMessageInput();
            if (StringUtil.isBlank(input))
                return;

            Date now = new Date();
            if (StringUtil.containsIgnoreCase(info.getStatus(), "Offline")) {
                appendMessage(ChatUtil.renderSendMsgFail(input, now));
                return;
            }
            Message message = new Message();
            message.setBody(input);
            message.setSentDate(DateUtil.dateToString(now));
            TransferHelper.sendMessage(message);
            Packet serverRsp = ClientHelper.await();
            if (serverRsp.is(StatusCode.CREATED))
                appendMessage(ChatUtil.renderSendMsg(input, now));
            else appendMessage(ChatUtil.renderSendMsgFail(input, now));
        } catch (IOException | InterruptedException ex) {
            appendMessage(ChatUtil.renderAlert("Disconnected!", true));
        } finally {
            btnSend.setEnabled(true);
        }
    }

    private void backToHome() {
        try {
            TransferHelper.leaveChat();
            quit(() -> ClientHelper.getFrame().showHome());
            stopLeaveTimer();
        } catch (IOException e) {
            ClientHelper.getFrame().showDisconnect();
        }
    }

    private String getMessageInput() {
        String input;
        if (txtAreaScrollPane.isVisible()) {
            input = inputArea.getText();
            txtAreaScrollPane.setVisible(false);
            txtInput.setVisible(true);
            txtInput.requestFocus();
        } else input = txtInput.getText();
        txtInput.setText("");
        inputArea.setText("");
        revalidate();
        return input;
    }

    public void startLeaveTimer(MessageBox box) {
        String temp = box.getText();
        leaveTimer = new Thread(() -> {
            try {
                int time = AUTO_LEFT_TIME;
                int sleep = AUTO_LEFT_TIME / (AUTO_LEFT_TIME/1000);
                while (time > 0) {
                    Thread.sleep(sleep);
                    time -= sleep;
                    box.setText(temp + " (" + (time/1000) + ")");
                }
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

    @Override
    public void doIntro() {
        getCover().setImages(COVER_LOADING_OUTRO);
        getCover().freezeLastFrame(false);
        super.doIntro();
    }

    private void quit(Runnable runnable) {
        getCover().setImages(COVER_LOADING_INTRO);
        getCover().freezeLastFrame(true);
        doOutro(runnable);
    }

    @Override
    public void reset() {
        super.reset();
        chatPanel.removeAll();
        chatPanel.revalidate();
        if (txtAreaScrollPane.isVisible()) {
            txtAreaScrollPane.setVisible(false);
            txtInput.setVisible(true);
            txtInput.requestFocus();
        }
        txtInput.setText("");
        inputArea.setText("");
        latestMsgDate = null;
        stopLeaveTimer();
    }

    @Override
    protected void lock() {
        super.lock();
        txtInput.setFocusable(false);
        inputArea.setFocusable(false);
    }

    @Override
    protected void unlock() {
        super.unlock();
        txtInput.setFocusable(true);
        inputArea.setFocusable(true);
    }

    private JButton btnBack;
    private JLabel lbStatus;
    private JLabel lbFriendAvatar;
    private JLabel lbFriendName;
    private JLabel lbFriendStatus;
    private JButton btnSend;
    private JTextField txtInput;
    private JPanel chatPanel;
    private JScrollPane txtAreaScrollPane;
    private JScrollPane chatScrollPane;
    private JTextArea inputArea;
}
