package GUI.handler;

import object.Packet;
import GUI.ClientGUI;
import GUI.component.Dialog;
import object.Header;
import core.Launcher;
import utils.StringUtil;

import javax.swing.*;
import java.awt.*;

public class AlertHandler {

    public static void showDisconnectAlert() {
        ClientGUI frame = Launcher.getInstance().getFrame();
        if (frame.isHomePage()) {
            showErrorAlert(frame, "Disconnected");
            frame.getHomePane().resetHomePage();
        }
        else ChatHandler.appendAlert("Disconnected !",true);
    }

    public static void showInviteAlert(Window parent, String pairName) {
        Packet packet = new Packet();
        packet.setHeader(Header.INVITE_CHAT););
        String content = StringUtil.applyWrapForGUI("Invite Friend:\n" + pairName + " ?");
        Dialog dialog = new Dialog(parent, content, true);
        dialog.setIcon(new ImageIcon("images/found.png"));
        dialog.setInnerDTO(packet);
        dialog.setFontColor(new Color(115,170,250));
        dialog.setBtnOkText("INVITE");
        dialog.setBtnCancelText("SKIP");
        dialog.display();
    }

    public static void showInviteFailedAlert(Window parent) {
        String content = StringUtil.applyWrapForGUI("Friend Busy..");
        Dialog dialog = new Dialog(parent, content, false);
        dialog.setIcon(new ImageIcon("images/fail.png"));
        dialog.setInnerDTO(new Packet());
        dialog.setFontColor(new Color(115,170,250));
        dialog.setBtnOkText(null);
        dialog.setBtnCancelText("GOT IT");
        dialog.display();
    }

    public static void showConfirmAlert(Window parent, Packet packet) {
        String content = StringUtil.applyWrapForGUI("Accept invite:\n" + packet.getData() + " ?");
        Dialog dialog = new Dialog(parent, content, true);
        dialog.setIcon(new ImageIcon("images/accept.png"));
        dialog.setInnerDTO(packet);
        dialog.setFontColor(new Color(115,170,250));
        dialog.setBtnOkText("ACCEPT");
        dialog.setBtnCancelText("DECLINE");
        dialog.display();
    }

    public static void showErrorAlert(Window parent, String error) {
        String content = StringUtil.applyWrapForGUI(error);
        Dialog dialog = new Dialog(parent, content, false);
        dialog.setIcon(new ImageIcon("images/error.png"));
        dialog.setInnerDTO(new Packet());
        dialog.setFontColor(new Color(250, 115, 115));
        dialog.setBtnOkText(null);
        dialog.setBtnCancelText("GOT IT");
        dialog.display();
    }

}
