package ClientGUI;

import Client.Client;
import Client.ClientWorker;
import Client.PairInfo;
import Services.DTO;
import Services.Header;
import Services.StringUtils;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Tran Long Tuan Vu
 */
public class ClientGUI extends MoveJFrame {
	private static final int LOGIN_PAGE = 0;
	private static final int CHAT_PAGE = 1;
	private static final int LIMIT_MESSAGE_LINE = 40;
	private static final int LIMIT_INPUT_LINE = 30;
	private static final int AUTO_LEFT_TIME = 20000; //millisecond

	private int scrollBarMaxValue;
	private int currentPage;
	private boolean isNeedTimeMaker;
	private boolean isChecking;
	private String checkResult;
	private boolean isFinding;

	private Thread timer;

	public ClientGUI() {
		setTitle("Chat App");
		initComponents();

		currentPage = LOGIN_PAGE;
		isNeedTimeMaker = false;

		//Event khi đóng window
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					//kiểm tra nếu có kết nối thì phải gửi "Break_Connect" và close socket trước khi tắt giao diện
					if (Client.checkConnection()) {
						Client.send(Header.BREAK_CONNECT_HEADER);
						dispose();
						System.exit(0);
					}
				} catch (Exception ignored) {}
			}
		});
	}

	/**
	 * Có đang là page Login không
	 */
	public boolean isLoginPage() {
		return currentPage == LOGIN_PAGE;
	}


	/**
	 * Có đang là page Chat không
	 */
	public boolean isChatPage() {
		return currentPage == CHAT_PAGE;
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

	private void initComponents() {
		loginPane = new JLayeredPane();
		lbAPPIcon = new JLabel();
		lbAPPName = new JLabel();
		inpuNametPanel = new RoundPanel(60);
		lbWelcome = new JLabel();
		lbTitles = new JLabel();
		txtName = new RoundJTextField(10);
		btnJoin = new JButton("JOIN");
		btnQuit = new JButton("QUIT");
		chatPane = new JLayeredPane();
		chatHeaderPanel = new JPanel();
		btnBack = new JButton();
		lbStatus = new JLabel();
		lbPairAvatar = new JLabel();
		lbPairName = new JLabel();
		lbPairName2 = new JLabel();
		btnInfo = new JButton();
		btnSend = new JButton();
		txtInput = new RoundJTextField(50);
		scrollPane1 = new JScrollPane();
		scrollPane2 = new JScrollPane();
		jChatBoxPanel = new JPanel();
		jChatPanel = new JPanel();
		inputArea = new JTextArea();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setIconImage(new ImageIcon("images/icon.png").getImage());
		Container contentPane = getContentPane();
		contentPane.setLayout(new CardLayout());

		//======== loginPane ========
		{
			loginPane.setBackground(Color.white);
			loginPane.setOpaque(true);
			loginPane.setFocusable(true);
			loginPane.requestFocus();

			//---- lbAPPIcon ----
			lbAPPIcon.setBackground(Color.white);
			lbAPPIcon.setIcon(new ImageIcon("images/icon.png"));
			loginPane.add(lbAPPIcon, JLayeredPane.DEFAULT_LAYER);
			lbAPPIcon.setBounds(105, 70, 134, 139);

			//---- lbAPPName ----
			lbAPPName.setBackground(Color.white);
			lbAPPName.setIcon(new ImageIcon("images/APP.png"));
			loginPane.add(lbAPPName, JLayeredPane.DEFAULT_LAYER);
			lbAPPName.setBounds(44, 180, 255, 148);

			//======== inputNamePanel ========
			{
				inpuNametPanel.setBackground(new Color(134, 238, 252));
				inpuNametPanel.setOpaque(false);
				inpuNametPanel.setBorder(new javax.swing.border.CompoundBorder(
								new javax.swing.border.TitledBorder(
										new javax.swing.border.EmptyBorder(0, 0 ,0 , 0),
										"JF\u006frm\u0044es\u0069gn\u0065r \u0045va\u006cua\u0074io\u006e",
										javax.swing.border.TitledBorder.CENTER,
										javax.swing.border.TitledBorder.BOTTOM,
										new java.awt.Font("D\u0069al\u006fg", java.awt.Font.BOLD ,12 ),
										java.awt.Color.red),
								inpuNametPanel.getBorder()));
				inpuNametPanel.addPropertyChangeListener( new java. beans .PropertyChangeListener ( ) {
					@Override
					public void propertyChange(java.beans.PropertyChangeEvent e) {
						if ("\u0062or\u0064er".equals(e.getPropertyName())) throw new RuntimeException();
					}
				});
				inpuNametPanel.setLayout(null);

				//---- lbWelcome ----
				lbWelcome.setText("   Let's Chat Together");
				lbWelcome.setFont(new Font("Segoe UI", Font.BOLD, 27));
				lbWelcome.setForeground(Color.white);
				inpuNametPanel.add(lbWelcome);
				lbWelcome.setBounds(new Rectangle(new Point(25, 40), lbWelcome.getPreferredSize()));

				//---- lbTitles ----
				lbTitles.setText("");
				lbTitles.setFont(new Font("Arial", Font.PLAIN, 14));
				lbTitles.setForeground(Color.white);
				lbTitles.setLabelFor(txtName);
				inpuNametPanel.add(lbTitles);
				lbTitles.setBounds(25, 86, 295, 25);

				//---- txtName ----
				txtName.setBorder(new EmptyBorder(5,15,5,5));
				txtName.setFont(new Font("Segoe UI Black", Font.PLAIN, 16));
				txtName.setHorizontalAlignment(SwingConstants.CENTER);
				txtName.setText("What's your name ?");
				txtName.addFocusListener(new FocusListener() {
					@Override
					public void focusGained(FocusEvent e) {
						if(txtName.getText().equalsIgnoreCase("What's your name ?")){
							txtName.setText("");
							txtName.setForeground(Color.BLACK);
						}
					}

					@Override
					public void focusLost(FocusEvent e) {
						if(txtName.getText().trim().equals("")){
							txtName.setText("What's your name ?");
							txtName.setForeground(new Color(182, 182, 182));
						}
					}
				});
				inpuNametPanel.add(txtName);
				txtName.setBounds(25, 110, 290, 40);

				//---- btnJoin ----
				btnJoin.setText("JOIN");
				btnJoin.setBorderPainted(false);
				btnJoin.setOpaque(false);
				btnJoin.setFocusPainted(false);
				btnJoin.setBackground(new Color(115, 170, 250));
				btnJoin.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
				btnJoin.setForeground(Color.white);
				btnJoin.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						btnJoinHandle();
					}
				});
				inpuNametPanel.add(btnJoin);
				btnJoin.setBounds(25, 170, 290, 39);

				//---- btnQuit ----
				btnQuit.setText("QUIT");
				btnQuit.setBorderPainted(false);
				btnQuit.setOpaque(false);
				btnQuit.setFocusPainted(false);
				btnQuit.setBackground(new Color(250, 115, 115));
				btnQuit.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
				btnQuit.setForeground(Color.white);
				btnQuit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						btnQuitHandle();
					}
				});
				inpuNametPanel.add(btnQuit);
				btnQuit.setBounds(25, 215, 290, 39);

				{
					// compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < inpuNametPanel.getComponentCount(); i++) {
						Rectangle bounds = inpuNametPanel.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = inpuNametPanel.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					inpuNametPanel.setMinimumSize(preferredSize);
					inpuNametPanel.setPreferredSize(preferredSize);
				}
			}
			loginPane.add(inpuNametPanel, JLayeredPane.DEFAULT_LAYER);
			inpuNametPanel.setBounds(0, 400, 335, 320);
		}
		contentPane.add(loginPane, "card1");

		//======== chatPane ========
		{
			//chatPane.setBackground(new Color(249, 253, 255));
			chatPane.setBackground(Color.white);
			chatPane.setOpaque(true);

			//======== chatHeaderPanel ========
			{
				chatHeaderPanel.setBackground(Color.white);
				chatHeaderPanel.setLayout(null);

				//---- btnBack ----
				btnBack.setBackground(Color.white);
				btnBack.setFocusPainted(false);
				btnBack.setBorderPainted(false);
				btnBack.setIcon(new ImageIcon("images/back.png"));
				btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btnBack.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						btnBackHandle();
					}
				});
				chatHeaderPanel.add(btnBack);
				btnBack.setBounds(10, 20, 27, 35);

				//---- lbStatus ----
				lbStatus.setIcon(new ImageIcon("images/checked.png"));
				chatHeaderPanel.add(lbStatus);
				lbStatus.setBounds(new Rectangle(new Point(75, 40), lbStatus.getPreferredSize()));

				//---- lbPairAvatar ----
				lbPairAvatar.setIcon(new ImageIcon("images/user.png"));
				chatHeaderPanel.add(lbPairAvatar);
				lbPairAvatar.setBounds(new Rectangle(new Point(54, 20), lbPairAvatar.getPreferredSize()));

				//---- lbPairName ----
				lbPairName.setText("PAIR NAME");
				lbPairName.setFont(new Font("Arial", Font.BOLD, 11));
				lbPairName.setForeground(new Color(1, 178, 254));
				chatHeaderPanel.add(lbPairName);
				lbPairName.setBounds(new Rectangle(new Point(100, 24), lbPairName.getPreferredSize()));

				//---- lbPairName2 ----
				lbPairName2.setText("Online");
				lbPairName2.setFont(new Font("Arial", Font.PLAIN, 11));
				lbPairName2.setBackground(new Color(204, 204, 204));
				lbPairName2.setForeground(Color.lightGray);
				chatHeaderPanel.add(lbPairName2);
				lbPairName2.setBounds(100, 38, 59, 13);

				//---- btnInfo ----
				btnInfo.setBorderPainted(false);
				btnInfo.setFocusPainted(false);
				btnInfo.setBackground(Color.white);
				btnInfo.setIcon(new ImageIcon("images/info.png"));
				btnInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btnInfo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						btnInfoHandle();
					}
				});
				chatHeaderPanel.add(btnInfo);
				btnInfo.setBounds(287, 21, 28, 29);

				{
					// compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < chatHeaderPanel.getComponentCount(); i++) {
						Rectangle bounds = chatHeaderPanel.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = chatHeaderPanel.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					chatHeaderPanel.setMinimumSize(preferredSize);
					chatHeaderPanel.setPreferredSize(preferredSize);
				}
			}
			chatPane.add(chatHeaderPanel, JLayeredPane.DEFAULT_LAYER);
			chatHeaderPanel.setBounds(0, 0, 340, 75);

			//---- btnSend ----
			btnSend.setBackground(new Color(249, 253, 255));
			btnSend.setBorderPainted(false);
			btnSend.setIcon(new ImageIcon("images/send-message.png"));
			btnSend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			btnSend.setFocusPainted(false);

			btnSend.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnSendHandle();
				}
			});
			chatPane.add(btnSend, JLayeredPane.DEFAULT_LAYER);
			btnSend.setBounds(290, 640, 40, 40);

			//---- txtInput ----
			txtInput.setBorder(new EmptyBorder(7,10,5,10));
			txtInput.setBackground(new Color(238,241,249));
			txtInput.setFont(new Font("Arial", Font.PLAIN, 14));
			chatPane.add(txtInput, JLayeredPane.DEFAULT_LAYER);
			txtInput.setBounds(20, 640, 265, 40);
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
						scrollPane1.setVisible(true);
						inputArea.requestFocus();
						revalidate();
					}
				}
			});

			txtInput.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					txtInput.setText(txtInput.getText() + "\n");
				}
			});

			//======== scrollPane1 ========
			{

				//---- inputArea ----
				inputArea.setBorder(new EmptyBorder(7,5,5,5));
				inputArea.setBackground(new Color(238,241,249));
				inputArea.setFont(new Font("Arial", Font.PLAIN, 14));
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
							scrollPane1.setVisible(false);
							txtInput.setText(inputArea.getText());
							txtInput.setVisible(true);
							txtInput.requestFocus();
							revalidate();
						}
					}
				});
				scrollPane1.setViewportView(inputArea);
			}
			chatPane.add(scrollPane1, JLayeredPane.DEFAULT_LAYER);
			scrollPane1.setBounds(20, 640, 265, 40);
			scrollPane1.setVisible(false);

			//======== scrollPane2 ========
			{
				//---- jChatBoxPanel ----
				jChatBoxPanel.setBackground(new Color(249, 253, 255));
				jChatBoxPanel.setOpaque(true);
				jChatBoxPanel.setLayout(new BorderLayout());

				//---- jChatBoxPanel ----
				jChatPanel.setBackground(new Color(249, 253, 255));
				jChatPanel.setOpaque(true);
				jChatPanel.setLayout(new BoxLayout(jChatPanel, BoxLayout.Y_AXIS));
				jChatPanel.add(Box.createRigidArea(new Dimension(0,10)));

				jChatBoxPanel.add(jChatPanel, BorderLayout.NORTH);
				scrollPane2.setViewportView(jChatBoxPanel);
				scrollPane2.getVerticalScrollBar().setUnitIncrement(16);
				scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				scrollBarMaxValue = scrollPane2.getVerticalScrollBar().getMaximum();
				scrollPane2.getVerticalScrollBar().addAdjustmentListener(
						e -> {
							if ((scrollBarMaxValue - e.getAdjustable().getMaximum()) == 0)
								return;
							e.getAdjustable().setValue(e.getAdjustable().getMaximum());
							scrollBarMaxValue = scrollPane2.getVerticalScrollBar().getMaximum();
						});
			}
			chatPane.add(scrollPane2, JLayeredPane.DEFAULT_LAYER);
			scrollPane2.setBounds(-2, 72, 340, 550);
		}
		//contentPane.add(chatPane, "card2");
		setSize(350, 735);
		setLocationRelativeTo(getOwner());
	}


	/**
	 * Xử lý khi bẩm nút "JOIN"
	 */
	private void btnJoinHandle() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					txtName.setEnabled(false);
					btnJoin.setEnabled(false);
					if (!Client.checkConnection()) {
						throw new IOException(Client.FAIL_CONNECT);
					}
					String name = txtName.getText();
					if (name.length() > ClientWorker.NAME_LIMIT
							|| name.isEmpty() || name.isBlank()) {
						Dialog.newAlertDialog(ClientGUI.this,"Invalid Name");
						lbTitles.setText("Must less than 10 characters !");
						return;
					}
					btnJoin.setFont(btnJoin.getFont().deriveFont(Font.BOLD, 24));
					btnQuit.setText("CANCEL");
					lbWelcome.setText("Checking");
					lbAPPIcon.setIcon(new ImageIcon("images/loading.gif"));
					DTO dto = new DTO(Header.NAME_CHECK_HEADER);
					dto.setData(name);
					ClientWorker.requestHandle(dto);
					int i = 1;
					int waitTime = 0;
					isChecking = true;
					while (isChecking || waitTime < 2000) {
						btnJoin.setText(("" + i++)
								.replace("1", "●")
								.replace("2", "●●")
								.replace("3", "●●●")
								.replace("4", "●●●●")
								.replace("5", "●●●●●"));
						if (i == 5)
							i = 1;
						//noinspection BusyWait
						Thread.sleep(200);
						waitTime += 200;
					}
					if (checkResult.equals("stop"))
						return;
					if (checkResult.equalsIgnoreCase("false")) {
						Dialog.newAlertDialog(ClientGUI.this, "Name Used");
						resetLoginPage();
					}
					else {
						ClientWorker.name = txtName.getText();
						lbTitles.setText("Hi " + ClientWorker.name + ", please wait :)");
						startFinding();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					Dialog.newAlertDialog(ClientGUI.this, "Got ERROR");
					if (btnQuit.getText().equals("CANCEL"))
						btnQuit.doClick();
				} catch (IOException | NullPointerException ex) {
					//exception throw khi ko thể kết nói tối server.
					try {
						System.out.println("Reconnecting...");
						lbWelcome.setText("Reconnecting");
						lbAPPIcon.setIcon(new ImageIcon("images/loading.gif"));
						//Thực hiện kết nối lại
						Client.close();
						Client.connectServer();
						System.out.println("Reconnected");
						lbWelcome.setText("Reconnected");
						lbAPPIcon.setIcon(new ImageIcon("images/icon.png"));
						txtName.setEnabled(true);
						btnJoin.setEnabled(true);
						if (!(txtName.getText().length() > ClientWorker.NAME_LIMIT)
								&& !txtName.getText().isEmpty()
								&& !txtName.getText().isBlank())
							btnJoin.doClick();
					} catch (IOException f) {
						//Throw exception nếu kết nối lại thất bại
						System.out.println("Reconnect Failed..");
						lbWelcome.setText("Reconnected Failed");
						lbAPPIcon.setIcon(new ImageIcon("images/icon.png"));
						Dialog.newAlertDialog(Client.Frame, Client.FAIL_CONNECT);
						setEnabled(true);
						txtName.setEnabled(true);
						btnJoin.setEnabled(true);
					} catch (UnknownError ignored) {
						lbWelcome.setText("U GOT BANNED !");
						lbAPPIcon.setIcon(new ImageIcon("images/icon.png"));
						setEnabled(true);
						txtName.setEnabled(true);
						btnJoin.setEnabled(true);
					}
				}
			}
		}).start();
	}

	/**
	 * Xử lý khi bẩm nút "QUIT/CANCEL"
	 */
	private void btnQuitHandle() {
		if (btnQuit.getText().equals("QUIT")) {
			dispose();
			System.exit(0);
		} else {
			try {
				DTO dto = new DTO(Header.STOP_FIND_HEADER);
				ClientWorker.requestHandle(dto);
				stopChecking("stop");
				stopFinding();
				Thread.sleep(500);
				resetLoginPage();
			} catch (IOException | InterruptedException ex) {
				Client.close();
				Client.Frame.showDisconnectAlert();
			}
		}
	}

	/**
	 * Xử lý khi bẩm nút "BACK" ở giao diện Chat
	 */
	private void btnBackHandle() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DTO dto = new DTO(Header.BREAK_PAIR_HEADER);
					ClientWorker.requestHandle(dto);
					changeToLogin();
					startFinding();
				} catch (IOException | InterruptedException ignored) {
					changeToLogin();
					Dialog.newAlertDialog(Client.Frame, "Disconnected");
					Client.Frame.resetLoginPage();
				}
			}
		}).start();
	}

	/**
	 * Xử lý khi bẩm nút "User Info" ở giao diện Chat
	 */
	private void btnInfoHandle() {
		JOptionPane.showMessageDialog(
				ClientGUI.this,
				ClientWorker.pair.toString(),
				"Friend Info",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Xử lý khi bẩm nút "SEND" ở giao diện Chat
	 */
	private void btnSendHandle() {
		try {
			String input;
			if (scrollPane1.isVisible()) {
				input = inputArea.getText();
				scrollPane1.setVisible(false);
				txtInput.setText("");
				txtInput.setVisible(true);
				txtInput.requestFocus();
			} else input = txtInput.getText();
			if (input.isEmpty() || input.isBlank())
				return;
			txtInput.setText("");
			inputArea.setText("");
			revalidate();
			if (ClientWorker.pair.getStatus().equalsIgnoreCase("Online")) {
				DTO dto = new DTO(Header.MESSAGE_HEADER);
				dto.setData(input);
				ClientWorker.requestHandle(dto);
				appendSend(input, LocalDateTime.now().toString());
			} else appendSendFail(input, LocalDateTime.now().toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Khởi động tìm kiếm bạn chat
	 */
	public void startFinding() throws IOException, NullPointerException, InterruptedException {
		lbWelcome.setText("Finding");
		DTO dto = new DTO(Header.FIND_CHAT_HEADER);
		dto.setData(txtName.getText());
		ClientWorker.requestHandle(dto);
		int i = 1;
		int waitTime = 0;
		isFinding = true;
		while (isFinding || waitTime < 2000) {
			btnJoin.setText(("" + i++)
					.replace("1", "●")
					.replace("2", "●●")
					.replace("3", "●●●")
					.replace("4", "●●●●")
					.replace("5", "●●●●●"));
			if (i == 5)
				i = 1;
			//noinspection BusyWait
			Thread.sleep(200);
			waitTime += 200;
		}
		changeToChat();
	}

	//Reset giao diện Login
	public void resetLoginPage() {
		stopChecking("stop");
		stopFinding();
		lbAPPIcon.setIcon(new ImageIcon("images/icon.png"));
		txtName.setEnabled(true);
		btnJoin.setEnabled(true);
		btnJoin.setIcon(null);
		btnJoin.setText("JOIN");
		btnJoin.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		btnQuit.setText("QUIT");
		lbWelcome.setText("Welcome");
		lbTitles.setText("");
	}

	/**
	 * Thêm dòng chat phía bạn chat
	 */
	public void appendReceive(String message, String createdDate) {
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
	public void appendSend(String message, String createdDate) {
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
	public void appendSendFail(String message, String createdDate) {
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
	public void appendAlert(String message, boolean isError) {
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
	public void appendTimeMaker(String time) {
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
	private void appendMessageToBox(String message, String createdDate, Color bg, Font font, Color fontColor, int fontAlign, int align) {
		if (currentPage != CHAT_PAGE)
			return;
		message = StringUtils.applyWrapForGUI(message);

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
		lbChat.addActionListener(e -> lbTime.setVisible(!lbTime.isVisible()));

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
		if (isNeedTimeMaker && !createdDate.equals("")) {
			appendTimeMaker(createdDate);
			isNeedTimeMaker = false;
		}
		jChatPanel.add(panel);
		jChatPanel.add(Box.createRigidArea(new Dimension(5,0)));
		jChatPanel.revalidate();
	}

	public void leftRoomTimer() {
		timer = new Thread(new Runnable() {
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

	public void showDisconnectAlert() {
		if (isLoginPage()) {
			Dialog.newAlertDialog(Client.Frame, "Disconnected");
			resetLoginPage();
		}
		else appendAlert("Disconnected !",true);
	}

	/**
	 * Set thông tin bạn chat
	 */
	public void setInfo(PairInfo info) {
		try {
			lbPairName.setText(info.getName());
			lbPairName2.setText(info.getStatus());
			if (lbPairName2.getText().equalsIgnoreCase("offline"))
				lbStatus.setIcon(new ImageIcon("images/remove.png"));
			else lbStatus.setIcon(new ImageIcon("images/checked.png"));
		} catch (NullPointerException ignored) {}
	}

	/**
	 * Chuyển qua giao diện Chat
	 */
	public void changeToChat() {
		if (isChatPage() || ClientWorker.pair == null)
			return;
		setInfo(ClientWorker.pair);
		currentPage = CHAT_PAGE;
		isNeedTimeMaker = true;
		getContentPane().removeAll();
		getContentPane().add(chatPane, "card2");
		appendAlert(ClientWorker.pair.getName() + " joined the chat", false);
		getContentPane().revalidate();
		getContentPane().repaint();
	}

	/**
	 * Chuyển qua giao diện Login
	 */
	public void changeToLogin() {
		if (isLoginPage())
			return;
		currentPage = LOGIN_PAGE;
		if (timer != null) {
			timer.interrupt();
			timer = null;
		}
		ClientWorker.pair = null;
		jChatPanel.removeAll();
		jChatPanel.revalidate();
		getContentPane().removeAll();
		getContentPane().add(loginPane, "card1");
		getContentPane().revalidate();
		getContentPane().repaint();
	}

	private JLayeredPane loginPane;
	private JLabel lbAPPIcon;
	private JLabel lbAPPName;
	private JPanel inpuNametPanel;
	private JLabel lbWelcome;
	private JLabel lbTitles;
	private JTextField txtName;
	private JButton btnJoin;
	private JButton btnQuit;
	private JLayeredPane chatPane;
	private JPanel chatHeaderPanel;
	private JButton btnBack;
	private JLabel lbStatus;
	private JLabel lbPairAvatar;
	private JLabel lbPairName;
	private JLabel lbPairName2;
	private JButton btnInfo;
	private JButton btnSend;
	private JTextField txtInput;
	private JPanel jChatBoxPanel;
	private JPanel jChatPanel;
	private JScrollPane scrollPane1;
	private JScrollPane scrollPane2;
	private JTextArea inputArea;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
