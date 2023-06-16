package com.itblee.constant;

import com.itblee.utils.PropertyUtil;

public interface ClientConstant {

    boolean SSL_DEBUG_ENABLE = false;

    String RESOURCE_PATH = PropertyUtil.getString("resource.root.path");

    String IP = PropertyUtil.getString("connect.ip");
    int PORT = PropertyUtil.getInt("connect.port");
    int PORT_SECURE = PropertyUtil.getInt("connect.port.secure");

    String TRUST_STORE_PATH = RESOURCE_PATH + PropertyUtil.getString("jsse.truststore.path");
    String TRUST_STORE_PWD = PropertyUtil.getString("jsse.truststore.pwd");

    int SOCKET_TIMEOUT = PropertyUtil.getInt("socket.timeout");
    int SECURE_SOCKET_TIMEOUT = PropertyUtil.getInt("socket.timeout.secure");

    int REQUEST_TIMEOUT = PropertyUtil.getInt("request.timeout");

    int LIMIT_MESSAGE_LINE = PropertyUtil.getInt("validate.message.line.limit");
    int LIMIT_INPUT_LINE = PropertyUtil.getInt("validate.input.message.line.limit");
    int CHAT_AUTO_LEFT_TIME = PropertyUtil.getInt("Chatroom.auto.leave.time");
    int NAME_LIMIT = PropertyUtil.getInt("validate.username.limit");


}
