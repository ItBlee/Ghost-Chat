package ClientGUI;

import Client.Client;
import Client.ClientWorker;
import Services.DTO;
import Services.Header;
import Services.StringUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Tran Long Tuan Vu
 */
public class Dialog extends JDialog {
	private static final int AUTO_SKIP_TIME = 10000; //millisecond
	private final Window owner;
	private final ImageIcon icon;
	private final Color fontColor;
	private final String content;
	private final String btnOkText;
	private final DTO innerDTO;
	private final String btnCancelText;
	private final boolean isConfirm;

	private Thread timer;

	private Dialog(Window owner, String content, String btnOkText, DTO innerDTO, String btnCancelText, ImageIcon icon, Color fontColor, boolean isConfirm) {
		super(owner);
		this.owner = owner;
		owner.setEnabled(false);

		this.innerDTO = innerDTO;
		this.content = content;
		this.btnOkText = btnOkText;
		this.btnCancelText = btnCancelText;
		this.icon = icon;
		this.fontColor = fontColor;
		this.isConfirm = isConfirm;

		initComponents();
		setVisible(true);
		setSkipTimer();
	}

	private void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		panel1 = new JPanel();
		lbIcon = new JLabel();
		lbContent = new JLabel();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setResizable(false);
		setUndecorated(true);
		setBackground(Color.white);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(null);
			dialogPane.setBackground(getBackground());
			dialogPane.setLayout(null);

			//======== contentPanel ========
			{
				contentPanel.setBackground(dialogPane.getBackground());
				contentPanel.setLayout(new FlowLayout());

				//======== panel1 ========
				{
					panel1.setBackground(dialogPane.getBackground());
					panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));

					//---- lbIcon ----
					panel1.add(Box.createRigidArea(new Dimension(10,20)));
					lbIcon.setIcon(icon);
					panel1.add(lbIcon);


					//---- lbContent ----
					//panel1.add(Box.createRigidArea(new Dimension(0,5)));
					lbContent.setText(content);
					lbContent.setFont(new Font("Arial", Font.BOLD, 22));
					lbContent.setBackground(Color.white);
					lbContent.setForeground(fontColor);
					lbContent.setHorizontalAlignment(SwingConstants.CENTER);
					panel1.add(lbContent);
				}
				contentPanel.add(panel1);
			}
			dialogPane.add(contentPanel);
			contentPanel.setBounds(1, 1, 296, contentPanel.getPreferredSize().height);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setBackground(dialogPane.getBackground());
				buttonBar.setLayout(new FlowLayout());

				//---- okButton ----
				okButton.setText(btnOkText);
				okButton.setBackground(new Color(1, 254, 149));
				okButton.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
				okButton.setForeground(Color.white);
				okButton.setFocusPainted(false);
				okButton.setBorderPainted(false);
				okButton.setMargin(new Insets(10, 15, 10, 15));
				okButton.setOpaque(false);
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						btnOKHandle();
					}
				});
				if (isConfirm)
					buttonBar.add(okButton);

				//---- cancelButton ----
				cancelButton.setText(btnCancelText);
				cancelButton.setBackground(new Color(250, 115, 115));
				cancelButton.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
				cancelButton.setForeground(Color.white);
				cancelButton.setFocusPainted(false);
				cancelButton.setBorderPainted(false);
				cancelButton.setMargin(new Insets(10, 20, 10, 20));
				cancelButton.setOpaque(false);
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						btnCancelHandle();
					}
				});
				buttonBar.add(cancelButton);
			}
			contentPane.add(buttonBar);
			buttonBar.setBounds(2, 260, 288, buttonBar.getPreferredSize().height);

			{
				// compute preferred size
				Dimension preferredSize = new Dimension();
				for(int i = 0; i < dialogPane.getComponentCount(); i++) {
					Rectangle bounds = dialogPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				Insets insets = dialogPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				dialogPane.setMinimumSize(preferredSize);
				dialogPane.setPreferredSize(preferredSize);
			}
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setSize(300, 350);
		setLocationRelativeTo(getOwner());
		setLocation(getX(),getY()-140);
	}

	private void btnOKHandle() {
		try {
			owner.setEnabled(true);
			dispose();
			DTO dto = new DTO(innerDTO.getHeader());
			dto.setSender(innerDTO.getSender());
			dto.setData("true");
			ClientWorker.requestHandle(dto);
			timer.interrupt();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void btnCancelHandle() {
		owner.setEnabled(true);
		dispose();
		if (!innerDTO.getHeader().isEmpty()) {
			DTO dto = new DTO(innerDTO.getHeader());
			dto.setSender(innerDTO.getSender());
			dto.setData("false");
			try {
				ClientWorker.requestHandle(dto);
				timer.interrupt();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void setSkipTimer() {
		timer = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int time = AUTO_SKIP_TIME;
					String text = cancelButton.getText();
					while (time > 0) {
						if (Client.Frame.isChatPage())
							break;
						Thread.sleep(AUTO_SKIP_TIME/10);
						time -= (AUTO_SKIP_TIME/10);
						cancelButton.setText(text + "(" + (time/1000) + ")");
					}
					cancelButton.doClick();
				} catch (InterruptedException ignored) {}
			}
		});
		timer.start();
	}

	@SuppressWarnings("ConstantConditions")
	public static void newInviteDialog(Window parent, String pairName) {
		DTO dto = new DTO(Header.INVITE_CHAT_HEADER);
		ImageIcon icon = new ImageIcon("images/found.png");
		Color fontColor = new Color(115,170,250);
		String content = StringUtils.applyWrapForGUI("Invite Friend:\n" + pairName + " ?");
		String btnOkText = "INVITE";
		String btnCancelText = "SKIP";
		boolean isConfirm = true;
		new Dialog(parent, content, btnOkText, dto, btnCancelText, icon, fontColor, isConfirm);
	}

	@SuppressWarnings("ConstantConditions")
	public static void newInviteFailedDialog(Window parent) {
		ImageIcon icon = new ImageIcon("images/fail.png");
		Color fontColor = new Color(115,170,250);
		String content = StringUtils.applyWrapForGUI("Friend Busy..");
		String btnOkText = null;
		String btnCancelText = "GOT IT";
		boolean isConfirm = false;
		new Dialog(parent, content, btnOkText, new DTO(""), btnCancelText, icon, fontColor, isConfirm);
	}

	@SuppressWarnings("ConstantConditions")
	public static void newConfirmDialog(Window parent, DTO dto) {
		ImageIcon icon = new ImageIcon("images/accept.png");
		Color fontColor = new Color(115,170,250);
		String content = StringUtils.applyWrapForGUI("Accept invite:\n" + dto.getData() + " ?");
		String btnOkText = "ACCEPT";
		String btnCancelText = "NO";
		boolean isConfirm = true;
		new Dialog(parent, content, btnOkText, dto, btnCancelText, icon, fontColor, isConfirm);
	}

	@SuppressWarnings("ConstantConditions")
	public static void newAlertDialog(Window parent, String error) {
		ImageIcon icon = new ImageIcon("images/error.png");
		Color fontColor = new Color(250, 115, 115);
		String content = StringUtils.applyWrapForGUI(error);
		String btnOkText = null;
		String btnCancelText = "GOT IT";
		boolean isConfirm = false;
		new Dialog(parent, content, btnOkText, new DTO(""), btnCancelText, icon, fontColor, isConfirm);
	}

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel panel1;
	private JLabel lbIcon;
	private JLabel lbContent;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
