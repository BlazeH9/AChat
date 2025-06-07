package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.server.dao.UserDao;
import cn.blazeh.achat.server.model.User;

import java.util.Optional;
import java.util.regex.Pattern;

public class UserManager {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    private final UserDao userDao;

    public UserManager(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean check(String userId, String password) {
        return userId != null && userDao.findUser(userId)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    public boolean add(String userId, String password) {
        return checkUserId(userId) && userDao.checkAndInsertUser(userId, password);
    }

    public boolean checkUserId(String userId) {
        if(userId == null || userId.isEmpty())
            return false;
        return USER_ID_PATTERN.matcher(userId).matches();
    }

    public boolean hasRegistered(String userId) {
        return userDao.findUser(userId).isPresent();
    }

    public Optional<User> findUser(String userId) {
        return userDao.findUser(userId);
    }

    public void save(User user) {
        if(user == null)
            return;
        userDao.updateUser(user);
    }

}
