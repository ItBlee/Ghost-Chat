package service.Impl;

import DAO.Impl.UserDAOImpl;
import DAO.UserDAO;
import com.mongodb.client.model.Filters;
import entity.User;
import exception.*;
import org.bson.types.ObjectId;
import service.UserService;
import utils.SecurityUtil;
import utils.ValidationUtils;

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
            throw new NotFoundException("User not found !");
        User user = results.iterator().next();
        if (!SecurityUtil.applySha256(password, user.getPasswordSalt()).equals(user.getPasswordHash()))
            throw new InvalidPasswordException("Wrong user password !");
        return user;
    }

    @Override
    public User register(String username, String password) throws ChatAppException {
        if (isExistUsername(username))
            throw new UserExistException("User already exist in database !");
        if (!ValidationUtils.isValidUsername(username))
            throw new InvalidUsernameException("Invalid username !");
        if (!ValidationUtils.isValidPassword(password))
            throw new InvalidPasswordException("Invalid password !");
        String salt = SecurityUtil.getSalt();
        String hash = SecurityUtil.applySha256(password, salt);
        User user = new User();
        user.setId(new ObjectId());
        user.setUsername(username);
        user.setPasswordHash(hash);
        user.setPasswordSalt(salt);
        user.setStatus(Boolean.TRUE);
        String newId = dao.insertOne(user);
        User inserted = dao.findById(newId);
        if (inserted == null)
            throw new QueryException("Insert new user failed !");
        return inserted;
    }

    @Override
    public boolean isExistUsername(String username) {
        Iterable<User> results = dao.findByCondition(Filters.eq("username", username), 1);
        return results.iterator().hasNext();
    }
}
