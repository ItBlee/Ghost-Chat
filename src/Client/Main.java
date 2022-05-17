package Client;

import ClientGUI.ClientGUI;
import ClientGUI.Dialog;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //Khởi tạo giao diện
        FlatIntelliJLaf.setup();
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
        } catch (IOException | NullPointerException | UnknownError ignored) { //Nếu kết nói thất bại
            System.out.println(Client.FAIL_CONNECT);
            Dialog.newAlertDialog(Client.Frame, Client.FAIL_CONNECT);
            Client.Frame.stopChecking("stop");
            Client.Frame.stopFinding();
        }
    }
}
