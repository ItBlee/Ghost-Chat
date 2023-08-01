package com.itblee.core;

import com.google.gson.reflect.TypeToken;
import com.itblee.core.Impl.SecureWorker;
import com.itblee.core.helper.ClientHelper;
import com.itblee.core.helper.TransferHelper;
import com.itblee.gui.Alert;
import com.itblee.gui.ClientFrame;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Request;
import com.itblee.transfer.StatusCode;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public final class RequestMapping {

    static final Controller CONNECT = new Controller() {{
        ClientFrame frame = ClientHelper.getFrame();

        map(Request.SESSION, (worker, data) -> {
            switch (data.getCode()) {
                case TIMEOUT:
                    ClientHelper.verify();
                case CONFLICT:
                case FORBIDDEN:
                    ClientHelper.verify();
                    ((SecureWorker) worker).resend();
                    break;
            }
        });

        map(Request.SESSION_KEY, (worker, data) -> {
            if (data.is(StatusCode.BAD_REQUEST)) {
                ClientHelper.getUser()
                        .getCertificate()
                        .setSecretKey(null);
                ClientHelper.verify();
                ((SecureWorker) worker).resend();
            }
        });

        map(Request.UNAUTHENTICATED, (worker, data) -> {
            Client.getInstance().toAnonymous();
            frame.getCurrent().changeTo(frame::showLogin);
        });

        map(Request.AUTH_LOGIN, (worker, data) -> {
            worker.complete(data);
            switch (data.getCode()) {
                case OK:
                    ClientHelper.getUser().setUsername(data.get(DataKey.USERNAME));
                    frame.getCurrent().changeTo(frame::showHome);
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

        map(Request.AUTH_REGISTER, (worker, data) -> {
            worker.complete(data);
            switch (data.getCode()) {
                case CREATED:
                    ClientHelper.getUser().setUsername(data.get(DataKey.USERNAME));
                    frame.getCurrent().changeTo(frame::showHome);
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

        map(Request.INVITE_CHAT, (worker, data) -> {
            String friendName = data.get(DataKey.FRIEND_NAME);
            Alert.showInvite("Invite friend:\n" + friendName + " ?",
                    TransferHelper::responseInvite);
        });

        map(Request.CONFIRM_CHAT, (worker, data) -> {
            String friendName = data.get(DataKey.FRIEND_NAME);
            Alert.showConfirm("Accept invitation:\n" + friendName + " ?",
                    TransferHelper::responseConfirm);
        });

        map(Request.CHAT_INFO, (worker, data) -> {
            String friendName = data.get(DataKey.FRIEND_NAME);
            switch (data.getCode()) {
                case FORBIDDEN:
                    Alert.showError(friendName + "\ndeclined your invitation");
                    break;
                case NOT_FOUND:
                    Alert.showError(friendName + "\n offline");
                    break;
                case CREATED:
                    worker.complete(data);
                    data.get(DataKey.FRIEND_INFO, FriendInfo.class)
                            .ifPresent(info -> frame.getCurrent()
                                    .changeTo(() -> frame.showChat(info))
                            );
                    break;
            }
        });

        map(Request.STOP_FIND, Worker::complete);

        map(Request.SEND_MESSAGE, Worker::complete);

        map(Request.RECEIVE_MESSAGE, (worker, data)
                -> data.get(DataKey.MESSAGE_BODY, Message.class)
                .ifPresent(frame::appendReceive));

        map(Request.FRIEND_INFO, (worker, data)
                -> data.get(DataKey.FRIEND_INFO, FriendInfo.class)
                .ifPresent(frame::setFriendInfo));

        map(Request.HISTORY_RECOVERY, (worker, data) -> {
            Optional<List<Message>> messages = data.get(DataKey.HISTORY_CHAT, new TypeToken<List<Message>>(){}.getType());
            messages.ifPresent(frame::appendHistory);
        });

        map(Request.UNAUTHENTICATED, (worker, data) -> {
            Client.getInstance().toAnonymous();
            frame.showLogin();
        });

        map(Request.SERVER_MESSAGE, (worker, data) -> {
            data.get(DataKey.MESSAGE_BODY, Message.class)
                    .ifPresent(message -> JOptionPane.showMessageDialog(
                            frame,
                            message.getBody(),
                            "Alert",
                            JOptionPane.INFORMATION_MESSAGE
                    ));
        });

        lock();
    }};

    static final Controller SSL = new Controller() {{
        map(Request.SESSION, (worker, data) -> {
            switch (data.getCode()) {
                case CREATED:
                    String uid = data.get(DataKey.SESSION_ID);
                    ClientHelper.getUser().setUid(uid);
                    worker.complete(data);
                    worker.close();
                    break;
                case BAD_REQUEST:
                    worker.complete(data);
                    break;
            }
        });

        map(Request.SESSION_KEY, (worker, data) -> {
            switch (data.getCode()) {
                case OK:
                    worker.complete(data);
                    break;
                case BAD_REQUEST:
                    ClientHelper.getUser()
                            .getCertificate()
                            .setSecretKey(null);
                    break;
            }
        });

        lock();
    }};

}
