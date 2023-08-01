package com.itblee.constant;

import com.itblee.utils.PropertyUtil;

public interface ClientConstant {

    boolean SSL_DEBUG_ENABLE = false;

    String RESOURCE_PATH = PropertyUtil.getString("resource.root.path");

    long REQUEST_TIMEOUT = PropertyUtil.getInt("request.timeout");

}
