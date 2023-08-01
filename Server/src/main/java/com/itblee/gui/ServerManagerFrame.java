package com.itblee.gui;

import com.itblee.core.Impl.UserSession;
import com.itblee.core.Server;
import com.itblee.core.ServerService;
import com.itblee.core.helper.ServerHelper;
import com.itblee.exception.NotFoundException;
import com.itblee.repository.document.Log;
import com.itblee.utils.PropertyUtil;
import com.itblee.utils.StringUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.itblee.constant.Resource.ICON;
import static com.itblee.constant.ServerConstant.RESOURCE_PATH;

public class ServerManagerFrame extends JFrame {

    private static final String[] TUTORIALS = {
            PropertyUtil.getString("tutorial.common"),
            PropertyUtil.getString("tutorial.online"),
            PropertyUtil.getString("tutorial.offline")
    };

    private static final int TUTORIAL_COMMON = 0;
    private static final int TUTORIAL_ONLINE = 1;
    private static final int TUTORIAL_OFFLINE = 2;

    private int tutorialSelected;

    private boolean lock = false;
    private UserSession selectedUser;

    public ServerManagerFrame() {
        initComponents();
    }

    private void initComponents() {
        JPanel jPanel1 = new JPanel();
        JButton btnList = new JButton();
        btnExecute = new JButton();
        JPanel jPanel2 = new JPanel();
        btnLog = new JButton();
        lbAlert = new JLabel();
        jLabel2 = new JLabel();
        txtSearch = new JTextField();
        btnCheck = new JButton();
        JLabel jLabel3 = new JLabel();
        JScrollPane jScrollPane1 = new JScrollPane();
        messageArea = new JTextArea();

        btnList.setIcon(new ImageIcon(RESOURCE_PATH + "images/add.png"));
        btnList.setFocusPainted(false);
        btnList.addActionListener(this::showUserList);

        btnExecute.setEnabled(false);
        btnExecute.setIcon(new ImageIcon(RESOURCE_PATH + "images/play_arrow.png"));
        btnExecute.setText("Execute");
        btnExecute.setFocusPainted(false);
        btnExecute.addActionListener(this::execute);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        btnLog.setEnabled(false);
        btnLog.setIcon(new ImageIcon(RESOURCE_PATH + "images/list_alt.png"));
        btnLog.setFocusPainted(false);
        btnLog.addActionListener(this::showUserLogs);

        lbAlert.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
        lbAlert.setText("");
        lbAlert.setHorizontalAlignment(SwingConstants.CENTER);

        jLabel2.setText("To User:");

        btnCheck.setIcon(new ImageIcon(RESOURCE_PATH + "images/check_icon.png"));
        btnCheck.setFocusPainted(false);
        btnCheck.addActionListener(e -> search());

        jLabel3.setText("Message:");

        txtSearch.setFont(new Font("roboto", Font.BOLD, 16));
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtSearch.setForeground(new Color(142, 142, 142));
        String ph_textField = "Check user status first";
        txtSearch.setText(ph_textField);
        txtSearch.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(txtSearch.getText().equalsIgnoreCase(ph_textField)){
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.black);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(StringUtil.isBlank(txtSearch.getText())) {
                    txtSearch.setText(ph_textField);
                    txtSearch.setForeground(new Color(142, 142, 142));
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                reset();
            }
            public void removeUpdate(DocumentEvent e) {
                reset();
            }
            public void insertUpdate(DocumentEvent e) {
                reset();
            }
        });

        messageArea.setColumns(20);
        messageArea.setRows(3);
        messageArea.setFont(new Font("roboto", Font.PLAIN, 16));
        messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        messageArea.setForeground(new Color(142, 142, 142));
        messageArea.setTabSize(2);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        tutorialSelected = TUTORIAL_COMMON;
        messageArea.setText(getTutorialText());
        messageArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageArea.getText().equalsIgnoreCase(getTutorialText())){
                    messageArea.setText("");
                    messageArea.setForeground(Color.black);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isBlank(messageArea.getText())){
                    messageArea.setText(getTutorialText());
                    messageArea.setForeground(new Color(142, 142, 142));
                }
            }
        });
        /*messageArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {}
            public void removeUpdate(DocumentEvent e) {
                if (selectedUser != null) return;
                btnExecute.setEnabled(messageArea.getText().startsWith("#"));
            }
            public void insertUpdate(DocumentEvent e) {
                if (selectedUser != null) return;
                btnExecute.setEnabled(messageArea.getText().startsWith("#"));
            }
        });*/
        jScrollPane1.setViewportView(messageArea);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(btnList, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnLog, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                    .addGap(191, 191, 191)
                                    .addComponent(lbAlert)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnExecute, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
                                .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, 609, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnCheck, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 652, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnList, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLog, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                    .addComponent(lbAlert)
                    .addComponent(btnExecute))
                .addGap(20, 20, 20)
                .addComponent(jLabel2)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCheck, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 163, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 367, GroupLayout.PREFERRED_SIZE)
        );

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Server manager - ChatApp");
        setIconImage(ICON);
        pack();
        setLocationRelativeTo(null);
    }

    private void showUserList(ActionEvent evt) {
        List<UserSession> users = ServerHelper.getService().getUsers();
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this,"There are currently no users !","Empty", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (userTable == null)
            userTable = new UserListTable();
        userTable.fillData(users);
        int result = JOptionPane.showConfirmDialog(this, userTable, "User List", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.YES_OPTION && userTable.getSelectedRow() != -1) {
            selectedUser = users.get(userTable.getSelectedRow());
            applyUser();
        }
    }

    private void execute(ActionEvent evt) {
        if (messageArea.getText().isEmpty() || messageArea.getText().equalsIgnoreCase(getTutorialText()))
            return;
        ServerService service = ServerHelper.getService();

        if (!messageArea.getText().startsWith("#")) {
            try {
                service.sendAlert(selectedUser, messageArea.getText());
                lbAlert.setText("SENT");
            } catch (Exception e) {
                lbAlert.setText("User not connected !");
            }
            return;
        }

        String command = messageArea.getText();
        if (command.toUpperCase().startsWith("#ALL")) {
            for (UserSession user : ServerHelper.getUserManager().getSessions()) {
                String message = command.trim().replaceAll("(?i)#all ", "");
                try {
                    service.sendAlert(user, message);
                } catch (Exception ignored) {}
            }
            lbAlert.setText("SENT ALL");
            return;
        }
        if (selectedUser == null) {
            switch (command.toUpperCase()) {
                case "#STOP":
                    service.stopAllUser();
                    lbAlert.setText("STOPPED ALL !");
                    break;

                default:
                    lbAlert.setText("WRONG !");
                    break;
            }
            return;
        }
        switch (command.toUpperCase()) {
            case "#BAN":
                try {
                    service.banUser(selectedUser);
                    lbAlert.setText("BANNED");
                } catch (NotFoundException e) {
                    lbAlert.setText("USER NOT FOUND !");
                }
                btnCheck.doClick();
                break;
            case "#UNBAN":
                try {
                    service.activeUser(selectedUser.getUsername());
                    lbAlert.setText("UNBANNED");
                } catch (NotFoundException e) {
                    lbAlert.setText("USER NOT FOUND !");
                }
                btnCheck.doClick();
                break;
            case "#STOP":
                service.stopUser(selectedUser);
                lbAlert.setText("STOPPED!");
                break;
            case "#SESSION":
                service.renewSessionTimer(selectedUser);
                lbAlert.setText("RENEW");
                break;
            case "#PWD":
                try {
                    service.resetPassword(selectedUser.getUsername());
                } catch (NotFoundException e) {
                    lbAlert.setText("USER NOT FOUND !");
                }
                break;

            default:
                lbAlert.setText("WRONG !");
                break;
        }
    }

    private void showUserLogs(ActionEvent evt) {
        Collection<Log> logs = ServerHelper.getLogger().getUserLogs(selectedUser.getUsername());
        if (logs.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Currently no history !",
                    "Empty",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (userLogFrame == null)
            userLogFrame = new UserLogFrame();
        userLogFrame.setTitle(selectedUser.getUsername() + " - CheckSyntax");
        userLogFrame.display(logs);
    }

    private void search() {
        String input = txtSearch.getText();
        Optional<UserSession> found = ServerHelper.getService().searchByName(input);
        if (found.isPresent()) {
            selectedUser = found.get();
            applyUser();
        } else {
            jLabel2.setText("To User: NOT FOUND");
            txtSearch.setBackground(new Color(231, 76, 60));
            tutorialSelected = TUTORIAL_COMMON;
            messageArea.setText(getTutorialText());
        }
        txtSearch.setForeground(Color.white);
    }

    private void reset() {
        if (lock)
            return;
        jLabel2.setText("To User: unchecked");
        txtSearch.setBackground(Color.white);
        txtSearch.setForeground(Color.black);
        messageArea.setForeground(new Color(142, 142, 142));
        tutorialSelected = TUTORIAL_COMMON;
        messageArea.setText(getTutorialText());
        if (selectedUser != null) {
            selectedUser = null;
            enableExecute(false);
        }
    }

    private void applyUser() {
        lock = true;
        lbAlert.setText("");
        StringBuilder s = new StringBuilder();
        s.append("To User: ");
        if (selectedUser.getUid() != null)
            s.append(selectedUser.getUid());
        if (selectedUser.getIp() != null)
            s.append(" at ").append(selectedUser.getIp());
        jLabel2.setText(s.toString());
        txtSearch.setText(selectedUser.getUsername());
        txtSearch.setForeground(Color.white);
        if (selectedUser.isBanned()) {
            txtSearch.setBackground(new Color(231, 76, 60));
            jLabel2.setText(jLabel2.getText() + " (Banned)");
            tutorialSelected = TUTORIAL_OFFLINE;
        } else if (selectedUser.getIp() == null) {
            txtSearch.setBackground(new Color(231, 231, 60));
            jLabel2.setText(jLabel2.getText() + " (Disconnected)");
            tutorialSelected = TUTORIAL_OFFLINE;
        } else if (selectedUser.isOnline()) {
            txtSearch.setBackground(new Color(117, 236, 99));
            tutorialSelected = TUTORIAL_ONLINE;
        } else {
            txtSearch.setBackground(new Color(117, 236, 99));
            jLabel2.setText(jLabel2.getText() + " (Offline)");
            tutorialSelected = TUTORIAL_OFFLINE;
        }
        messageArea.setForeground(new Color(142, 142, 142));
        messageArea.setText(getTutorialText());
        btnExecute.setEnabled(true);
        btnLog.setEnabled(true);
        lock = false;
    }

    private String getTutorialText() {
        return TUTORIALS[tutorialSelected];
    }

    private void enableExecute(boolean b) {
        btnExecute.setEnabled(b);
        btnLog.setEnabled(b);
    }

    @Override
    public void dispose() {
        try {
            Server.getInstance().shutdown();
        } catch (Exception ignored) {}
        super.dispose();
    }

    public JButton btnCheck;
    private JButton btnExecute;
    private JButton btnLog;
    public JLabel lbAlert;
    private JLabel jLabel2;
    public JTextArea messageArea;
    public JTextField txtSearch;
    private UserListTable userTable;
    private UserLogFrame userLogFrame;
}
