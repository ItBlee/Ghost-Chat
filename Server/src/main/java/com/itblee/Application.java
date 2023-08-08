package com.itblee;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.itblee.core.*;
import com.itblee.gui.ServerManagerFrame;

import java.awt.*;
import java.io.IOException;

public class Application {

    public static void main(String[] args) {
        ServerContainer.userManager = new UserManager();
        ServerContainer.chatManager = new ChatManager();
        ServerContainer.logger = new Logger();
        ServerContainer.serverService = new MyServerService(
                ServerContainer.userManager,
                ServerContainer.chatManager,
                ServerContainer.logger
        );
        ServerContainer.server = MyServer.init(ServerContainer.serverService);
        FlatIntelliJLaf.setup();
        try {
            ServerContainer.server.launch();
            EventQueue.invokeLater(() -> new ServerManagerFrame().setVisible(true));
        } catch (IOException e) {
            throw new IllegalStateException("Problem while starting server.");
        }
    }

}
