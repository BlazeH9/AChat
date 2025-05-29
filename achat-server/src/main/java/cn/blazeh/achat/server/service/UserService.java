package cn.blazeh.achat.server.service;

import cn.blazeh.achat.server.manager.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;

public enum UserService {

    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    public void login(String userId) {
        LOGGER.info("{}登录成功", userId);
    }

    public void logout(String userId) {
        LOGGER.info("{}退出登录", userId);
    }

    public Optional<String> getUserId(String sessionId) {
        return getUserId(UUID.fromString(sessionId));
    }

    public Optional<String> getUserId(UUID sessionId) {
        return SessionManager.INSTANCE.getUserId(sessionId);
    }

}
