package com.itblee.transfer;

public enum MyRequest implements Request {
    LEAVE_CHAT,
    FIND_CHAT,
    STOP_FIND,

    INVITE_CHAT,
    CONFIRM_CHAT,
    CHAT_INFO,
    FRIEND_INFO,

    USER_INFO,
    HISTORY_RECOVERY,

    SEND_MESSAGE,
    RECEIVE_MESSAGE,
    SERVER_MESSAGE,

    AUTH_LOGIN,
    AUTH_REGISTER,
    AUTH_LOGOUT,
    UNAUTHENTICATED
}
