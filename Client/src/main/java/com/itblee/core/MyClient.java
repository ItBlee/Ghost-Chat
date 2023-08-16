package com.itblee.core;

import com.itblee.core.client.Client;
import com.itblee.utils.PropertyUtil;

public class MyClient {

    public static Client init() {
        String ip = PropertyUtil.getString("connect.ip");
        int port = PropertyUtil.getInt("connect.port");
        int sslPort = PropertyUtil.getInt("connect.ssl.port");
        Client client = Client.init(ip, port, sslPort);
        client.addController(RequestMapping.CONNECT);
        return client;
    }

}
