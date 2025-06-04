package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.server.dao.UserDao;
import cn.blazeh.achat.server.model.User;

import java.util.Optional;

public enum UserManager {

    INSTANCE;

    public boolean check(String userId, String password) {
        return userId != null && UserDao.findUser(userId)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    public boolean add(String userId, String password) {
        return checkUserId(userId) && UserDao.checkAndInsertUser(userId, password);
    }

    public boolean checkUserId(String userId) {
        return userId != null && !userId.isEmpty();
    }

    public boolean hasRegistered(String userId) {
        return UserDao.findUser(userId).isPresent();
    }

    public Optional<User> findUser(String userId) {
        return UserDao.findUser(userId);
    }

    public void save(User user) {
        if(user == null)
            return;
        UserDao.updateUser(user);
    }

}
