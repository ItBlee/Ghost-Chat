package com.itblee.gui.page;

import com.itblee.gui.ClientFrame;
import com.itblee.gui.component.AbstractPane;

import javax.swing.*;

import static com.itblee.constant.Resource.BG_LOADING;

public class LoadingPage extends AbstractPane {

    public LoadingPage(ClientFrame owner) {
        super(owner);
        initComponents();
    }

    private void initComponents() {
        setOpaque(true);
        setFocusable(true);

        JLabel bg = new JLabel();
        bg.setIcon(BG_LOADING);
        add(bg);
        bg.setBounds(0, 0, 350, 735);
    }

}
