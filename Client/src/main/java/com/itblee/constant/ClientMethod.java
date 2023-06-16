package com.itblee.constant;

import com.itblee.core.ClientHelper;
import com.itblee.core.Controller;
import com.itblee.core.Worker;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Header;

import java.util.UUID;

public interface ClientMethod {

    Controller CONNECT = new Controller() {{
        put(Header.REGISTER_NAME, Worker::complete);

        put(Header.INVITE_CHAT, Worker::complete);

        put(Header.CONFIRM_CHAT, Worker::complete);

        put(Header.CHAT_INFO, Worker::complete);

        put(Header.STOP_FIND, Worker::complete);

        put(Header.SEND_MESSAGE, Worker::complete);

        put(Header.RECEIVE_MESSAGE, (worker, data) -> {
            Message message = data.get(DataKey.MESSAGE_BODY);
            ClientHelper.getFrame().appendReceive(message);
        });

        put(Header.FRIEND_INFO, (worker, data) -> {
            FriendInfo info = data.get(DataKey.FRIEND_INFO);
            ClientHelper.getFrame().setFriendInfo(info);
        });

        put(Header.UNAUTHENTICATED, (worker, data) -> {
            ClientHelper.requestSession();
            worker.resend();
        });

        lock();
    }};

    Controller AUTH = new Controller() {{
        put(Header.AUTH_REGISTER, (worker, data) -> {
            UUID sessionId = data.get(DataKey.SESSION_ID);
            ClientHelper.setUid(sessionId);
            worker.close();
        });

        lock();
    }};

}
