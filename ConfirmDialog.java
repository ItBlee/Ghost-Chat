import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Mon Jan 03 11:15:24 ICT 2022
 */



/**
 * @author Tr?n Long Tu?n V?
 */
public class ConfirmDialog extends JDialog {
	public ConfirmDialog(Window owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Tr?n Long Tu?n V?
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		panel1 = new JPanel();
		label1 = new JLabel();
		label2 = new JLabel();
		label3 = new JLabel();
		label4 = new JLabel();
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
			dialogPane.setBackground(Color.white);
			dialogPane.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new javax
			. swing. border .EmptyBorder ( 0, 0 ,0 , 0) ,  "JF\u006frmDesi\u0067ner Ev\u0061luatio\u006e" , javax. swing
			.border . TitledBorder. CENTER ,javax . swing. border .TitledBorder . BOTTOM, new java. awt .
			Font ( "Dialo\u0067", java .awt . Font. BOLD ,12 ) ,java . awt. Color .red
			) ,dialogPane. getBorder () ) ); dialogPane. addPropertyChangeListener( new java. beans .PropertyChangeListener ( ){ @Override
			public void propertyChange (java . beans. PropertyChangeEvent e) { if( "borde\u0072" .equals ( e. getPropertyName (
			) ) )throw new RuntimeException( ) ;} } );
			dialogPane.setLayout(null);

			//======== contentPanel ========
			{
				contentPanel.setBackground(Color.white);
				contentPanel.setLayout(new FlowLayout());

				//======== panel1 ========
				{
					panel1.setBackground(Color.white);
					panel1.setFont(new Font("Arial", Font.PLAIN, 11));
					panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));

					//---- label1 ----
					label1.setIcon(new ImageIcon(getClass().getResource("/found.png")));
					panel1.add(label1);

					//---- label2 ----
					label2.setText("Invite Your Fiends");
					label2.setFont(new Font("Arial", Font.BOLD, 18));
					label2.setBackground(Color.white);
					panel1.add(label2);

					//---- label3 ----
					label3.setText("Do you want to invite {pairName} ?");
					label3.setFont(new Font("Arial", Font.PLAIN, 11));
					panel1.add(label3);

					//---- label4 ----
					label4.setText("text");
					panel1.add(label4);
				}
				contentPanel.add(panel1);
			}
			dialogPane.add(contentPanel);
			contentPanel.setBounds(1, 1, 296, contentPanel.getPreferredSize().height);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setBackground(Color.white);
				buttonBar.setLayout(new FlowLayout());

				//---- okButton ----
				okButton.setText("Invite");
				buttonBar.add(okButton);

				//---- cancelButton ----
				cancelButton.setText("Skip");
				buttonBar.add(cancelButton);
			}
			dialogPane.add(buttonBar);
			buttonBar.setBounds(2, 279, 288, buttonBar.getPreferredSize().height);

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
		setSize(300, 383);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Tr?n Long Tu?n V?
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel panel1;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel label4;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
