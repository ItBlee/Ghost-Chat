package com.itblee.gui;

import com.itblee.core.User;
import com.itblee.utils.Converter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Collection;

public class UserListTable extends JScrollPane {

    public UserListTable() {
        initComponents();
    }

    private void initComponents() {
        table = new JTable() {
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
                "#", "Username", "UID", "IP", "Secret Key", "Timer", "Status", "Created Date"
        };
        model.setColumnIdentifiers(columnHeader);

        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);
        table.getColumnModel().getColumn(7).setPreferredWidth(150);
        //table.getColumnModel().getColumn(7).setPreferredWidth(70);

        setViewportView(table);
        setPreferredSize(new Dimension(903,400));
    }

    public void fillData(Collection<User> users) {
        model.setRowCount(0);
        Object[] rowData = new Object[8];
        for (User user : users) {
            rowData[0] = model.getRowCount() + 1;
            rowData[1] = user.getUsername();
            rowData[2] = user.getUid();
            rowData[3] = user.getIp();
            if (rowData[3] == null)
                rowData[3] = "Disconnected";
            rowData[4] = user.getSession().getSecretKey();
            try {
                long milliseconds = System.currentTimeMillis() - user.getSession().getLatestAccessTime();
                if (milliseconds >= 0)
                    rowData[5] = Converter.convertTime(milliseconds);
            } catch (Exception e) {
                rowData[5] = "Expired";
            }
            rowData[6] = user.getStatus().toString();
            rowData[7] = user.getCreatedDate();
            model.addRow(rowData);
        }
    }

    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    private DefaultTableModel model;
    private JTable table;
}
