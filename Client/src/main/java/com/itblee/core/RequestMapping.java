package com.itblee.core;

import com.google.gson.reflect.TypeToken;
import com.itblee.core.helper.TransferHelper;
import com.itblee.gui.Alert;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.transfer.*;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public final class RequestMapping {

    static final Controller CONNECT = new Controller() {{

        map(MyRequest.UNAUTHENTICATED, (worker, data) -> {
            ClientContainer.user.toAnonymous();
            ClientContainer.frame.getCurrent()
                    .changeTo(ClientContainer.frame::showLogin);
        });

        map(MyRequest.AUTH_LOGIN, (worker, data) -> {
            worker.complete(data);
            switch (data.getCode(DefaultStatusCode.class)) {
                case OK:
                    ClientContainer.user.setUsername(data.get(MyDataKey.USERNAME));
                    ClientContainer.frame.getCurrent()
                            .changeTo(ClientContainer.frame::showHome);
                    break;
                case NOT_FOUND:
                    Alert.showError("Not Registered !");
                    break;
                case CONFLICT:
                    Alert.showError("Already Being Used !");
                    break;
                case FORBIDDEN:
                    Alert.showError("Account Got Banned !");
                    break;
                case INTERNAL_SERVER_ERROR:
                    Alert.showError("Got ERROR !");
                    break;
                default:
                    Alert.showError("Login Failed !");
                    break;
            }
        });

        map(MyRequest.AUTH_REGISTER, (worker, data) -> {
            worker.complete(data);
            switch (data.getCode(DefaultStatusCode.class)) {
                case CREATED:
                    ClientContainer.user.setUsername(data.get(MyDataKey.USERNAME));
                    ClientContainer.frame.getCurrent()
                            .changeTo(ClientContainer.frame::showHome);
                    break;
                case CONFLICT:
                    Alert.showError("Username used");
                    break;
                case BAD_REQUEST:
                    Alert.showError("Invalid name or password !");
                    break;
                case INTERNAL_SERVER_ERROR:
                    Alert.showError("Got ERROR !");
                    break;
                case NOT_FOUND:
                default:
                    Alert.showError("SignUp Failed !");
                    break;
            }
        });

        map(MyRequest.INVITE_CHAT, (worker, data) -> {
            String friendName = data.get(MyDataKey.FRIEND_NAME);
            Alert.showInvite("Invite friend:\n" + friendName + " ?",
                    TransferHelper::responseInvite);
        });

        map(MyRequest.CONFIRM_CHAT, (worker, data) -> {
            String friendName = data.get(MyDataKey.FRIEND_NAME);
            Alert.showConfirm("Accept invitation:\n" + friendName + " ?",
                    TransferHelper::responseConfirm);
        });

        map(MyRequest.CHAT_INFO, (worker, data) -> {
            String friendName = data.get(MyDataKey.FRIEND_NAME);
            switch (data.getCode(DefaultStatusCode.class)) {
                case FORBIDDEN:
                    Alert.showError(friendName + "\ndeclined your invitation");
                    break;
                case NOT_FOUND:
                    Alert.showError(friendName + "\n offline");
                    break;
                case CREATED:
                    worker.complete(data);
                    data.get(MyDataKey.FRIEND_INFO, FriendInfo.class)
                            .ifPresent(info -> ClientContainer.frame.getCurrent()
                                    .changeTo(() -> ClientContainer.frame.showChat(info))
                            );
                    break;
            }
        });

        map(MyRequest.STOP_FIND, Worker::complete);

        map(MyRequest.SEND_MESSAGE, Worker::complete);

        map(MyRequest.RECEIVE_MESSAGE, (worker, data)
                -> data.get(MyDataKey.MESSAGE_BODY, Message.class)
                .ifPresent(ClientContainer.frame::appendReceive));

        map(MyRequest.FRIEND_INFO, (worker, data)
                -> data.get(MyDataKey.FRIEND_INFO, FriendInfo.class)
                .ifPresent(ClientContainer.frame::setFriendInfo));

        map(MyRequest.HISTORY_RECOVERY, (worker, data) -> {
            Optional<List<Message>> messages = data.get(MyDataKey.HISTORY_CHAT, new TypeToken<List<Message>>(){}.getType());
            messages.ifPresent(ClientContainer.frame::appendHistory);
        });

        map(MyRequest.SERVER_MESSAGE, (worker, data) -> {
            data.get(MyDataKey.MESSAGE_BODY, Message.class)
                    .ifPresent(message -> JOptionPane.showMessageDialog(
                            ClientContainer.frame,
                            message.getBody(),
                            "Alert",
                            JOptionPane.INFORMATION_MESSAGE
                    ));
        });

    }};

}
