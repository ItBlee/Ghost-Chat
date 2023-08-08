package com.itblee.gui.component;

import com.itblee.utils.function.Choosable;
import com.itblee.utils.PropertyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;
import static com.itblee.constant.Resource.*;

public class Dialog extends JDialog {

	private Thread timer;

	public static class Builder {
		private JFrame owner;
		private Choosable choosable;
		private String message = "ALERT";
		private boolean isAlert = false;
		private AnimatedImage icon = new AnimatedImage(null);
		private Color fontColor = COLOR_DARK_BLUE;
		private String acceptTitle = "OK";
		private String declineTitle = "CANCEL";

		public Builder setOwner(JFrame owner) {
			this.owner = owner;
			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder reply(Choosable choosable) {
			this.choosable = choosable;
			return this;
		}

		public Builder setIcon(ImageIcon... icons) {
			this.icon.setImages(icons);
			return this;
		}

		public Builder setFontColor(Color fontColor) {
			this.fontColor = fontColor;
			return this;
		}

		public Builder setAcceptTitle(String acceptTitle) {
			this.acceptTitle = acceptTitle;
			return this;
		}

		public Builder setDeclineTitle(String declineTitle) {
			this.declineTitle = declineTitle;
			return this;
		}

		public Dialog build() {
			Objects.requireNonNull(owner);
			Objects.requireNonNull(message);
			if (choosable == null)
				isAlert = true;
			return new Dialog(this);
		}
	}

	private Dialog(Builder builder) {
		super(builder.owner);
		this.owner = builder.owner;
		this.message = builder.message;
		this.isAlert = builder.isAlert;
		this.choosable = builder.choosable;
		this.icon = builder.icon;
		this.fontColor = builder.fontColor;
		this.acceptTitle = builder.acceptTitle;
		this.declineTitle = builder.declineTitle;
		initComponents();
	}

	public static Builder builder() {
		return new Builder();
	}

	private void initComponents() {
		JLabel bg = new JLabel();
		JPanel mainPanel = new JPanel();
		JLabel lbContent = new JLabel();
		JPanel buttonBar = new JPanel();
		okButton = new JButton();
		declineButton = new JButton();

		//======== this ========
		setResizable(false);
		setUndecorated(true);
		getRootPane().setOpaque(false);
		setBackground(COLOR_TRANSPARENT);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(COLOR_TRANSPARENT);

		//======== buttonBar ========
		{
			buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
			buttonBar.setOpaque(false);
			buttonBar.setLayout(new FlowLayout());

			//---- okButton ----
			okButton.setText(acceptTitle);
			okButton.setBackground(COLOR_DARK_BLUE);
			okButton.setFont(FONT_SEGOE_BOLD_17);
			okButton.setForeground(Color.WHITE);
			okButton.setFocusPainted(false);
			okButton.setBorderPainted(false);
			okButton.setMargin(new Insets(10, 15, 10, 15));
			okButton.setOpaque(false);
			okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			okButton.addActionListener(e -> onClickAccept());
			buttonBar.add(okButton);
			if (okButton.getWidth() < 140)
				okButton.setPreferredSize(new Dimension(140, 50));

			//---- cancelButton ----
			declineButton.setText(declineTitle);
			declineButton.setContentAreaFilled(false);
			declineButton.setFont(FONT_SEGOE_BOLD_17);
			declineButton.setForeground(COLOR_DARK_BLUE);
			declineButton.setFocusPainted(false);
			declineButton.setBorderPainted(false);
			declineButton.setMargin(new Insets(10, 20, 10, 20));
			declineButton.setOpaque(false);
			declineButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			declineButton.addActionListener(e -> onClickDecline());
			if (!isAlert)
				buttonBar.add(declineButton);
		}
		contentPane.add(buttonBar);
		buttonBar.setBounds(0, 290, 350, 358);

		contentPane.add(Box.createRigidArea(new Dimension(10,30)));

		contentPane.add(icon);
		icon.setBounds(0, -23, 350, 200);

		{
			mainPanel.setOpaque(false);
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
			lbContent.setText(message);
			lbContent.setFont(FONT_ARIA_BOLD_26);
			lbContent.setBackground(Color.WHITE);
			lbContent.setForeground(fontColor);
			lbContent.setHorizontalAlignment(SwingConstants.CENTER);
			mainPanel.add(lbContent);
		}
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setBounds(0, 210, 350, 200);

		bg.setIcon(new ImageIcon(RESOURCE_PATH + "images/dialog/dialog.png"));
		contentPane.add(bg);

		setSize(350, 430);
		setLocationRelativeTo(getOwner());
		setLocation(getX()+1,getY() + 180);
	}

	public void display() {
		startTimer();
		setVisible(true);
		owner.setEnabled(false);
		owner.getRootPane().getGlassPane().setVisible(true);
		icon.startAnimation();
	}

	private void exit() {
		owner.setEnabled(true);
		owner.getRootPane().getGlassPane().setVisible(false);
		dispose();
		icon.stopAnimation();
		if (timer != null)
			timer.interrupt();
	}

	private void onClickAccept() {
		exit();
		if (choosable != null) {
			try {
				choosable.reply(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void onClickDecline() {
		exit();
		if (choosable != null) {
			try {
				choosable.reply(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void startTimer() {
		JButton button = isAlert ? okButton : declineButton;
		timer = new Thread(() -> {
			int timeout = PropertyUtil.getInt("request.timeout");
			int timer = timeout;
			String title = button.getText();
			try {
				while (timer > 0) {
					button.setText(title + " (" + (timer / 1000) + ")");
					Thread.sleep(timeout / 10);
					timer -= (timeout / 10);
				}
			} catch (InterruptedException ignored) {}
			button.doClick();
		});
		timer.start();
	}

	private final Choosable choosable;
	private final JFrame owner;
	private final String message;
	private final AnimatedImage icon;
	private final Color fontColor;
	private final String acceptTitle;
	private final String declineTitle;
	private final boolean isAlert;
	private JButton okButton;
	private JButton declineButton;
}
