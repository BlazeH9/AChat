package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.server.dao.UserDao;

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

}
