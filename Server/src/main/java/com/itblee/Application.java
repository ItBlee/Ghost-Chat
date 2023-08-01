package com.itblee;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.itblee.gui.LauncherFrame;

import java.awt.*;

public class Application {

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        EventQueue.invokeLater(() -> new LauncherFrame().setVisible(true));
    }

}
