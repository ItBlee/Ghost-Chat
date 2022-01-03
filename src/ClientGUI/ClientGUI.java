package ClientGUI;

import Services.StringUtils;
import com.formdev.flatlaf.FlatIntelliJLaf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Tran Long Tuan Vu
 */
public class ClientGUI extends MoveJFrame {
	private int verticalScrollBarMaximumValue;
	private static final int LIMIT_MESSAGE_LINE = 40;
	private static final int LIMIT_INPUT_LINE = 30;

	private static Timer alphaChanger;

	public ClientGUI() {
		setTitle("Chat App");
		initComponents();
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
				lbWelcome.setText("Welcome");
				lbWelcome.setFont(new Font("Segoe UI", Font.BOLD, 27));
				lbWelcome.setForeground(Color.white);
				inpuNametPanel.add(lbWelcome);
				lbWelcome.setBounds(new Rectangle(new Point(25, 40), lbWelcome.getPreferredSize()));

				//---- lbTitles ----
				lbTitles.setText("What's your name ?");
				lbTitles.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
				lbTitles.setForeground(Color.white);
				lbTitles.setLabelFor(txtName);
				inpuNametPanel.add(lbTitles);
				lbTitles.setBounds(25, 86, 295, 25);

				//---- txtName ----
				txtName.setBorder(new EmptyBorder(5,5,5,5));
				txtName.setFont(new Font("Segoe UI Black", Font.PLAIN, 16));
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
				btnInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
					appendSend("Hải nèHải nèHảiHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nèHải nè");
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
				verticalScrollBarMaximumValue = scrollPane2.getVerticalScrollBar().getMaximum();
				scrollPane2.getVerticalScrollBar().addAdjustmentListener(
						e -> {
							if ((verticalScrollBarMaximumValue - e.getAdjustable().getMaximum()) == 0)
								return;
							e.getAdjustable().setValue(e.getAdjustable().getMaximum());
							verticalScrollBarMaximumValue = scrollPane2.getVerticalScrollBar().getMaximum();
						});
			}
			chatPane.add(scrollPane2, JLayeredPane.DEFAULT_LAYER);
			scrollPane2.setBounds(-2, 72, 340, 550);
		}
		//contentPane.add(chatPane, "card2");
		setSize(350, 735);
		setLocationRelativeTo(getOwner());
	}

	public void appendReceive(String message) {
		Color bg = new Color(238,241,249);
		Font font = new Font("Arial", Font.PLAIN, 16);
		Color fontColor = new Color(0, 0, 0);
		int fontAlign = SwingConstants.LEFT;
		int align = FlowLayout.LEFT;
		appendMessageToBox(message, bg, font, fontColor, fontAlign, align);
	}

	public void appendSend(String message) {
		Color bg = new Color(1, 178, 254);
		Font font = new Font("Arial", Font.PLAIN, 16);
		Color fontColor = Color.white;
		int fontAlign = SwingConstants.RIGHT;
		int align = FlowLayout.RIGHT;
		appendMessageToBox(message, bg, font, fontColor, fontAlign, align);
	}

	public void appendSendFail(String message) {
		Color bg = new Color(252, 136, 136);
		Font font = new Font("Arial", Font.PLAIN, 16);
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		Font newFont = new Font(attributes);
		Color fontColor = Color.lightGray;
		int fontAlign = SwingConstants.RIGHT;
		int align = FlowLayout.RIGHT;
		appendMessageToBox(message, bg, newFont, fontColor, fontAlign, align);
	}

	public void appendAlert(String message, boolean isError) {
		Color bg;
		if (isError)
			bg = new Color(250, 115, 115);
		else bg = new Color(1, 254, 149);
		Font font = new Font("Arial", Font.PLAIN, 16);
		Color fontColor = Color.white;
		int fontAlign = SwingConstants.CENTER;
		int align = FlowLayout.CENTER;
		appendMessageToBox(message, bg, font, fontColor, fontAlign, align);
	}

	@SuppressWarnings("ConstantConditions")
	public void appendTimeLine(String time) {
		Color bg = null;
		Font font = new Font("Arial", Font.PLAIN, 12);
		Color fontColor = Color.BLACK;
		int fontAlign = SwingConstants.CENTER;
		int align = FlowLayout.CENTER;
		appendMessageToBox(time, bg, font, fontColor, fontAlign, align);
	}

	private void appendMessageToBox(String message, Color bg, Font font, Color fontColor, int fontAlign, int align) {
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

		JLabel time = new JLabel("10:34");
		time.setVisible(false);
		lbChat.addActionListener(e -> time.setVisible(!time.isVisible()));

		if (align == FlowLayout.RIGHT)
			panel.add(time);
		panel.add(lbChat);
		if (align == FlowLayout.LEFT)
			panel.add(time);
		jChatPanel.add(panel);
		jChatPanel.add(Box.createRigidArea(new Dimension(5,0)));
		jChatPanel.revalidate();
	}

	public static void main(String[] args) {
		FlatIntelliJLaf.setup();
		UIManager.put( "Button.arc", 999 );
		UIManager.put( "ScrollBar.trackArc", 999 );
		UIManager.put( "ScrollBar.thumbArc", 999 );
		UIManager.put( "ScrollBar.trackInsets", new Insets( 2, 4, 2, 4 ) );
		UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
		UIManager.put( "ScrollBar.track", new Color( 0xe0e0e0 ) );

		ClientGUI frame = new ClientGUI();
		frame.setVisible(true);

		Dialog dialog = Dialog.newAlertDialog(frame, "Server Busy !");
		dialog.setVisible(true);
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
