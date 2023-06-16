package com.itblee.gui;

import com.itblee.utils.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.itblee.constant.ClientConstant.LIMIT_MESSAGE_LINE;
import static com.itblee.constant.Resource.*;

public class ChatUtil {

    public static JPanel renderReceiveMsg(String message, String sentDate) {
        return new MessageBox()
                .setMessage(message)
                .setSentDate(sentDate)
                .setBackground(COLOR_WHITE_GREY)
                .setFont(FONT_ARIA_PLAIN_16)
                .setFontColor(Color.BLACK)
                .setFontAlign(SwingConstants.LEFT)
                .setAlign(FlowLayout.LEFT)
                .build();
    }

    public static JPanel renderSendMsg(String message, String sentDate) {
        return new MessageBox()
                .setMessage(message)
                .setSentDate(sentDate)
                .setBackground(COLOR_DEEP_BLUE)
                .setFont(FONT_ARIA_PLAIN_16)
                .setFontColor(Color.WHITE)
                .setFontAlign(SwingConstants.RIGHT)
                .setAlign(FlowLayout.RIGHT)
                .build();
    }

    public static JPanel renderSendMsgFail(String message, String sentDate) {
        return new MessageBox()
                .setMessage(message)
                .setSentDate(sentDate)
                .setBackground(COLOR_DEEP_BLUE)
                .setFont(FONT_ARIA_PLAIN_16)
                .setFontColor(Color.BLACK)
                .setFontAlign(SwingConstants.RIGHT)
                .setAlign(FlowLayout.RIGHT)
                .error()
                .build();
    }

    public static JPanel renderAlert(String message, boolean isError) {
        return new MessageBox()
                .setMessage(message)
                .setBackground(isError ? COLOR_LIGHT_RED : COLOR_LIGHT_GREEN)
                .setFont(FONT_ARIA_PLAIN_16)
                .setFontColor(Color.WHITE)
                .setFontAlign(SwingConstants.CENTER)
                .setAlign(FlowLayout.CENTER)
                .build();
        /*if (isError) {
            font = new Font("Arial", Font.PLAIN, 12);
            fontColor = new Color(250, 115, 115);
            appendMessageToBox("Left room after 20s", "", null, font, fontColor, fontAlign, align);
            leftRoomTimer();
        }*/
    }

    public static JPanel renderTime(String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(time);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        time = localDateTime.format(formatter);
        return new MessageBox()
                .setMessage(time)
                .setFont(FONT_ARIA_PLAIN_12)
                .setFontColor(Color.BLACK)
                .setFontAlign(SwingConstants.CENTER)
                .setAlign(FlowLayout.CENTER)
                .build();
    }

    private static class MessageBox {
        private String message;
        private String sentDate;
        private Color background;
        private Font font;
        private Color fontColor;
        private int fontAlign;
        private int align;
        private boolean isError = false;

        public MessageBox setMessage(String message) {
            this.message = message;
            return this;
        }

        public MessageBox setSentDate(String sentDate) {
            this.sentDate = sentDate;
            return this;
        }

        public MessageBox setBackground(Color background) {
            this.background = background;
            return this;
        }

        public MessageBox setFont(Font font) {
            this.font = font;
            return this;
        }

        public MessageBox setFontColor(Color fontColor) {
            this.fontColor = fontColor;
            return this;
        }

        public MessageBox setFontAlign(int fontAlign) {
            this.fontAlign = fontAlign;
            return this;
        }

        public MessageBox setAlign(int align) {
            this.align = align;
            return this;
        }

        public MessageBox error() {
            this.isError = true;
            return this;
        }

        public JPanel build() {
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
            lbChat.setBackground(background);
            lbChat.setOpaque(true);
            lbChat.setHorizontalAlignment(fontAlign);
            String time;
            if (StringUtil.isNotBlank(sentDate)) {
                LocalDateTime localDateTime = LocalDateTime.parse(sentDate);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                time = localDateTime.format(formatter);
            }
            else time = sentDate;
            JLabel lbTime = new JLabel(time);
            lbTime.setVisible(false);
            lbChat.addActionListener(e -> {
                lbTime.setVisible(!lbTime.isVisible());
                StringUtil.copyToClipboard(lbChat.getText());
            });

            if (align == FlowLayout.RIGHT)
                panel.add(lbTime);
            if (isError) {
                panel.add(new JLabel(IMAGE_SEND_FAIL));
                if (!lbChat.getText().startsWith("Left"))
                    lbChat.setEnabled(false);
            }
            panel.add(lbChat);
            if (align == FlowLayout.LEFT)
                panel.add(lbTime);
            return panel;
        }
    }

}
