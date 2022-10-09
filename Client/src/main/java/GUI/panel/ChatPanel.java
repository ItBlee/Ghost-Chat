package GUI.panel;

import GUI.ClientGUI;
import GUI.component.Dialog;
import GUI.component.RoundJTextField;
import object.Header;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;

import static constant.ClientConstant.LIMIT_INPUT_LINE;

public class ChatPanel extends JLayeredPane {
    private int scrollBarMaxValue;
    private int messageCount;

    public ChatPanel() {
        initComponents();
        messageCount = 0;
    }

    private void initComponents() {
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

        //chatPane.setBackground(new Color(249, 253, 255));
        this.setBackground(Color.white);
        this.setOpaque(true);

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
        }
        this.add(chatHeaderPanel, JLayeredPane.DEFAULT_LAYER);
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
        this.add(btnSend, JLayeredPane.DEFAULT_LAYER);
        btnSend.setBounds(290, 640, 40, 40);

        //---- txtInput ----
        txtInput.setBorder(new EmptyBorder(7,10,5,10));
        txtInput.setBackground(new Color(238,241,249));
        txtInput.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(txtInput, JLayeredPane.DEFAULT_LAYER);
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
        this.add(scrollPane1, JLayeredPane.DEFAULT_LAYER);
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
            scrollPane2.getVerticalScrollBar().addAdjustmentListener(e -> {
                if ((scrollBarMaxValue - e.getAdjustable().getMaximum()) == 0)
                    return;
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                scrollBarMaxValue = scrollPane2.getVerticalScrollBar().getMaximum();
            });
        }
        this.add(scrollPane2, JLayeredPane.DEFAULT_LAYER);
        scrollPane2.setBounds(-2, 72, 340, 550);
    }

    /**
     * Xử lý khi bẩm nút "BACK" ở giao diện Chat
     */
    private void btnBackHandle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DTO dto = new DTO(Header.BREAK_PAIR););
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
                DTO dto = new DTO(Header.MESSAGE););
                dto.setData(input);
                ClientWorker.requestHandle(dto);
                appendSend(input, LocalDateTime.now().toString());
            } else appendSendFail(input, LocalDateTime.now().toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    public void appendMessage(JPanel panel) {
        jChatPanel.add(panel);
        jChatPanel.add(Box.createRigidArea(new Dimension(5,0)));
        jChatPanel.revalidate();
        messageCount++;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

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
}
