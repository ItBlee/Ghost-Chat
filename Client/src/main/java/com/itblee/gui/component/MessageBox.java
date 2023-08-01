package com.itblee.gui.component;

import com.itblee.utils.DateUtil;
import com.itblee.utils.PropertyUtil;
import com.itblee.utils.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;
import static com.itblee.constant.Resource.COLOR_WHITE_DEEP_GREY;

public class MessageBox extends JPanel {

    private static final ImageIcon IMG_FAIL = new ImageIcon(RESOURCE_PATH + "images/sendFail.png");
    private static final int LIMIT_MESSAGE_LINE = PropertyUtil.getInt("validate.message.line.limit");

    private JButton message;
    private Date date;
    private boolean isError;
    private boolean isAlert;

    public MessageBox(LayoutManager layout) {
        super(layout);
    }

    public String getText() {
        return message.getText();
    }

    public void setText(String text) {
        message.setText(text);
        revalidate();
    }

    private void setMessage(JButton lbMessage) {
        this.message = lbMessage;
        add(lbMessage);
    }

    public Date getDate() {
        return date;
    }

    private void setDate(JLabel lbTime, Date time) {
        this.date = time;
        add(lbTime);
    }

    public boolean isError() {
        return isError;
    }

    private void setError(boolean error) {
        isError = error;
    }

    public boolean isAlert() {
        return isAlert;
    }

    public void setAlert(boolean alert) {
        isAlert = alert;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        for (Component component : getComponents()) {
            component.setFocusable(focusable);
        }
    }

    public static class Builder {

        private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(10,10,10,10);
        private static final Insets INSETS = new Insets(10, 10, 10, 10);

        private String message;
        private Date sentDate;
        private Color background;
        private Font font;
        private Color fontColor;
        private int fontAlign;
        private int align;
        private boolean isError = false;
        private boolean isAlert = false;

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setSentDate(Date sentDate) {
            this.sentDate = sentDate;
            return this;
        }

        public Builder setBackground(Color background) {
            this.background = background;
            return this;
        }

        public Builder setFont(Font font) {
            this.font = font;
            return this;
        }

        public Builder setFontColor(Color fontColor) {
            this.fontColor = fontColor;
            return this;
        }

        public Builder setFontAlign(int fontAlign) {
            this.fontAlign = fontAlign;
            return this;
        }

        public Builder setAlign(int align) {
            this.align = align;
            return this;
        }

        public Builder alert() {
            this.isAlert = true;
            return this;
        }

        public Builder error() {
            this.isError = true;
            return this;
        }

        public MessageBox build() {
            message = StringUtil.applyWrapForGUI(message);

            MessageBox box = new MessageBox(new FlowLayout(align));
            box.setBackground(COLOR_WHITE_DEEP_GREY);
            box.setOpaque(true);

            JButton lbChat = new JButton(message);
            lbChat.setFont(font);
            lbChat.setForeground(fontColor);
            lbChat.setFocusPainted(false);
            if (message.length() > LIMIT_MESSAGE_LINE * 2)
                lbChat.setBorder(EMPTY_BORDER);
            else lbChat.setBorderPainted(false);
            lbChat.setMargin(INSETS);
            lbChat.setBackground(background);
            lbChat.setOpaque(true);
            lbChat.setHorizontalAlignment(fontAlign);
            String time = sentDate != null ? DateUtil.DateToTimeString(sentDate) : "";
            JLabel lbTime = new JLabel(time);
            lbTime.setVisible(false);
            lbChat.addActionListener(e -> {
                if (StringUtil.isNotBlank(lbTime.getText()))
                    lbTime.setVisible(!lbTime.isVisible());
                StringUtil.copyToClipboard(lbChat.getText());
            });
            if (align == FlowLayout.RIGHT)
                box.setDate(lbTime, sentDate);
            box.setError(isError);
            box.setAlert(isAlert);
            if (isError) {
                box.add(new JLabel(IMG_FAIL));
                if (!lbChat.getText().startsWith("Left"))
                    lbChat.setEnabled(false);
            }
            box.setMessage(lbChat);
            if (align == FlowLayout.LEFT)
                box.setDate(lbTime, sentDate);
            return box;
        }
    }

}
