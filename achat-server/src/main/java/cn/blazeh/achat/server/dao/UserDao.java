package cn.blazeh.achat.server.dao;

import cn.blazeh.achat.server.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserDao {

    private static final Map<String, User> users = new ConcurrentHashMap<>();

    static {
        users.put("test1", new User("test1", "123456"));
        users.put("test2", new User("test2", "88888888"));
    }

    private UserDao() {}

    public static Optional<User> findUser(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public static void insertUser(User user) {
        users.put(user.getUserId(), user);
    }
}
