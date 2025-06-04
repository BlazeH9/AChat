package cn.blazeh.achat.server.service;

import cn.blazeh.achat.server.manager.MessageManager;
import cn.blazeh.achat.server.manager.SessionManager;
import cn.blazeh.achat.server.manager.UserManager;
import cn.blazeh.achat.server.model.User;
import cn.blazeh.achat.server.model.UserSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum UserService {

    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void login(String userId) {
        UserManager.INSTANCE.findUser(userId).ifPresent(user -> {
            users.put(userId, user);
            LOGGER.info("{}登录成功", userId);
        });
    }

    public void logout(String userId) {
        UserManager.INSTANCE.save(users.remove(userId));
        LOGGER.info("{}退出登录", userId);
    }

    public void flushInbox(String userId) {
        SessionManager.INSTANCE.getSession(userId).map(UserSession::getSessionId).ifPresent(sessionId -> {
            InboxService.INSTANCE.getMessages(userId).stream()
                    .map(MessageManager.INSTANCE::getMessage)
                    .flatMap(Optional::stream)
                    .forEach(msg -> ChatService.INSTANCE.sendPrivateMessage(sessionId, msg));
            InboxService.INSTANCE.clearMessages(userId);
        });
    }

    public Optional<String> getUserId(UUID sessionId) {
        return SessionManager.INSTANCE.getUserId(sessionId);
    }

    public Optional<User> getUser(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

}
