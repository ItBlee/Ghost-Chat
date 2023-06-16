package com.itblee.constant;

import com.itblee.utils.PropertyUtil;

public interface ServerConstant {

    String RESOURCE_PATH = PropertyUtil.getString("resource.root.path");

    int PORT = PropertyUtil.getInt("connect.port");
    int PORT_SECURE = PropertyUtil.getInt("connect.port.secure");

    String KEY_STORE_PATH = RESOURCE_PATH + PropertyUtil.getString("jsse.keystore.path");
    String KEY_STORE_ALIAS = PropertyUtil.getString("jsse.keystore.alias");
    String KEY_STORE_PWD_HASH = PropertyUtil.getString("jsse.keystore.pwd.hash");
    String KEY_STORE_PWD_SALT = PropertyUtil.getString("jsse.keystore.pwd.salt");

    int SESSION_EXPIRED_TIME = PropertyUtil.getInt("session.time.expire");

    int SOCKET_TIMEOUT = PropertyUtil.getInt("socket.timeout");
    int SECURE_SOCKET_TIMEOUT = PropertyUtil.getInt("socket.timeout.secure");

    int REQUEST_TIMEOUT = PropertyUtil.getInt("request.timeout");

    int NAME_LIMIT = PropertyUtil.getInt("validate.username.limit");

}
