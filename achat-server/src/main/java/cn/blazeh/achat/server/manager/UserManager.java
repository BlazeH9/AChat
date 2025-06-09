package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.server.dao.UserDao;
import cn.blazeh.achat.server.model.User;
import cn.blazeh.achat.server.util.DigestUtils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户管理服务，负责用户注册、登录验证和用户信息管理
 */
public class UserManager {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    private final UserDao userDao;

    public UserManager(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 验证用户ID与密码是否匹配
     * @param userId 用户ID
     * @param password 密码
     * @return 匹配成功返回true，否则返回false
     */
    public boolean check(String userId, String password) {
        return userId != null && userDao.selectUser(userId)
                .map(user -> DigestUtils.hashWithSalt(password, user.getSalt()).equals(user.getPassword()))
                .orElse(false);
    }

    /**
     * 添加新用户
     * @param userId 用户ID
     * @param password 密码
     * @return 添加成功返回true，否则返回false
     */
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

    /**
     * 检查用户ID是否合法
     * @param userId 用户ID
     * @return 合法返回true，不合法返回false
     */
    public boolean checkUserId(String userId) {
        if(userId == null || userId.isEmpty())
            return false;
        return USER_ID_PATTERN.matcher(userId).matches();
    }

    /**
     * 检查用户是否已经注册
     * @param userId 用户ID
     * @return 已注册返回true，否则返回false
     */
    public boolean hasRegistered(String userId) {
        return userDao.selectUser(userId).isPresent();
    }

    /**
     * 根据用户ID查找对应的数据
     * @param userId 用户ID
     * @return 用户User实例
     */
    public Optional<User> findUser(String userId) {
        return userDao.selectUser(userId);
    }

    public void save(User user) {

    }

}