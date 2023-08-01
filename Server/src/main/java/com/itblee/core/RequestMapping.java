package com.itblee.core;

import com.itblee.core.Impl.UserSession;
import com.itblee.core.helper.ServerHelper;
import com.itblee.core.helper.TransferHelper;
import com.itblee.exception.UnauthenticatedException;
import com.itblee.model.Message;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Request;
import com.itblee.transfer.StatusCode;

import java.util.Optional;
import java.util.UUID;

public final class RequestMapping {

    static final Controller CONNECT = new Controller() {{
        ServerService service = ServerHelper.getService();

        map(Request.BREAK_CONNECT, (worker, data) -> service.breakConnection(worker));

        map(Request.AUTH_LOGIN, (worker, data) -> {
            String username = data.get(DataKey.USERNAME);
            String password = data.get(DataKey.PASSWORD);
            StatusCode code = service.login(worker.getUid(), username, password);
            switch (code) {
                case OK:
                    TransferHelper.call(worker).responseLogin(username, StatusCode.OK);
                    break;
                case CONFLICT:
                case FORBIDDEN:
                case BAD_REQUEST:
                case NOT_FOUND:
                    TransferHelper.call(worker).responseLogin(code);
                    break;
            }
        });

        map(Request.AUTH_REGISTER, (worker, data) -> {
            String username = data.get(DataKey.USERNAME);
            String password = data.get(DataKey.PASSWORD);
            StatusCode code = service.register(worker.getUid(), username, password);
            switch (code) {
                case CREATED:
                    TransferHelper.call(worker).responseRegister(username, StatusCode.CREATED);
                    break;
                case CONFLICT:
                case BAD_REQUEST:
                case INTERNAL_SERVER_ERROR:
                case NOT_FOUND:
                    TransferHelper.call(worker).responseRegister(code);
                    break;
            }
        });

        map(Request.AUTH_LOGOUT, (worker, data) -> {
            try {
                service.logout(worker.getUid());
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

        map(Request.FIND_CHAT, (worker, data) -> {
            try {
                service.findChatFor(worker.getUid());
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

        map(Request.STOP_FIND, (worker, data) -> {
            try {
                service.stopFindChat(worker.getUid());
                worker.complete(data);
                TransferHelper.call(worker).warnStopFindChat();
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

        map(Request.INVITE_CHAT, Worker::complete);

        map(Request.CONFIRM_CHAT, Worker::complete);

        map(Request.LEAVE_CHAT, (worker, data) -> {
            try {
                service.leaveChat(worker.getUid());
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

        map(Request.SEND_MESSAGE, (worker, data) -> {
            Optional<Message> message = data.get(DataKey.MESSAGE_BODY, Message.class);
            if (!message.isPresent())
                return;
            try {
                service.syncMessage(worker.getUid(), message.get());
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

        lock();
    }};

    static final Controller SSL = new Controller() {{
        ServerService service = ServerHelper.getService();

        map(Request.BREAK_CONNECT, (worker, data) -> worker.close());

        map(Request.SESSION, (worker, data) -> {
            String secretKey = data.get(DataKey.SECRET_KEY);
            try {
                UserSession session = service.createSession(secretKey);
                TransferHelper.call(worker).responseSession(session.getUid(), StatusCode.CREATED);
                worker.close();
            } catch (IllegalArgumentException e) {
                TransferHelper.call(worker).responseSession(StatusCode.BAD_REQUEST);
            }
        });

        map(Request.SESSION_KEY, (worker, data) -> {
            String stringId = data.get(DataKey.SESSION_ID);
            String secretKey = data.get(DataKey.SECRET_KEY);
            UUID uid = UUID.fromString(stringId);
            if (service.changeSessionKey(uid, secretKey)) {
                TransferHelper.call(worker).responseChangeKey(StatusCode.OK);
                worker.close();
            } else TransferHelper.call(worker).responseSession(StatusCode.BAD_REQUEST);
        });

        lock();
    }};

}
