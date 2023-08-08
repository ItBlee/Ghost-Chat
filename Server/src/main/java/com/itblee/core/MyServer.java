package com.itblee.core;

import com.itblee.core.server.Server;
import com.itblee.utils.PropertyUtil;

public class MyServer {

    public static Server init(MyServerService service) {
        int port = PropertyUtil.getInt("connect.port");
        int sslPort = PropertyUtil.getInt("connect.ssl.port");
        Server server = Server.init(port, sslPort);
        server.addController(RequestMapping.CONNECT);
        server.setService(service);
        return server;
    }

}
