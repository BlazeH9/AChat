package cn.blazeh.achat.server.service;

import cn.blazeh.achat.server.manager.SessionManager;

import java.util.Optional;
import java.util.UUID;

public enum UserService {

    INSTANCE;

    public void login(String userId) {
        System.out.println(userId + "登录成功");
    }

    public void logout(String userId) {
        System.out.println(userId + "退出登录");
    }

    public Optional<String> getUserId(String sessionId) {
        return getUserId(UUID.fromString(sessionId));
    }

    public Optional<String> getUserId(UUID sessionId) {
        return SessionManager.INSTANCE.getUserId(sessionId);
    }

}
