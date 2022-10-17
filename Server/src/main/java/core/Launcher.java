package core;

import constant.ServerMethods;
import security.Certificate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Launcher {
    private static Launcher launcher;
    private final Server server;
    private final Server secureServer;

    private final Set<Certificate> sessions;

    private Dispatcher dispatcher;
    private Dispatcher secureDispatcher;
    private PairDispatcher pairDispatcher;

    private Launcher(Server server, Server serverSecure) {
        this.server = server;
        this.secureServer = serverSecure;
        sessions = Collections.synchronizedSet(new HashSet<Certificate>());
    }

    public static synchronized Launcher init(Server server, Server serverSecure) {
        if (launcher != null)
            throw new AssertionError("Already initialized launcher !");

        if (server.isOpened() && server.isSecured())
            throw new IllegalStateException("invalid main Server !");

        if (serverSecure.isOpened() && !serverSecure.isSecured())
            throw new IllegalStateException("invalid security Server !");

        launcher = new Launcher(server, serverSecure);
        return launcher;
    }

    public static synchronized Launcher getInstance() {
        if (launcher == null)
            throw new AssertionError("You have to call init first");
        return launcher;
    }

    public void launch(String keyStorePassword) throws IOException {
        server.open();
        dispatcher = new Dispatcher(server, ServerMethods.methods);
        dispatcher.start();

        secureServer.openSecure(keyStorePassword);
        secureDispatcher = new Dispatcher(secureServer, ServerMethods.methods);
        secureDispatcher.start();

        pairDispatcher = new PairDispatcher();
        pairDispatcher.start();
    }

    public void close() throws IOException {
        if (sessions != null && !sessions.isEmpty())
            sessions.clear();
        if (dispatcher != null && dispatcher.isAlive())
            dispatcher.interrupt();
        if (secureDispatcher != null && secureDispatcher.isAlive())
            secureDispatcher.interrupt();
        if (server != null)
            server.close();
        if (secureServer != null)
            secureServer.close();
    }

    public synchronized Set<Certificate> getSessions() {
        return sessions;
    }
}
