package com.itblee.core;

import com.itblee.constant.Resource;
import com.itblee.core.Impl.UserSession;
import com.itblee.core.helper.LoggerHelper;
import com.itblee.core.helper.ServerHelper;
import com.itblee.core.helper.TransferHelper;
import com.itblee.exception.*;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.repository.document.Log;
import com.itblee.repository.document.UserDetail;
import com.itblee.security.Certificate;
import com.itblee.security.HashUtil;
import com.itblee.service.Impl.UserServiceImpl;
import com.itblee.service.UserService;
import com.itblee.transfer.Packet;
import com.itblee.transfer.StatusCode;
import com.itblee.utils.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServerService {

    private final UserService userService = new UserServiceImpl();

    public UserSession requireSession(Worker worker, UUID uuid) throws UnverifiedException {
        Optional<UserSession> result = ServerHelper.getSession(uuid);
        if (!result.isPresent()) {
            //TransferHelper.call(worker).warnSession(StatusCode.FORBIDDEN);
            throw new UnverifiedException();
        }
        UserSession session = result.get();
        if (session.getWorker() != null
                && session.getWorker() != worker
                && session.getWorker().isAlive()) {
            //TransferHelper.call(worker).warnSession(StatusCode.CONFLICT);
            throw new UnverifiedException();
        }
        worker.setUid(session.getUid());
        session.setWorker(worker);
        session.resetTimer();
        return session;
    }

    public UserSession requireAuthenticated(UUID uuid) throws UnauthenticatedException {
        UserSession session = ServerHelper.getSession(uuid)
                .orElseThrow(IllegalArgumentException::new);
        if (!ServerHelper.getUserManager().authenticated(session.getUsername()))
            throw new UnauthenticatedException();
        return session;
    }

    public UserSession createSession(String secretKey) {
        if (StringUtil.isBlank(secretKey))
            throw new IllegalArgumentException();
        Certificate certificate = new Certificate(UUID.randomUUID(), secretKey);
        UserSession session = new UserSession();
        session.setCertificate(certificate);
        ServerHelper.addSession(session);
        session.resetTimer();
        return session;
    }

    public boolean changeSessionKey(UUID uuid, String secretKey) {
        ObjectUtil.requireNonNull(uuid);
        if (StringUtil.isBlank(secretKey))
            return false;
        Optional<UserSession> result = ServerHelper.getSession(uuid);
        if (!result.isPresent())
            return false;
        UserSession session = result.get();
        session.getCertificate().setSecretKey(secretKey);
        return true;
    }

    public void breakConnection(Worker worker) throws IOException {
        worker.close();
        UserSession session = ServerHelper.getSession(worker.getUid())
                .orElseThrow(IllegalStateException::new);
        try {
            leaveChat(session.getUid());
            logout(session.getUid());
        } catch (UnauthenticatedException ignored) {}
        LoggerHelper.log(session).breakConnect();
    }

    public StatusCode login(UUID uuid, String username, String password) {
        UserManager userManager = ServerHelper.getUserManager();
        if (userManager.authenticated(username))
            return StatusCode.CONFLICT;
        try {
            UserDetail result = userService.login(username, password);
            UserSession user = userManager.authenticate(uuid, result.getUsername());
            LoggerHelper.log(user).login();
            return StatusCode.OK;
        } catch (NotFoundException e) {
            return StatusCode.NOT_FOUND;
        } catch (ForbiddenException e) {
            return StatusCode.FORBIDDEN;
        } catch (BadRequestException e) {
            return StatusCode.BAD_REQUEST;
        }
    }

    public StatusCode register(UUID uuid, String username, String password) {
        UserManager userManager = ServerHelper.getUserManager();
        if (userManager.authenticated(username))
            return StatusCode.CONFLICT;
        try {
            UserDetail result = userService.register(username, password);
            UserSession user = userManager.authenticate(uuid, result.getUsername());
            LoggerHelper.log(user).register();
            return StatusCode.CREATED;
        } catch (BadRequestException e) {
            return StatusCode.BAD_REQUEST;
        } catch (UserExistException e) {
            return StatusCode.CONFLICT;
        } catch (Exception e) {
            return StatusCode.INTERNAL_SERVER_ERROR;
        }
    }

    public void logout(UUID uuid) throws UnauthenticatedException {
        UserSession user = requireAuthenticated(uuid);
        if (!ServerHelper.removeFromQueue(user.getUid())) {
            try {
                leaveChat(user.getUid());
            } catch (UnauthenticatedException ignored) {}
        }
        LoggerHelper.log(user).logout();
        ServerHelper.getUserManager().unauthenticate(uuid);
        TransferHelper.call(user).warnUnauthenticated();
    }

    public void stopFindChat(UUID userId) throws UnauthenticatedException {
        UserSession user = requireAuthenticated(userId);
        user.offline();
        ServerHelper.removeFromQueue(user.getUid());
        LoggerHelper.log(user).stopFindChat();
    }

    public void findChatFor(UUID userId) throws UnauthenticatedException {
        UserSession user = requireAuthenticated(userId);
        user.online();
        new Thread(() -> {
            try {
                findChatFor(user);
            } catch (InterruptedException ignored) {}
        }).start();
        LoggerHelper.log(user).startFindChat();
    }

    private void findChatFor(UserSession user) throws InterruptedException {
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
            TransferHelper.call(user).askForInvitation(friend.getUsername());

            final int requestTimeout = PropertyUtil.getInt("request.timeout");

            clientResp = userWorker.await(requestTimeout);
            if (!user.isOnline())
                return;

            boolean isAcceptInvite = clientResp.is(StatusCode.OK);
            if (!isAcceptInvite) {
                chatManager.reject(user.getUid(), friend.getUid());
                continue;
            }

            if (!friend.isOnline()) {
                TransferHelper.call(user).responseFriendOffline(friend.getUsername());
                continue;
            }
            final Worker friendWorker = friend.getWorker();
            TransferHelper.call(friend).askForConfirmation(user.getUsername());

            clientResp = friendWorker.await(requestTimeout);

            if (!user.isOnline()) {
                TransferHelper.call(friend).responseFriendOffline(user.getUsername());
                return;
            }
            boolean isConfirmInvite = clientResp.is(StatusCode.OK);
            if (!isConfirmInvite) {
                TransferHelper.call(user).responseInvitationDeclined(friend.getUsername());
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
                    TransferHelper.call(friend).responseFriendOffline(user.getUsername());
                    return;
                }
                if (!friend.isOnline()) {
                    TransferHelper.call(user).responseFriendOffline(friend.getUsername());
                    continue;
                }
            }

            Logger logger = ServerHelper.getLogger();
            Collection<Log> chatLogs = logger.getUserHistoryChat(user.getUsername(), friend.getUsername());
            List<Message> messages = Converter.extractHistoryChat(chatLogs);

            String avt1 = ArrayUtil.getRandom(Resource.AVATARS_ENCODE);
            String avt2 = ArrayUtil.getRandom(Resource.AVATARS_ENCODE);
            while (avt1.equals(avt2)) {
                avt2 = ArrayUtil.getRandom(Resource.AVATARS_ENCODE);
            }

            FriendInfo info = new FriendInfo();
            info.setName(friend.getUsername());
            info.setStatus(friend.getStatus().toString());
            info.setAvatar(avt1);
            TransferHelper.call(user).recoveryHistoryChat(messages);
            TransferHelper.call(user).responseCreatedChat(info);
            LoggerHelper.log(user).matchFriend(friend.getUid(), friend.getUsername());

            info.setName(user.getUsername());
            info.setStatus(user.getStatus().toString());
            info.setAvatar(avt2);
            TransferHelper.call(friend).recoveryHistoryChat(messages);
            TransferHelper.call(friend).responseCreatedChat(info);
            LoggerHelper.log(friend).matchFriend(user.getUid(), user.getUsername());
            return;
        }
    }

    public void syncMessage(UUID userId, Message message) throws UnauthenticatedException {
        UserSession user = requireAuthenticated(userId);
        ChatRoom room = user.getRoom();
        Collection<UserSession> members = room.getMembers();
        int count = 0;
        for (UserSession member : members) {
            if (member == user)
                continue;
            TransferHelper.call(member).sendChatMessage(message);
            LoggerHelper.log(user).sendMessage(member.getUsername(), message.getBody());
            LoggerHelper.log(member).receiveMessage(user.getUsername(), message.getBody());
            count++;
        }
        boolean isSuccess = count > 0;
        TransferHelper.call(user).responseSendMessageStatus(isSuccess);
    }

    public void leaveChat(UUID userId) throws UnauthenticatedException {
        UserSession user = requireAuthenticated(userId);
        user.offline();
        Optional<ChatRoom> room = ServerHelper.getChatManager().leaveRoom(user);
        if (!room.isPresent())
            return;
        FriendInfo info = new FriendInfo();
        info.setName(user.getUsername());
        info.setStatus(user.getStatus().toString());
        for (UserSession member : room.get().getMembers()) {
            TransferHelper.call(member).modifyFriendInfo(info);
            LoggerHelper.log(user).leaveChat(member.getUid(), member.getUsername());
        }
    }

    public void removeSession(UserSession session) {
        try {
            logout(session.getUid());
        } catch (UnauthenticatedException ignored) {}
        Worker worker = session.getWorker();
        if (worker.isAlive()) {
            //TransferHelper.call(worker).warnSession(StatusCode.TIMEOUT);
            try {
                worker.close();
            } catch (IOException ignored) {}
        }
        ServerHelper.getUserManager().remove(session);
    }

    public Optional<UserSession> searchByName(String username) {
        UserDetail found = userService.findByUsername(username).orElse(null);
        if (found == null)
            return Optional.empty();
        UserSession session = ServerHelper.getUser(username).orElse(new UserSession());
        return Optional.of(Converter.convertUser(found, session));
    }

    public List<UserSession> getUsers() {
        Collection<UserDetail> userDetails = userService.findAllWithLimitFields();
        return userDetails.stream()
                .map(userDetail -> Converter.convertUser(userDetail, new UserSession()))
                .map(user -> {
                    UserSession session = ServerHelper.getUser(user.getUsername()).orElse(null);
                    if (session == null)
                        return user;
                    session.setStatus(user.getStatus());
                    return session;
                })
                .collect(Collectors.toList());
    }

    public void banUser(UserSession user) throws NotFoundException {
        userService.banUser(user.getUsername());
        try {
            sendAlert(user, "U got banned !");
        } catch (Exception ignored) {}
        try {
            logout(user.getUid());
        } catch (UnauthenticatedException ignored) {}
    }

    public void activeUser(String username) throws NotFoundException {
        userService.activeUser(username);
    }

    public void changePassword(String username, String password) throws NotFoundException {
        String salt = HashUtil.getSalt();
        String hash = HashUtil.applySha256(password, salt);
        userService.changePassword(username, hash, salt);
        ServerHelper.getUser(username).ifPresent(user -> {
            try {
                logout(user.getUid());
            } catch (UnauthenticatedException ignored) {}
        });
    }

    public void resetPassword(String username) throws NotFoundException {
        userService.resetPassword(username);
        ServerHelper.getUser(username).ifPresent(user -> {
            try {
                logout(user.getUid());
            } catch (UnauthenticatedException ignored) {}
        });
    }

    public void sendAlert(UserSession user, String message) throws IOException {
        Message msg = new Message();
        msg.setBody(message);
        msg.setSentDate(LocalDateTime.now().toString());
        TransferHelper.call(user).serverMessage(msg);
        LoggerHelper.log(user).messageFromServer(message);
    }

    public void stopAllUser() {
        for (UserSession user : ServerHelper.getUserManager().getAuthenticated()) {
            stopUser(user);
        }
    }

    public void stopUser(UserSession user) {
        try {
            logout(user.getUid());
        } catch (UnauthenticatedException ignored) {}
    }

    public void renewSessionTimer(UserSession user) {
        user.resetTimer();
    }

}
