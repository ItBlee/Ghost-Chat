package com.itblee.core;

import com.itblee.core.Impl.DispatcherImpl;
import com.itblee.core.Impl.GateImpl;
import com.itblee.core.Impl.SecureGate;
import com.itblee.utils.PropertyUtil;

import java.io.IOException;

import static com.itblee.constant.ServerConstant.*;

public class Server {

    private static Server server;

    private Gate gate;
    private Gate sslGate;

    private Dispatcher dispatcher;
    private Dispatcher sslDispatcher;

    private final ServerService serverService = new ServerService();
    private final UserManager userManager = new UserManager();
    private final ChatManager chatManager = new ChatManager();
    private final Logger logger = new Logger();

    public static synchronized Server getInstance() {
        if (server == null)
            server = new Server();
        return server;
    }

    public void launch(String keyStorePassword) throws IOException {
        int port = PropertyUtil.getInt("connect.port");
        int portSecure = PropertyUtil.getInt("connect.auth.port");
        gate = new GateImpl(port);
        gate.open();

        dispatcher = new DispatcherImpl(gate);
        dispatcher.setController(RequestMapping.CONNECT);
        dispatcher.setTimeout(SOCKET_TIMEOUT);
        dispatcher.start();

        sslGate = new SecureGate(portSecure, keyStorePassword);
        sslGate.open();

        sslDispatcher = new DispatcherImpl(sslGate);
        sslDispatcher.setController(RequestMapping.SSL);
        sslDispatcher.setTimeout(SOCKET_AUTH_TIMEOUT);
        sslDispatcher.start();

        userManager.start();
    }

    public void shutdown() throws IOException {
        if (server == null)
            throw new IllegalStateException("You have to call init first");
        userManager.getSessions().forEach(logger::log);
        userManager.clear();
        if (dispatcher != null && dispatcher.isAlive())
            dispatcher.interrupt();
        if (sslDispatcher != null && sslDispatcher.isAlive())
            sslDispatcher.interrupt();
        if (gate != null)
            gate.close();
        if (sslGate != null)
            sslGate.close();
    }

    public ServerService getService() {
        return serverService;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public Logger getLogger() {
        return logger;
    }
}
