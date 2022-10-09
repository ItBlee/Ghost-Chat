package GUI;

import GUI.panel.ChatPanel;
import GUI.panel.HomePanel;
import GUI.component.MoveJFrame;

import javax.swing.*;
import java.awt.*;

import static constant.ClientConstant.*;

public class ClientGUI extends MoveJFrame {

	private int currentPage;
	private boolean isChecking;
	private String checkResult;
	private boolean isFinding;

	private Thread timer;

	public ClientGUI() {
		setTitle("Chat App");
		initComponents();

		currentPage = LOGIN_PAGE;

		//Event khi đóng window
		/*addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					//kiểm tra nếu có kết nối thì phải gửi "Break_Connect" và close socket trước khi tắt giao diện
					if (General.launcher.checkConnection()) {
						Client.send(Header.BREAK_CONNECT););
						dispose();
						System.exit(0);
					}
				} catch (Exception ignored) {}
			}
		});*/
	}

	private void initComponents() {
		homePane = new HomePanel();
		chatPane = new ChatPanel();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setIconImage(new ImageIcon("images/icon.png").getImage());
		Container contentPane = getContentPane();
		contentPane.setLayout(new CardLayout());
		contentPane.add(homePane, "home");
		//contentPane.add(chatPane, "chat");
		setSize(350, 735);
		setLocationRelativeTo(getOwner());
	}

	/**
	 * Có đang là page Login không
	 */
	public boolean isHomePage() {
		return currentPage == LOGIN_PAGE;
	}

	/**
	 * Có đang là page Chat không
	 */
	public boolean isChatPage() {
		return currentPage == CHAT_PAGE;
	}

	public boolean isChecking() {
		return isChecking;
	}

	public void startChecking() {
		isChecking = true;
	}

	/**
	 * Có đang tìm kiếm bạn chat không
	 */
	public boolean isFinding() {
		return isFinding;
	}

	/**
	 * Dừng kiểm tra name
	 */
	public void stopChecking(String result) {
		if (isChecking)
			isChecking = false;
		this.checkResult = result;
	}

	/**
	 * Dừng tìm bạn chat
	 */
	public void stopFinding() {
		if (isFinding)
			isFinding = false;
	}

	/**
	 * Chuyển qua giao diện Chat
	 */
	public void changeToChat() {
		if (isChatPage() || ClientWorker.pair == null)
			return;
		setInfo(ClientWorker.pair);
		currentPage = CHAT_PAGE;
		getContentPane().removeAll();
		getContentPane().add(chatPane, "chat");
		appendAlert(ClientWorker.pair.getName() + " joined the chat", false);
		getContentPane().revalidate();
		getContentPane().repaint();
	}

	/**
	 * Chuyển qua giao diện Login
	 */
	public void changeToLogin() {
		if (isHomePage())
			return;
		currentPage = LOGIN_PAGE;
		if (timer != null) {
			timer.interrupt();
			timer = null;
		}
		ClientWorker.pair = null;
		chatPane.removeAll();
		chatPane.revalidate();
		getContentPane().removeAll();
		getContentPane().add(homePane, "home");
		getContentPane().revalidate();
		getContentPane().repaint();
	}

	public HomePanel getHomePane() {
		return homePane;
	}

	public ChatPanel getChatPane() {
		return chatPane;
	}

	private HomePanel homePane;
	private ChatPanel chatPane;
}
