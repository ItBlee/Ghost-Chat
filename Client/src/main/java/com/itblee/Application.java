package com.itblee;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.itblee.core.Client;
import com.itblee.gui.ClientFrame;

import javax.swing.*;
import java.awt.*;

public class Application {

    public static void main(String[] args) {
        setupTheme();
        ClientFrame frame = new ClientFrame();
        EventQueue.invokeLater(() -> frame.setVisible(true));
        Client.init(frame);
        try {
            frame.showLoading();
            Thread.sleep(2000);
            frame.showLogin();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*try {
            Client.newInstance(frame).requestSession();
            frame.showLogin();
        } catch (IOException e) {
            frame.showDisconnect();
        }*/
    }

    public static void setupTheme() {
        FlatIntelliJLaf.setup();
        UIManager.put( "Button.arc", 25 );
        UIManager.put( "ScrollBar.trackArc", 999 );
        UIManager.put( "ScrollBar.thumbArc", 999 );
        UIManager.put( "ScrollBar.trackInsets", new Insets( 2, 4, 2, 4 ) );
        UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
        UIManager.put( "ScrollBar.track", new Color( 0xe0e0e0 ) );
    }
}
