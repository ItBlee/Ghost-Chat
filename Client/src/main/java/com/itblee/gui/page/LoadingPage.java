package com.itblee.gui.page;

import com.itblee.gui.component.TransitionPane;

import javax.swing.*;

import static com.itblee.constant.Resource.BG_LOADING;

public class LoadingPage extends TransitionPane {

    public LoadingPage() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(true);
        setFocusable(true);

        JLabel bg = new JLabel();
        bg.setIcon(BG_LOADING);
        add(bg, JLayeredPane.DEFAULT_LAYER);
        bg.setBounds(0, -38, 365, 735);
    }

    @Override
    public void doIntro() {
    }

    @Override
    public void doOutro() {
    }

}
