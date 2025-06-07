package cn.blazeh.achat.server.dao;

import cn.blazeh.achat.server.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserDao {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public UserDao() {
        users.put("test1", new User("test1", "123456"));
        users.put("test2", new User("test2", "88888888"));
    }

    public Optional<User> findUser(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public void insertUser(User user) {
        users.put(user.getUserId(), user);
    }

    public void insertUser(String userId, String password) {
        insertUser(new User(userId, password));
    }

    public boolean checkAndInsertUser(String userId, String password) {
        if(users.containsKey(userId))
            return false;
        insertUser(userId, password);
        return true;
    }

    public void updateUser(User user) {
        users.put(user.getUserId(), user);
    }

}
