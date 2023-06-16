package com.itblee.DAO.Impl;

import com.itblee.DAO.UserDAO;
import com.itblee.DAO.entity.User;

public class UserDAOImpl extends MongoDAO<User> implements UserDAO {

    public UserDAOImpl() {
        super("user");
    }
}
