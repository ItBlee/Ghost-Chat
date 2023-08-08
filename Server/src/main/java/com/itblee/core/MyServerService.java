package com.itblee.core;

import com.itblee.constant.Resource;
import com.itblee.core.helper.LoggerHelper;
import com.itblee.core.helper.TransferHelper;
import com.itblee.core.server.ServerService;
import com.itblee.exception.*;
import com.itblee.model.FriendInfo;
import com.itblee.model.Message;
import com.itblee.repository.document.Log;
import com.itblee.repository.document.UserDetail;
import com.itblee.security.HashApplier;
import com.itblee.security.Session;
import com.itblee.service.Impl.UserServiceImpl;
import com.itblee.service.UserService;
import com.itblee.transfer.DefaultStatusCode;
import com.itblee.transfer.Packet;
import com.itblee.utils.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MyServerService extends ServerService {

    private final UserService userService;

    private final UserManager userManager;

    private final ChatManager chatManager;

    private final Logger logger;

    public MyServerService(UserManager userManager, ChatManager chatManager, Logger logger) {
        super(userManager);
        this.logger = logger;
        this.userManager = userManager;
        this.chatManager = chatManager;
        userService = new UserServiceImpl();
    }

    public User requireAuthenticated(UUID uuid) throws UnauthenticatedException {
        User session = userManager.getUser(uuid)
                .orElseThrow(IllegalArgumentException::new);
        if (!userManager.authenticated(session.getUsername()))
            throw new UnauthenticatedException();
        return session;
    }

    @Override
    public void breakConnection(Worker worker) throws IOException {
        super.breakConnection(worker);
        Session session = userManager.get(worker.getUid())
                .orElseThrow(IllegalStateException::new);
        try {
            leaveChat(session.getUid());
            logout(session.getUid());
        } catch (UnauthenticatedException ignored) {}
        //LoggerHelper.log(session).breakConnect();
    }

    public DefaultStatusCode login(UUID uuid, String username, String password) {
        if (userManager.authenticated(username))
            return DefaultStatusCode.CONFLICT;
        try {
            UserDetail result = userService.login(username, password);
            User user = userManager.authenticate(uuid, result.getUsername());
            LoggerHelper.log(user).login();
            return DefaultStatusCode.OK;
        } catch (NotFoundException e) {
            return DefaultStatusCode.NOT_FOUND;
        } catch (ForbiddenException e) {
            return DefaultStatusCode.FORBIDDEN;
        } catch (BadRequestException e) {
            return DefaultStatusCode.BAD_REQUEST;
        }
    }

    public DefaultStatusCode register(UUID uuid, String username, String password) {
        if (userManager.authenticated(username))
            return DefaultStatusCode.CONFLICT;
        try {
            UserDetail result = userService.register(username, password);
            User user = userManager.authenticate(uuid, result.getUsername());
            LoggerHelper.log(user).register();
            return DefaultStatusCode.CREATED;
        } catch (BadRequestException e) {
            return DefaultStatusCode.BAD_REQUEST;
        } catch (UserExistException e) {
            return DefaultStatusCode.CONFLICT;
        } catch (Exception e) {
            return DefaultStatusCode.INTERNAL_SERVER_ERROR;
        }
    }

    public void logout(UUID uuid) throws UnauthenticatedException {
        User user = requireAuthenticated(uuid);
        if (!chatManager.quitQueue(user.getUid())) {
            try {
                leaveChat(user.getUid());
            } catch (UnauthenticatedException ignored) {}
        }
        LoggerHelper.log(user).logout();
        userManager.unauthenticate(user);
        TransferHelper.call(user).warnUnauthenticated();
    }

    public void stopFindChat(UUID userId) throws UnauthenticatedException {
        User user = requireAuthenticated(userId);
        user.offline();
        chatManager.quitQueue(user.getUid());
        LoggerHelper.log(user).stopFindChat();
    }

    public void findChatFor(UUID userId) throws UnauthenticatedException {
        User user = requireAuthenticated(userId);
        user.online();
        new Thread(() -> {
            try {
                findChatFor(user);
            } catch (InterruptedException ignored) {}
        }).start();
        LoggerHelper.log(user).startFindChat();
    }

    private void findChatFor(User user) throws InterruptedException {
        while (true) {
            Optional<UUID> found = chatManager.findFriendFor(user.getUid());

            if (!found.isPresent()) {
                chatManager.joinQueue(user.getUid());
                return;
            }

            Packet clientResp;

            final Worker userWorker = user.getWorker();
            Optional<User> friendOptional = userManager.getUser(found.get());
            if (!friendOptional.isPresent())
                break;
            User friend = friendOptional.get();
            TransferHelper.call(user).askForInvitation(friend.getUsername());

            final int requestTimeout = PropertyUtil.getInt("request.timeout");

            clientResp = userWorker.await(requestTimeout);
            if (!user.isOnline())
                return;

            boolean isAcceptInvite = clientResp.is(DefaultStatusCode.OK);
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
            boolean isConfirmInvite = clientResp.is(DefaultStatusCode.OK);
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
        User user = requireAuthenticated(userId);
        ChatRoom room = user.getRoom();
        Collection<User> members = room.getMembers();
        int count = 0;
        for (User member : members) {
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
        User user = requireAuthenticated(userId);
        user.offline();
        Optional<ChatRoom> room = chatManager.leaveRoom(user);
        if (!room.isPresent())
            return;
        FriendInfo info = new FriendInfo();
        info.setName(user.getUsername());
        info.setStatus(user.getStatus().toString());
        for (User member : room.get().getMembers()) {
            TransferHelper.call(member).modifyFriendInfo(info);
            LoggerHelper.log(user).leaveChat(member.getUid(), member.getUsername());
        }
    }

    @Override
    public void removeSession(Session session) {
        try {
            logout(session.getUid());
        } catch (UnauthenticatedException ignored) {}
        super.removeSession(session);
        logger.record(session);
    }

    public void recordAllLogs() {
        userManager.getSessions().forEach(logger::record);
        userManager.clear();
    }

    public Optional<User> searchByName(String username) {
        UserDetail found = userService.findByUsername(username).orElse(null);
        if (found == null)
            return Optional.empty();
        User user = new User(new Session());
        Converter.convertUser(found, user);
        userManager.getUser(username).ifPresent(u -> {
            user.setStatus(u.getStatus());
            user.setSession(u.getSession());
        });
        return Optional.of(user);
    }

    public List<User> getUsers() {
        Collection<UserDetail> userDetails = userService.findAllWithLimitFields();
        return userDetails.stream()
                .map(userDetail -> Converter.convertUser(userDetail, new User(new Session())))
                .map(user -> {
                    User u = userManager.getUser(user.getUsername())
                            .orElse(null);
                    if (u == null)
                        return user;
                    u.setCreatedDate(user.getCreatedDate());
                    return u;
                })
                .collect(Collectors.toList());
    }

    public void banUser(User user) throws NotFoundException {
        userService.banUser(user.getUsername());
        try {
            sendAlert(user.getSession(), "U got banned !");
        } catch (Exception ignored) {}
        try {
            logout(user.getUid());
        } catch (UnauthenticatedException ignored) {}
    }

    public void activeUser(String username) throws NotFoundException {
        userService.activeUser(username);
    }

    public void changePassword(String username, String password) throws NotFoundException {
        String salt = HashApplier.getSalt();
        String hash = HashApplier.applySha256(password, salt);
        userService.changePassword(username, hash, salt);
        userManager.getUser(username).ifPresent(user -> {
            try {
                logout(user.getUid());
            } catch (UnauthenticatedException ignored) {}
        });
    }

    public void resetPassword(String username) throws NotFoundException {
        userService.resetPassword(username);
        userManager.getUser(username).ifPresent(user -> {
            try {
                logout(user.getUid());
            } catch (UnauthenticatedException ignored) {}
        });
    }

    public void sendAlert(Session session, String message) {
        Message msg = new Message();
        msg.setBody(message);
        msg.setSentDate(LocalDateTime.now().toString());
        TransferHelper.call(session).serverMessage(msg);
        LoggerHelper.log(session).messageFromServer(message);
    }

    public void stopAllUser() {
        for (User user : userManager.getAuthenticated()) {
            stopUser(user);
        }
    }

    public void stopUser(User user) {
        try {
            logout(user.getUid());
        } catch (UnauthenticatedException ignored) {}
    }

}
