package com.itblee.gui.page;

import com.itblee.gui.ClientFrame;
import com.itblee.gui.component.AbstractPane;

import javax.swing.*;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;
import static com.itblee.constant.Resource.COLOR_DARK_BLUE;

public class LoadingPage extends AbstractPane {

    public LoadingPage(ClientFrame owner) {
        super(owner);
    }

    @Override
    public void initComponents() {
        setOpaque(true);
        setFocusable(true);
        setBackground(COLOR_DARK_BLUE);
        JLabel bg = new JLabel();
        bg.setIcon(new ImageIcon(RESOURCE_PATH + "images/loading/loading.gif"));
        add(bg);
        bg.setBounds(0, 0, 350, 735);
    }

}
