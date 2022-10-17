package GUI.component;

import tranfer.Packet;
import tranfer.DataKey;
import core.Launcher;
import utils.JsonParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Dialog extends JDialog {
	public static final int AUTO_SKIP_TIME = 10000; //millisecond
	private final Window owner;
	private final String message;
	private ImageIcon icon;
	private Color fontColor;
	private String btnOkText;
	private Packet innerDTO;
	private String btnCancelText;
	private final boolean isConfirm;

	private Thread timer;

	public Dialog(Window owner, String message, boolean isConfirm) {
		super(owner);
		this.owner = owner;
		owner.setEnabled(false);
		this.message = message;
		this.isConfirm = isConfirm;
		initComponents();
	}

	private void initComponents() {
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JPanel panel1 = new JPanel();
		JLabel lbIcon = new JLabel();
		JLabel lbContent = new JLabel();
		JPanel buttonBar = new JPanel();
		JButton okButton = new JButton();
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
					lbContent.setText(message);
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
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setSize(300, 350);
		setLocationRelativeTo(getOwner());
		setLocation(getX(),getY()-140);
	}

	public void display() {
		setVisible(true);
		setSkipTimer();
	}

	private void btnOKHandle() {
		try {
			owner.setEnabled(true);
			dispose();
			Packet packet = new Packet();
			packet.setHeader(innerDTO.getHeader());
			packet.setSender(innerDTO.getSender());
			packet.getData().put(DataKey.USER_CHOICE, DataKey.ACCEPT);
			String json = JsonParser.toJson(packet);
			Launcher.getInstance().getWorker().send(json);
			timer.interrupt();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void btnCancelHandle() {
		owner.setEnabled(true);
		dispose();
		if (!innerDTO.getHeader().isEmpty()) {
			try {
				Packet packet = new Packet();
				packet.setHeader(innerDTO.getHeader());
				packet.setSender(innerDTO.getSender());
				packet.getData().put(DataKey.USER_CHOICE, DataKey.DECLINE);
				String json = JsonParser.toJson(packet);
				Launcher.getInstance().getWorker().send(json);
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
						if (Launcher.getInstance().getFrame().isChatPage())
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

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public Color getFontColor() {
		return fontColor;
	}

	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}

	public String getBtnOkText() {
		return btnOkText;
	}

	public void setBtnOkText(String btnOkText) {
		this.btnOkText = btnOkText;
	}

	public Packet getInnerDTO() {
		return innerDTO;
	}

	public void setInnerDTO(Packet innerDTO) {
		this.innerDTO = innerDTO;
	}

	public String getBtnCancelText() {
		return btnCancelText;
	}

	public void setBtnCancelText(String btnCancelText) {
		this.btnCancelText = btnCancelText;
	}

	private JButton cancelButton;
}
