package GUI.handler;

import core.Adapter;
import utils.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static constant.ClientConstant.*;
import static constant.ClientConstant.AUTO_LEFT_TIME;

public class ChatHandler {

    /**
     * Thêm dòng chat phía bạn chat
     */
    public static void appendReceive(String message, String createdDate) {
        Color bg = new Color(238,241,249);
        Font font = new Font("Arial", Font.PLAIN, 16);
        Color fontColor = new Color(0, 0, 0);
        int fontAlign = SwingConstants.LEFT;
        int align = FlowLayout.LEFT;
        appendMessageToBox(message, createdDate, bg, font, fontColor, fontAlign, align);
    }

    /**
     * Thêm dòng chat phía bản thân
     */
    public static void appendSend(String message, String createdDate) {
        Color bg = new Color(1, 178, 254);
        Font font = new Font("Arial", Font.PLAIN, 16);
        Color fontColor = Color.white;
        int fontAlign = SwingConstants.RIGHT;
        int align = FlowLayout.RIGHT;
        appendMessageToBox(message, createdDate, bg, font, fontColor, fontAlign, align);
    }

    /**
     * Thêm dòng chat lỗi khi ngừng chat
     */
    public static void appendSendFail(String message, String createdDate) {
        Color bg = new Color(1, 178, 254);
        Font font = new Font("Arial", Font.PLAIN, 16);
        Color fontColor = Color.BLACK;
        int fontAlign = SwingConstants.RIGHT;
        int align = FlowLayout.RIGHT;
        appendMessageToBox(message, createdDate, bg, font, fontColor, fontAlign, align);
    }

    /**
     * Thêm dòng chat thông báo
     */
    public static void appendAlert(String message, boolean isError) {
        Color bg;
        if (isError)
            bg = new Color(250, 115, 115);
        else bg = new Color(1, 254, 149);
        Font font = new Font("Arial", Font.PLAIN, 16);
        Color fontColor = Color.WHITE;
        int fontAlign = SwingConstants.CENTER;
        int align = FlowLayout.CENTER;
        appendMessageToBox(message, "", bg, font, fontColor, fontAlign, align);
        if (isError) {
            font = new Font("Arial", Font.PLAIN, 12);
            fontColor = new Color(250, 115, 115);
            appendMessageToBox("Left room after 20s", "", null, font, fontColor, fontAlign, align);
            leftRoomTimer();
        }
    }

    /**
     * Thêm mark thời gian
     */
    @SuppressWarnings("ConstantConditions")
    public static void appendTimeMaker(String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(time);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        time = localDateTime.format(formatter);
        Color bg = null;
        Font font = new Font("Arial", Font.PLAIN, 12);
        Color fontColor = Color.BLACK;
        int fontAlign = SwingConstants.CENTER;
        int align = FlowLayout.CENTER;
        appendMessageToBox(time, "", bg, font, fontColor, fontAlign, align);
    }

    /**
     * Xử lý thêm dòng chat
     */
    private static void appendMessageToBox(String message, String createdDate, Color bg, Font font, Color fontColor, int fontAlign, int align) {
        if (!Adapter.isChatPage())
            return;
        message = StringUtil.applyWrapForGUI(message);

        JPanel panel = new JPanel(new FlowLayout(align));
        panel.setBackground(new Color(249, 253, 255));
        panel.setOpaque(true);

        JButton lbChat = new JButton(message);
        lbChat.setFont(font);
        lbChat.setForeground(fontColor);
        lbChat.setFocusPainted(false);
        if (message.length() > LIMIT_MESSAGE_LINE * 2)
            lbChat.setBorder(new EmptyBorder(10,10,10,10));
        else lbChat.setBorderPainted(false);
        lbChat.setMargin(new Insets(10, 10, 10, 10));
        lbChat.setBackground(bg);
        lbChat.setOpaque(true);
        lbChat.setHorizontalAlignment(fontAlign);
        String time;
        if (!createdDate.equals("")) {
            LocalDateTime localDateTime = LocalDateTime.parse(createdDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            time = localDateTime.format(formatter);
        }
        else time = createdDate;
        JLabel lbTime = new JLabel(time);
        lbTime.setVisible(false);
        lbChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbTime.setVisible(!lbTime.isVisible());
                StringUtil.copyToClipboard(lbChat.getText());
            }
        });

        if (align == FlowLayout.RIGHT)
            panel.add(lbTime);
        if (ClientWorker.pair.getStatus().equalsIgnoreCase("Offline")) {
            JLabel lbError = new JLabel(new ImageIcon("images/sendFail.png"));
            panel.add(lbError);
            if (!lbChat.getText().startsWith("Left"))
                lbChat.setEnabled(false);
        }
        panel.add(lbChat);
        if (align == FlowLayout.LEFT)
            panel.add(lbTime);
        if (Adapter.isFirstMessage() && !createdDate.equals(""))
            appendTimeMaker(createdDate);
        Adapter.appendMessageToChatBox(panel);
    }

    public static void leftRoomTimer() {
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int time = AUTO_LEFT_TIME;
                    while (time > 0) {
                        Thread.sleep(AUTO_LEFT_TIME/(AUTO_LEFT_TIME/1000));
                        time -= (AUTO_LEFT_TIME/(AUTO_LEFT_TIME/1000));
                        //lbLeftAlert.setText(text + (time/1000) + "s");
                    }
                    btnBack.doClick();
                } catch (InterruptedException ignored) {}
            }
        });
        timer.start();
    }

}
