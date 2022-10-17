package GUI.panel;

import tranfer.Packet;
import GUI.ClientGUI;
import GUI.component.Dialog;
import GUI.component.RoundJTextField;
import GUI.component.RoundPanel;
import GUI.handler.AlertHandler;
import tranfer.Header;
import core.Launcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

import static constant.ClientConstant.*;

public class HomePanel extends JLayeredPane {

    public HomePanel() {
        initComponents();
    }

    private void initComponents() {
        lbAPPIcon = new JLabel();
        lbAPPName = new JLabel();
        inputPanel = new RoundPanel(60);
        lbWelcome = new JLabel();
        lbTitles = new JLabel();
        txtName = new RoundJTextField(10);
        btnJoin = new JButton("JOIN");
        btnQuit = new JButton("QUIT");

        setBackground(Color.white);
        setOpaque(true);
        setFocusable(true);
        requestFocus();

        //---- lbAPPIcon ----
        lbAPPIcon.setBackground(Color.white);
        lbAPPIcon.setIcon(new ImageIcon("images/icon.png"));
        this.add(lbAPPIcon, JLayeredPane.DEFAULT_LAYER);
        lbAPPIcon.setBounds(105, 70, 134, 139);

        //---- lbAPPName ----
        lbAPPName.setBackground(Color.white);
        lbAPPName.setIcon(new ImageIcon("images/APP.png"));
        this.add(lbAPPName, JLayeredPane.DEFAULT_LAYER);
        lbAPPName.setBounds(44, 180, 255, 148);

        //======== inputNamePanel ========
        {
            inputPanel.setBackground(new Color(134, 238, 252));
            inputPanel.setOpaque(false);
            inputPanel.setLayout(null);

            //---- lbWelcome ----
            lbWelcome.setText("   Let's Chat Together");
            lbWelcome.setFont(new Font("Segoe UI", Font.BOLD, 27));
            lbWelcome.setForeground(Color.white);
            inputPanel.add(lbWelcome);
            lbWelcome.setBounds(new Rectangle(new Point(25, 40), lbWelcome.getPreferredSize()));

            //---- lbTitles ----
            lbTitles.setText("");
            lbTitles.setFont(new Font("Arial", Font.PLAIN, 14));
            lbTitles.setForeground(Color.white);
            lbTitles.setLabelFor(txtName);
            inputPanel.add(lbTitles);
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
            inputPanel.add(txtName);
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
            inputPanel.add(btnJoin);
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
            inputPanel.add(btnQuit);
            btnQuit.setBounds(25, 215, 290, 39);
        }
        this.add(inputPanel, JLayeredPane.DEFAULT_LAYER);
        inputPanel.setBounds(0, 400, 335, 320);
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
                    if (!Launcher.getInstance().getClient().isClosed()) {
                        throw new IOException(FAIL_CONNECT);
                    }
                    String name = txtName.getText();
                    if (name.length() > NAME_LIMIT
                            || name.isEmpty() || name.isBlank()) {
                        AlertHandler.showErrorAlert(
                                Launcher.getInstance().getFrame(),
                                "Invalid Name");
                        lbTitles.setText("Must less than 10 characters !");
                        txtName.setEnabled(true);
                        btnJoin.setEnabled(true);
                        return;
                    }
                    btnJoin.setFont(btnJoin.getFont().deriveFont(Font.BOLD, 24));
                    btnQuit.setText("CANCEL");
                    lbWelcome.setText("Checking");
                    lbAPPIcon.setIcon(new ImageIcon("images/loading.gif"));
                    Packet packet = new Packet();
                    packet.setHeader(Header.NAME_CHECK););
                    /*request.setData(name);
                    ClientWorker.requestHandle(request);*/
                    int i = 1;
                    int waitTime = 0;
                    Launcher.getInstance().getFrame().startChecking();
                    while (Launcher.getInstance().getFrame().isChecking() || waitTime < 2000) {
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
                    if (checkResult.equalsIgnoreCase(STOP_REQUEST))
                        return;
                    if (checkResult.equalsIgnoreCase(DECLINE_REQUEST)) {
                        GUI.component.Dialog.newAlertDialog(ClientGUI.this, "Name Used");
                        resetHomePage();
                    }
                    else {
                        ClientWorker.name = txtName.getText();
                        lbTitles.setText("Hi " + ClientWorker.name + ", please wait :)");
                        startFinding();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    GUI.component.Dialog.newAlertDialog(ClientGUI.this, "Got ERROR");
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
                        txtName.setEnabled(true);
                        btnJoin.setEnabled(true);
                    } catch (UnknownError ignored) {
                        lbWelcome.setText("U GOT BANNED !");
                        lbAPPIcon.setIcon(new ImageIcon("images/icon.png"));
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
                DTO dto = new DTO(Header.STOP_FIND););
                ClientWorker.requestHandle(dto);
                stopChecking(STOP_REQUEST);
                stopFinding();
                Thread.sleep(500);
                resetHomePage();
            } catch (IOException | InterruptedException ex) {
                Client.close();
                Client.Frame.showDisconnectAlert();
            }
        }
    }

    //Reset giao diện Login
    public void resetHomePage() {
        stopChecking(STOP_REQUEST);
        stopFinding();
        lbAPPIcon.setIcon(new ImageIcon("images/icon.png"));
        txtName.setEnabled(true);
        btnJoin.setEnabled(true);
        btnJoin.setIcon(null);
        btnJoin.setText("JOIN");
        btnJoin.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        btnQuit.setText("QUIT");
        lbWelcome.setText("   Let's Chat Together");
        lbTitles.setText("");
    }

    /**
     * Khởi động tìm kiếm bạn chat
     */
    public void startFinding() throws IOException, NullPointerException, InterruptedException {
        lbWelcome.setText("Finding");
        DTO dto = new DTO(Header.FIND_CHAT););
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

    private JLabel lbAPPIcon;
    private JLabel lbAPPName;
    private JLabel lbWelcome;
    private JLabel lbTitles;
    private JPanel inputPanel;
    private JTextField txtName;
    private JButton btnJoin;
    private JButton btnQuit;
}
