package com.itblee.core;

import com.itblee.constant.Resource;
import com.itblee.core.Impl.UserSession;
import com.itblee.exception.UnauthenticatedException;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.security.Certificate;
import com.itblee.transfer.Packet;
import com.itblee.transfer.StatusCode;
import com.itblee.utils.ArrayUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.itblee.constant.ServerConstant.NAME_LIMIT;
import static com.itblee.constant.ServerConstant.REQUEST_TIMEOUT;

public class ServerService {

    public static void registerSession(Worker worker, UUID uuid, String secretKey) throws IOException {
        UserSession session = ServerHelper.getSession(uuid)
                .orElse(ServerService.createSession(worker, secretKey));
        session.renew();
        TransferHelper.call(worker).responseAuthRegister(session.getUid());
        worker.close();
    }

    public static UserSession createSession(Worker worker, String secretKey) {
        Certificate certificate = new Certificate(UUID.randomUUID(), secretKey);
        UserSession session = new UserSession(certificate);
        session.setWorker(worker);
        ServerHelper.addSession(session);
        worker.setUid(certificate.getUid());
        return session;
    }

    public static UserSession requireSession(Worker worker, UUID uuid) throws IOException {
        Optional<UserSession> session = ServerHelper.getSession(uuid);
        if (worker.getUid() != uuid
                || !session.isPresent()
                || !ServerHelper.isActiveSession(session.get())) {
            TransferHelper.call(worker).warnUnauthenticated();
            worker.close();
            throw new UnauthenticatedException();
        }
        return session.get();
    }

    public static void registerUsername(Worker worker, String username) throws IOException {
        boolean isAccept = ServerService.checkUsername(username);
        TransferHelper.call(worker).responseNameRegister(isAccept);
    }

    public static boolean checkUsername(String username) {
        return ServerHelper.containName(username) && username.length() <= NAME_LIMIT;
    }

    public static void breakConnection(Worker worker) throws IOException {
        worker.close();
        ServerHelper.removeSession(worker.getUid());
    }

    public static void makeUserOffline(UUID userId) throws InterruptedException, IOException {
        UserSession user = ServerHelper.getSession(userId)
                .orElseThrow(UnauthenticatedException::new);
        if (!user.isOnline())
            return;
        user.offline();
        ServerHelper.removeFromQueue(user.getUid());
        Worker userWorker = user.getWorker();
        Packet response = TransferHelper.warnStopFindChat();
        userWorker.complete(response);
        TransferHelper.call(userWorker).send(response);
    }

    public static void makeUserOnline(UUID userId) {
        UserSession user = ServerHelper.getSession(userId)
                .orElseThrow(UnauthenticatedException::new);
        if (user.isOnline())
            return;
        user.online();
        new Thread(() -> {
            try {
                ServerService.findChatFor(user);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void findChatFor(UserSession user) throws InterruptedException, IOException {
        ChatManager chatManager = ServerHelper.getChatManager();
        while (true) {
            Optional<UserSession> found = chatManager.findFriendFor(user.getUid());

            if (!found.isPresent()) {
                chatManager.joinQueue(user.getUid());
                return;
            }

            Packet clientResp;

            final Worker userWorker = user.getWorker();
            UserSession friend = found.get();
            TransferHelper.call(userWorker).askForInvitation(friend.getUsername());

            clientResp = userWorker.await(REQUEST_TIMEOUT);
            if (!user.isOnline())
                return;

            boolean isAcceptInvite = clientResp.is(StatusCode.OK);
            if (!isAcceptInvite) {
                chatManager.reject(user.getUid(), friend.getUid());
                continue;
            }

            if (!friend.isOnline()) {
                TransferHelper.call(userWorker).responseFriendOffline(friend.getUsername());
                continue;
            }
            final Worker friendWorker = friend.getWorker();
            TransferHelper.call(friendWorker).askForConfirmation(user.getUsername());

            clientResp = friendWorker.await(REQUEST_TIMEOUT);

            if (!user.isOnline()) {
                TransferHelper.call(friendWorker).responseFriendOffline(user.getUsername());
                return;
            }
            boolean isConfirmInvite = clientResp.is(StatusCode.OK);
            if (!isConfirmInvite) {
                TransferHelper.call(userWorker).responseInvitationDeclined(friend.getUsername());
                chatManager.reject(friend.getUid(), user.getUid());
                continue;
            }

            ChatRoom room = new ChatRoom();
            room.add(user);
            room.add(friend);
            if (!chatManager.registerRoom(room)) {
                if (!user.isOnline() && !friend.isOnline())
                    return;
                if (!user.isOnline()) {
                    TransferHelper.call(friendWorker).responseFriendOffline(user.getUsername());
                    return;
                }
                if (!friend.isOnline()) {
                    TransferHelper.call(userWorker).responseFriendOffline(friend.getUsername());
                    continue;
                }
            }
            FriendInfo info = new FriendInfo();
            info.setName(friend.getUsername());
            info.setStatus(friend.getStatus().toString());
            info.setAvatar(ArrayUtil.getRandom(Resource.AVATARS_ENCODE));
            TransferHelper.call(userWorker).responseCreatedChat(info);

            info.setName(user.getUsername());
            info.setStatus(user.getStatus().toString());
            info.setAvatar(ArrayUtil.getRandom(Resource.AVATARS_ENCODE));
            TransferHelper.call(friendWorker).responseCreatedChat(info);
            return;
        }
    }

    public static void syncMessage(UUID senderId, Message message) throws IOException {
        UserSession sender = ServerHelper.getSession(senderId)
                .orElseThrow(UnauthenticatedException::new);
        ChatRoom room = sender.getRoom();
        Collection<UserSession> members = room.getMembers();
        members.remove(sender);
        for (UserSession member : members) {
            TransferHelper.call(member.getWorker()).sendChatMessage(message);
        }
        TransferHelper.call(sender.getWorker()).responseCreatedMessage();
    }

    public static void leaveChat(UUID userId) throws IOException {
        UserSession user = ServerHelper.getSession(userId)
                .orElseThrow(UnauthenticatedException::new);
        ChatManager chatManager = ServerHelper.getChatManager();
        Optional<ChatRoom> room = chatManager.leaveRoom(user);
        user.offline();
        if (room.isPresent()) {
            FriendInfo info = new FriendInfo();
            info.setName(user.getUsername());
            info.setStatus(user.getStatus().toString());
            for (UserSession member : room.get().getMembers()) {
                TransferHelper.call(member.getWorker()).modifyFriendInfo(info);
            }
        }
    }

}
