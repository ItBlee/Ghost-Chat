package com.itblee;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.itblee.gui.ServerGUI;
import com.itblee.core.Gate;
import com.itblee.core.Impl.GateImpl;
import com.itblee.core.Impl.SecureGate;
import com.itblee.core.Server;

import java.io.IOException;

public class Application {

    public static void main(String[] args) {

    }

    public static void invokeGUI() {
        FlatIntelliJLaf.setup();
        ServerGUI.showAuthenticationFrame();
    }

    public static void setupServer() throws IOException {
        Gate mainGate = new GateImpl(5000);
        Gate secureGate = new SecureGate(5005, "");
        Server.launch();
    }

}
