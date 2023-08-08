package com.itblee.core;

import com.itblee.utils.StringUtil;

public class User {
    private String username;

    public void toAnonymous() {
        username = null;
    }

    public boolean authenticated() {
        return !anonymous();
    }

    public boolean anonymous() {
        return StringUtil.isBlank(username);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
