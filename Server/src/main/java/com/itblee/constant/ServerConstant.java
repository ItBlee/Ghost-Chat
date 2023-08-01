package com.itblee.constant;

import com.itblee.utils.PropertyUtil;

public interface ServerConstant {

    String RESOURCE_PATH = PropertyUtil.getString("resource.root.path");

    String KEY_STORE_PWD_HASH = PropertyUtil.getString("jsse.keystore.pwd.hash");
    String KEY_STORE_PWD_SALT = PropertyUtil.getString("jsse.keystore.pwd.salt");

    int SOCKET_TIMEOUT = PropertyUtil.getInt("socket.timeout");
    int SOCKET_AUTH_TIMEOUT = PropertyUtil.getInt("socket.auth.timeout");

}
