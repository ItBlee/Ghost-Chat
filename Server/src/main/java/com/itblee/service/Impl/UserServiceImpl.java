package com.itblee.service.Impl;

import com.itblee.DAO.Impl.UserDAOImpl;
import com.itblee.DAO.UserDAO;
import com.itblee.exception.*;
import com.mongodb.client.model.Filters;
import com.itblee.DAO.entity.User;
import exception.*;
import org.bson.types.ObjectId;
import com.itblee.service.UserService;
import com.itblee.security.EncryptUtil;
import com.itblee.utils.ObjectUtil;

public class UserServiceImpl implements UserService {
    private final UserDAO dao;

    public UserServiceImpl() {
        dao = new UserDAOImpl();
    }

    @Override
    public User findById(String id) {
        return dao.findById(id);
    }

    @Override
    public User login(String username, String password) throws ChatAppException {
        Iterable<User> results = dao.findByCondition(Filters.eq("username", username), 1);
        if (!results.iterator().hasNext())
            throw new NotFoundException("The username is not registered !");
        User user = results.iterator().next();
        if (!EncryptUtil.applySha256(password, user.getPasswordSalt()).equals(user.getPasswordHash()))
            throw new InvalidPasswordException("Wrong user password !");
        if (!user.getStatus())
            throw new InvalidStatusException("User is banned !");
        return user;
    }

    @Override
    public User register(String username, String password) throws ChatAppException {
        if (isExistUsername(username))
            throw new UserExistException("Username already exist !");
        if (!ObjectUtil.isValidUsername(username))
            throw new InvalidUsernameException("Invalid username !");
        if (!ObjectUtil.isValidPassword(password))
            throw new InvalidPasswordException("Invalid password !");
        String salt = EncryptUtil.getSalt();
        String hash = EncryptUtil.applySha256(password, salt);
        User user = new User();
        user.setId(new ObjectId());
        user.setUsername(username);
        user.setPasswordHash(hash);
        user.setPasswordSalt(salt);
        user.setStatus(Boolean.TRUE);
        String newId = dao.insertOne(user);
        User inserted = dao.findById(newId);
        if (inserted == null)
            throw new QueryException("Register user failed !");
        return inserted;
    }

    @Override
    public boolean isExistUsername(String username) {
        Iterable<User> results = dao.findByCondition(Filters.eq("username", username), 1);
        return results.iterator().hasNext();
    }
}
