package com.itblee.gui;

import com.itblee.gui.component.MessageBox;
import com.itblee.utils.DateUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

import static com.itblee.constant.Resource.*;

public class ChatUtil {

    public static MessageBox renderReceiveMsg(String message, Date sentDate) {
        return MessageBox.builder()
                .setMessage(message)
                .setSentDate(sentDate)
                .setBackground(COLOR_WHITE_GREY)
                .setFont(FONT_ARIA_PLAIN_16)
                .setFontColor(Color.BLACK)
                .setFontAlign(SwingConstants.LEFT)
                .setAlign(FlowLayout.LEFT)
                .build();
    }

    public static MessageBox renderSendMsg(String message, Date sentDate) {
        return MessageBox.builder()
                .setMessage(message)
                .setSentDate(sentDate)
                .setBackground(COLOR_DEEP_BLUE)
                .setFont(FONT_ARIA_PLAIN_16)
                .setFontColor(Color.WHITE)
                .setFontAlign(SwingConstants.RIGHT)
                .setAlign(FlowLayout.RIGHT)
                .build();
    }

    public static MessageBox renderSendMsgFail(String message, Date sentDate) {
        return MessageBox.builder()
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

    public static MessageBox renderAlert(String message, boolean isError) {
        MessageBox messageBox = MessageBox.builder()
                .setMessage(message)
                .setBackground(isError ? COLOR_LIGHT_RED : COLOR_LIGHT_GREEN)
                .setFont(FONT_ARIA_PLAIN_16)
                .setFontColor(Color.WHITE)
                .setFontAlign(SwingConstants.CENTER)
                .setAlign(FlowLayout.CENTER)
                .alert()
                .build();
        messageBox.setFocusable(false);
        return messageBox;
    }

    public static MessageBox renderTime(Date date) {
        String time;
        if (DateUtil.sameDate(date, new Date())) {
            time = DateUtil.DateToTimeString(date);
        } else time = DateUtil.DateToDayString(date);
        MessageBox messageBox = MessageBox.builder()
                .setMessage(time)
                .setFont(FONT_ARIA_PLAIN_12)
                .setFontColor(Color.BLACK)
                .setFontAlign(SwingConstants.CENTER)
                .setAlign(FlowLayout.CENTER)
                .alert()
                .build();
        messageBox.setFocusable(false);
        return messageBox;
    }

}
