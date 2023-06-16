package com.itblee.service;

import com.itblee.DAO.entity.User;
import com.itblee.exception.ChatAppException;

public interface UserService {
    User findById(String id);
    User login(String username, String password) throws ChatAppException;
    User register(String username, String password) throws ChatAppException;
    boolean isExistUsername(String username);
}
