package com.itblee.gui;

import com.itblee.core.helper.ClientHelper;
import com.itblee.gui.component.MessageBox;
import com.itblee.gui.component.MoveJFrame;
import com.itblee.gui.component.AbstractPane;
import com.itblee.gui.page.*;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.utils.DateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;
import static com.itblee.constant.Resource.*;

public class ClientFrame extends MoveJFrame {

	private CardLayout card;
	private Map<Page, AbstractPane> pages;

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
	}

	private void initComponents() {
		pages = new HashMap<>();
		pages.put(Page.LOADING, new LoadingPage(this));
		pages.put(Page.DISCONNECT, new ErrorPage(this));
		pages.put(Page.LOGIN, new LoginPage(this));
		pages.put(Page.HOME, new HomePage(this));
		pages.put(Page.CHAT, new ChatPage(this));

		current = Page.LOADING;

		card = new CardLayout();
		Container contentPane = getContentPane();
		contentPane.setLayout(card);
		pages.forEach((page, jComponent) -> contentPane.add(jComponent, page.name()));

		//======== this ========
		setTitle("GHOST CHAT");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setIconImage(new ImageIcon(RESOURCE_PATH + "images/icon.png").getImage());
		setSize(350, 735);
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

	public void appendReceive(Message message) {
		Date date = DateUtil.stringToDate(message.getSentDate());
		MessageBox receiveMsg = ChatUtil.renderReceiveMsg(message.getBody(), date);
		getChatPage().appendMessage(receiveMsg);
	}

	public void appendHistory(List<Message> messages) {
		ChatPage chatPage = getChatPage();
		MessageBox messageBox;
		String username = ClientHelper.getUser().getUsername();
		for (Message message : messages) {
			Date date = DateUtil.stringToDate(message.getSentDate());
			if (message.getSender().equals(username))
				messageBox = ChatUtil.renderSendMsg(message.getBody(), date);
			else messageBox = ChatUtil.renderReceiveMsg(message.getBody(), date);
			chatPage.appendMessage(messageBox);
		}
	}

	public void setFriendInfo(FriendInfo info) {
		getChatPage().updateInfo(info);
	}

	private ChatPage getChatPage() {
		ChatPage chatPage = (ChatPage) pages.get(Page.CHAT);
		if (!chatPage.isLoaded())
			chatPage.load();
		return chatPage;
	}

	@Override
	public void dispose() {
		try {
			ClientHelper.closeConnection();
		} catch (Exception ignored) {}
		super.dispose();
	}

	private void setPage(Page page) {
		Page old = current;
		current = page;
		AbstractPane oldPage = pages.get(old);
		Color titleBarColor;
		try {
			String username = ClientHelper.getUser().getUsername();
			if (username != null)
				setTitle("GHOST CHAT - " + username);
			else setTitle("GHOST CHAT");
		} catch (Exception ignored) {}
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
		oldPage.doOutro();
		AbstractPane newPage = pages.get(current);
		newPage.from(old);
		if (!newPage.isLoaded())
			newPage.load();
		card.show(getContentPane(), current.name());
		newPage.doIntro();
		getRootPane().putClientProperty("JRootPane.titleBarBackground", titleBarColor);
		oldPage.reset();
	}

	private void loadPage(Page page) {
		pages.get(page).load();
	}

	public AbstractPane getCurrent() {
		return pages.get(current);
	}

	public AbstractPane getPage(Page page) {
		return pages.get(page);
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
		if (!ClientHelper.authenticated()) {
			showLogin();
			return;
		}
		setPage(Page.HOME);
	}

	public void showChat(FriendInfo info) {
		if (!ClientHelper.authenticated()) {
			showLogin();
			return;
		}
		setPage(Page.CHAT);
		setFriendInfo(info);
	}

	public void loadLoading() {
		loadPage(Page.LOADING);
	}

	public void loadDisconnect() {
		loadPage(Page.DISCONNECT);
	}

	public void loadLogin() {
		loadPage(Page.LOGIN);
	}

	public void loadHome() {
		loadPage(Page.HOME);
	}

	public void loadChat() {
		loadPage(Page.CHAT);
	}

}
