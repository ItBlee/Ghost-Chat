package com.itblee.gui;

import com.itblee.repository.document.Log;
import com.itblee.utils.JsonParser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.itblee.constant.Resource.ICON;

public class UserLogFrame extends JFrame {

    private final List<Log> logs = new ArrayList<>();

    public UserLogFrame() {
        initComponent();
    }

    private void initComponent() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane();
        JScrollPane jScrollPane1 = new JScrollPane();
        JTable table = new JTable() {
            final DefaultTableCellRenderer renderRight = new DefaultTableCellRenderer();
            {
                renderRight.setHorizontalAlignment(SwingConstants.RIGHT);
            }
            @Override
            public TableCellRenderer getCellRenderer (int arg0, int arg1) {
                return renderRight;
            }
        };
        table.setDragEnabled(false);

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] columnHeader = new String [] {
                "UID", "Contact", "Action", "Date"
        };
        model.setColumnIdentifiers(columnHeader);

        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(225);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Log log = logs.get(table.getSelectedRow());
                    String builder = "At  " + log.getCreatedDate() + "\n" +
                            "Session ID: " + log.getUid() + "\n" +
                            "Contact: " + log.getContact() + "\n" +
                            "Action: " + log.getAction() + "\n\n" +
                            "Details:\n" + JsonParser.toPrettyJson(log.getDetail()) + "\n";
                    textArea.setText(builder);
                } catch (Exception ignored) {}
            }
        });

        jScrollPane.setViewportView(table);
        jScrollPane.setPreferredSize(new Dimension(725,400));
        jScrollPane1.setViewportView(textArea);
        jScrollPane1.setPreferredSize(new Dimension(725,300));

        JPanel panel = new JPanel();
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxlayout);
        panel.add(jScrollPane);
        panel.add(jScrollPane1);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setIconImage(ICON);
        setAlwaysOnTop(true);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    public void display(Collection<Log> logs) {
        this.logs.clear();
        this.logs.addAll(logs);
        model.setRowCount(0);
        Object[] rowData = new Object[4];
        for (Log log : this.logs) {
            rowData[0] = log.getUid();
            rowData[1] = log.getContact();
            rowData[2] = log.getAction();
            rowData[3] = log.getCreatedDate();
            model.addRow(rowData);
        }
        setVisible(true);
    }

    private DefaultTableModel model;
}
