package Client;

import ClientGUI.ClientGUI;
import ClientGUI.Dialog;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //System.err.close();

        //Khởi tạo giao diện
        FlatIntelliJLaf.setup();
        FlatLightLaf.installLafInfo();
        FlatDarkLaf.installLafInfo();
        FlatIntelliJLaf.installLafInfo();
        FlatDarculaLaf.installLafInfo();
        UIManager.put( "Button.arc", 999 );
        UIManager.put( "ScrollBar.trackArc", 999 );
        UIManager.put( "ScrollBar.thumbArc", 999 );
        UIManager.put( "ScrollBar.trackInsets", new Insets( 2, 4, 2, 4 ) );
        UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
        UIManager.put( "ScrollBar.track", new Color( 0xe0e0e0 ) );

        Client.Frame = new ClientGUI();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Client.Frame.setVisible(true);
            }
        });

        //Cấu trúc Client và kết nối tới Server
        try {
            Client.connectServer();
        } catch (IOException | NullPointerException ignored) { //Nếu kết nói thất bại
            System.out.println("Server closed.");
            Dialog.newAlertDialog(Client.Frame, Client.FAIL_CONNECT);
            Client.Frame.stopChecking("stop");
            Client.Frame.stopFinding();
        }
    }
}
