import GUI.ClientGUI;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;

import static constant.ClientConstant.FAIL_CONNECT;

public class Application {

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        UIManager.put( "Button.arc", 999 );
        UIManager.put( "ScrollBar.trackArc", 999 );
        UIManager.put( "ScrollBar.thumbArc", 999 );
        UIManager.put( "ScrollBar.trackInsets", new Insets( 2, 4, 2, 4 ) );
        UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
        UIManager.put( "ScrollBar.track", new Color( 0xe0e0e0 ) );

        JFrame frame = new ClientGUI();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });

            System.out.println(FAIL_CONNECT);
            /*Dialog.newAlertDialog(Frame, FAIL_CONNECT);
            Frame.stopChecking("stop");
            Frame.stopFinding();*/
    }
}
