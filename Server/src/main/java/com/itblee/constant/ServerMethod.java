package com.itblee.constant;

import com.itblee.core.Controller;
import com.itblee.core.ServerService;
import com.itblee.core.Worker;
import com.itblee.model.Message;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Header;

import java.util.UUID;

public interface ServerMethod {

    Controller CONNECT = new Controller() {{
        put(Header.BREAK_CONNECT, (worker, data) -> ServerService.breakConnection(worker));

        put(Header.FIND_CHAT, (worker, data) -> ServerService.makeUserOnline(worker.getUid()));

        put(Header.STOP_FIND, (worker, data) -> ServerService.makeUserOffline(worker.getUid()));

        put(Header.INVITE_CHAT, Worker::complete);

        put(Header.CONFIRM_CHAT, Worker::complete);

        put(Header.LEAVE_CHAT, (worker, data) -> {
            ServerService.leaveChat(worker.getUid());
        });

        put(Header.SEND_MESSAGE, (worker, data) -> {
            Message message = data.get(DataKey.MESSAGE_BODY);
            ServerService.syncMessage(worker.getUid(), message);
        });

        put(Header.REGISTER_NAME, (worker, data) -> {
            String username = data.get(DataKey.USERNAME);
            ServerService.registerUsername(worker, username);
        });

        lock();
    }};

    Controller AUTH = new Controller() {{
        put(Header.AUTH_REGISTER, (worker, data) -> {
            String secretKey = data.get(DataKey.SECRET_KEY);
            UUID uid = data.get(DataKey.SESSION_ID);
            ServerService.registerSession(worker, uid, secretKey);
        });

        lock();
    }};

}
