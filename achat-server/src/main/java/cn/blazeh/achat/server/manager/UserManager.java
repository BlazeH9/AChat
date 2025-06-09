package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.server.dao.UserDao;
import cn.blazeh.achat.server.model.User;
import cn.blazeh.achat.server.util.DigestUtils;

import java.util.Optional;
import java.util.regex.Pattern;

public class UserManager {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    private final UserDao userDao;

    public UserManager(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean check(String userId, String password) {
        return userId != null && userDao.selectUser(userId)
                .map(user -> DigestUtils.hashWithSalt(password, user.getSalt()).equals(user.getPassword()))
                .orElse(false);
    }

    public boolean add(String userId, String password) {
        if(!checkUserId(userId))
            return false;
        return userDao.selectUser(userId)
                .map(ignored -> false)
                .orElseGet(() -> {
                    String salt = DigestUtils.generateSalt();
                    return userDao.insertUser(new User(userId, DigestUtils.hashWithSalt(password, salt), salt));
                });
    }

    public boolean checkUserId(String userId) {
        if(userId == null || userId.isEmpty())
            return false;
        return USER_ID_PATTERN.matcher(userId).matches();
    }

    public boolean hasRegistered(String userId) {
        return userDao.selectUser(userId).isPresent();
    }

    public Optional<User> findUser(String userId) {
        return userDao.selectUser(userId);
    }

    public void save(User user) {

    }

}
