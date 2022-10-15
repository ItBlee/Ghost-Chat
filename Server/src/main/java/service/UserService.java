package service;

import entity.User;
import exception.*;

public interface UserService {
    User findById(String id);
    User login(String username, String password) throws ChatAppException;
    User register(String username, String password) throws ChatAppException;
    boolean isExistUsername(String username);
}
