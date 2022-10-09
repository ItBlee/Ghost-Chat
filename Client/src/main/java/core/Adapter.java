package core;

import javax.swing.*;

public class Adapter {

    public boolean isHomePage() {
        return Launcher.getInstance().getFrame().isHomePage();
    }

    public boolean isChatPage() {
        return Launcher.getInstance().getFrame().isChatPage();
    }

    public boolean isFirstMessage() {
        return Launcher.getInstance().getFrame().getChatPane().getMessageCount() == 0;
    }

    public void appendMessageToChatBox(JPanel panel) {
        Launcher.getInstance().getFrame().getChatPane().appendMessage(panel);
    }

    public void doClick(String componentName) {
        switch (componentName) {
            case "btnBack":
                Launcher.getInstance().getFrame().getChatPane().getBtnBack().doClick();
                return;
        }
    }
}
