package com.itblee.core;

import com.itblee.constant.ServerMethod;
import com.itblee.core.Impl.DispatcherImpl;
import com.itblee.core.Impl.GateImpl;
import com.itblee.core.Impl.SecureGate;

import java.io.IOException;

import static com.itblee.constant.ServerConstant.*;

public class Server {

    private static Server server;

    private Gate gate;
    private Gate secureGate;

    private Dispatcher dispatcher;
    private Dispatcher secureDispatcher;

    private SessionManager sessionManager;
    private ChatManager chatManager;

    public static synchronized Server getInstance() {
        if (server == null)
            server = new Server();
        return server;
    }

    public void launch(String keyStorePassword) throws IOException {
        gate = new GateImpl(PORT);
        gate.open();

        dispatcher = new DispatcherImpl(gate);
        dispatcher.setController(ServerMethod.CONNECT);
        dispatcher.setTimeout(SOCKET_TIMEOUT);
        dispatcher.start();

        secureGate = new SecureGate(PORT_SECURE, keyStorePassword);
        secureGate.open();

        secureDispatcher = new DispatcherImpl(secureGate);
        secureDispatcher.setController(ServerMethod.AUTH);
        secureDispatcher.setTimeout(SECURE_SOCKET_TIMEOUT);
        secureDispatcher.start();

        sessionManager = new SessionManager();
        chatManager = new ChatManager();
    }

    public void shutdown() throws IOException {
        if (server == null)
            throw new IllegalStateException("You have to call init first");
        sessionManager.clear();
        if (dispatcher != null && dispatcher.isAlive())
            dispatcher.interrupt();
        if (secureDispatcher != null && secureDispatcher.isAlive())
            secureDispatcher.interrupt();
        gate.close();
        secureGate.close();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }
}
