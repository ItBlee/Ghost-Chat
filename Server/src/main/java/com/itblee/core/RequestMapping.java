package com.itblee.core;

import com.itblee.core.helper.TransferHelper;
import com.itblee.exception.UnauthenticatedException;
import com.itblee.model.Message;
import com.itblee.transfer.*;

import java.util.Optional;

public final class RequestMapping {

    static final Controller CONNECT = new Controller() {{

        map(MyRequest.AUTH_LOGIN, (worker, data) -> {
            MyServerService service = ServerContainer.serverService;
            String username = data.get(MyDataKey.USERNAME);
            String password = data.get(MyDataKey.PASSWORD);
            DefaultStatusCode code = service.login(worker.getUid(), username, password);
            switch (code) {
                case OK:
                    TransferHelper.call(worker).responseLogin(username, DefaultStatusCode.OK);
                    break;
                case CONFLICT:
                case FORBIDDEN:
                case BAD_REQUEST:
                case NOT_FOUND:
                    TransferHelper.call(worker).responseLogin(code);
                    break;
            }
        });

        map(MyRequest.AUTH_REGISTER, (worker, data) -> {
            MyServerService service = ServerContainer.serverService;
            String username = data.get(MyDataKey.USERNAME);
            String password = data.get(MyDataKey.PASSWORD);
            DefaultStatusCode code = service.register(worker.getUid(), username, password);
            switch (code) {
                case CREATED:
                    TransferHelper.call(worker).responseRegister(username, DefaultStatusCode.CREATED);
                    break;
                case CONFLICT:
                case BAD_REQUEST:
                case INTERNAL_SERVER_ERROR:
                case NOT_FOUND:
                    TransferHelper.call(worker).responseRegister(code);
                    break;
            }
        });

        map(MyRequest.AUTH_LOGOUT, (worker, data) -> {
            MyServerService service = ServerContainer.serverService;
            try {
                service.logout(worker.getUid());
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

        map(MyRequest.FIND_CHAT, (worker, data) -> {
            MyServerService service = ServerContainer.serverService;
            try {
                service.findChatFor(worker.getUid());
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

        map(MyRequest.STOP_FIND, (worker, data) -> {
            MyServerService service = ServerContainer.serverService;
            try {
                service.stopFindChat(worker.getUid());
                worker.complete(data);
                TransferHelper.call(worker).warnStopFindChat();
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

        map(MyRequest.INVITE_CHAT, Worker::complete);

        map(MyRequest.CONFIRM_CHAT, Worker::complete);

        map(MyRequest.LEAVE_CHAT, (worker, data) -> {
            MyServerService service = ServerContainer.serverService;
            try {
                service.leaveChat(worker.getUid());
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

        map(MyRequest.SEND_MESSAGE, (worker, data) -> {
            Optional<Message> message = data.get(MyDataKey.MESSAGE_BODY, Message.class);
            if (!message.isPresent())
                return;
            MyServerService service = ServerContainer.serverService;
            try {
                service.syncMessage(worker.getUid(), message.get());
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });

    }};

}
