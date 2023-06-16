package com.itblee.gui;

import com.itblee.constant.Resource;
import com.itblee.core.ClientHelper;
import com.itblee.core.TransferHelper;
import com.itblee.gui.component.MoveJFrame;
import com.itblee.gui.component.TransitionPane;
import com.itblee.gui.page.*;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.utils.ObjectUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.itblee.constant.Resource.*;

public class ClientFrame extends MoveJFrame {

	private CardLayout card;
	private Map<Page, TransitionPane> pages;

	private Page current;

	public enum Page {
		HOME,
		CHAT,
		LOADING,
		LOGIN,
		DISCONNECT
	}

	public ClientFrame() {
		initComponents();
		setPage(Page.CHAT);
	}

	private void initComponents() {
		pages = new LinkedHashMap<>();
		pages.put(Page.LOADING, new LoadingPage());
		pages.put(Page.DISCONNECT, new ErrorPage());
		pages.put(Page.LOGIN, new LoginPage());
		pages.put(Page.HOME, new HomePage());
		pages.put(Page.CHAT, new ChatPage());

		current = Page.LOADING;

		card = new CardLayout();
		Container contentPane = getContentPane();
		contentPane.setLayout(card);
		pages.forEach((page, jComponent) -> contentPane.add(jComponent, page.name()));

		//======== this ========
		setTitle("GHOST CHAT");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setIconImage(Resource.IMAGE_ICON.getImage());
		setSize(365, 735);
		setLocationRelativeTo(getOwner());
		JRootPane rootPane = getRootPane();
		rootPane.putClientProperty("JRootPane.titleBarForeground", Color.WHITE);
		rootPane.setGlassPane(new JComponent() {
			public void paintComponent(Graphics g) {
				g.setColor(new Color(0, 0, 0, 100));
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		});
		rootPane.getGlassPane().setVisible(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}

	public void showLoading() {
		setPage(Page.LOADING);
	}

	public void showDisconnect() {
		setPage(Page.DISCONNECT);
	}

	public void showLogin() {
		setPage(Page.LOGIN);
	}

	public void showHome() {
		setPage(Page.HOME);
	}

	public void appendReceive(Message message) {
		if (current != Page.CHAT)
			return;
		JPanel receivePanel = ChatUtil.renderReceiveMsg(message.getBody(), message.getSentDate());
		((ChatPage) getCurrent()).appendMessage(receivePanel);
	}

	public void setFriendInfo(FriendInfo info) {
		if (current != Page.CHAT)
			return;
		((ChatPage) getCurrent()).updateInfo(info);
	}

	public void goTo(Page page, Object... params) {
		switch (page) {
			case HOME:
				goHomePage();
				break;
			case CHAT:
				goChatPage((FriendInfo) params[0]);
		}
	}

	private void goHomePage() {
		if (current == Page.HOME)
			return;
		setPage(Page.HOME);
		((HomePage) getCurrent()).toNormalState();
	}

	private void goChatPage(FriendInfo info) {
		if (current == Page.CHAT)
			return;
		setPage(Page.CHAT);
		ObjectUtil.requireNonNull(info);
		((ChatPage) getCurrent()).reset();
		((ChatPage) getCurrent()).updateInfo(info);
	}

	@Override
	public void dispose() {
		if (ClientHelper.isConnected()) {
			try {
				TransferHelper.closeConnect();
				ClientHelper.close();
			} catch (IOException ignored) {}
		}
		super.dispose();
	}

	private void setPage(Page page) {
		pages.get(current).doOutro();
		current = page;
		Color titleBarColor;
		switch (page) {
			case CHAT:
			case LOADING:
			case LOGIN:
				titleBarColor = COLOR_DARK_BLUE;
				break;
			case DISCONNECT:
				titleBarColor = COLOR_DARK_SEMI_BLUE;
				break;
			default:
				titleBarColor = COLOR_SEMI_BLACK;
		}
		getRootPane().putClientProperty("JRootPane.titleBarBackground", titleBarColor);
		card.show(getContentPane(), current.name());
		pages.get(current).doIntro();
	}

	private JComponent getCurrent() {
		return pages.get(current);
	}

}
