package DAO.Impl;

import DAO.UserDAO;
import entity.User;

public class UserDAOImpl extends MongoDAO<User> implements UserDAO {

    public UserDAOImpl() {
        super("user");
    }
}
