package com.itblee.service.Impl;

import com.itblee.exception.*;
import com.itblee.repository.Impl.UserRepositoryImpl;
import com.itblee.repository.UserRepository;
import com.itblee.repository.document.UserDetail;
import com.itblee.security.HashUtil;
import com.itblee.service.UserService;
import com.itblee.utils.ValidateUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

public class UserServiceImpl implements UserService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final UserRepository userRepository = new UserRepositoryImpl();

    @Override
    public List<UserDetail> findAllWithLimitFields() {
        String[] fields = {"username", "status", "createdDate", "modifiedDate"};
        FindIterable<UserDetail> results = userRepository.findAll(fields);
        return results.into(new ArrayList<>());
    }

    @Override
    public Optional<UserDetail> findByUsername(String username) {
        Bson filter = Filters.eq("username", username);
        int limit = 1;
        FindIterable<UserDetail> results = userRepository.findByCondition(filter, limit);
        return Optional.ofNullable(results.first());
    }

    @Override
    public UserDetail login(String username, String password) throws NotFoundException, ForbiddenException, BadRequestException {
        UserDetail userDetail = findByUsername(username).orElse(null);
        if (userDetail == null)
            throw new NotFoundException();
        if (userDetail.getStatus() == 0)
            throw new ForbiddenException();
        String hash = HashUtil.applySha256(password, userDetail.getPasswordSalt());
        if (!hash.equals(userDetail.getPasswordHash()))
            throw new BadRequestException();
        return userDetail;
    }

    @Override
    public UserDetail register(String username, String password) throws BadRequestException, UserExistException {
        if (!ValidateUtil.isValidUsername(username))
            throw new InvalidUsernameException("Invalid username !");
        if (!ValidateUtil.isValidPassword(password))
            throw new InvalidPasswordException("Invalid password !");
        if (findByUsername(username).isPresent())
            throw new UserExistException("Username already exist !");
        String salt = HashUtil.getSalt();
        String hash = HashUtil.applySha256(password, salt);
        UserDetail userDetail = new UserDetail();
        userDetail.setId(new ObjectId());
        userDetail.setUsername(username);
        userDetail.setPasswordHash(hash);
        userDetail.setPasswordSalt(salt);
        userDetail.setStatus(1);
        Optional<ObjectId> newId = userRepository.insertOne(userDetail);
        return newId.flatMap(userRepository::findById)
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public void changePassword(String username, String hash, String salt) throws NotFoundException {
        UserDetail userDetail = findByUsername(username)
                .orElseThrow(() -> new NotFoundException("The username is not registered !"));
        List<Bson> updates = new ArrayList<>();
        updates.add(Updates.set("passwordHash", hash));
        updates.add(Updates.set("passwordSalt", salt));
        userRepository.updateOne(userDetail.getId(), updates);
    }

    @Override
    public void resetPassword(String username) throws NotFoundException {
        String salt = HashUtil.getSalt();
        String hash = HashUtil.applySha256(DEFAULT_PASSWORD, salt);
        changePassword(username, hash, salt);
    }

    @Override
    public void banUser(String username) throws NotFoundException {
        changeStatus(username, 0);
    }

    @Override
    public void activeUser(String username) throws NotFoundException {
        changeStatus(username, 1);
    }

    @Override
    public void changeStatus(String username, int status) throws NotFoundException {
        UserDetail userDetail = findByUsername(username)
                .orElseThrow(() -> new NotFoundException("The username is not registered !"));
        List<Bson> updates = new ArrayList<>();
        updates.add(Updates.set("status", status));
        userRepository.updateOne(userDetail.getId(), updates);
    }

}
