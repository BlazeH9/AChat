package cn.blazeh.achat.server.service;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.server.manager.InboxManager;
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
import java.util.function.Consumer;

public class UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private final Map<String, User> users = new ConcurrentHashMap<>();

    private final InboxManager inboxManager;
    private final UserManager userManager;
    private final SessionManager sessionManager;
    private final MessageManager messageManager;

    public UserService(InboxManager inboxManager, UserManager userManager, SessionManager sessionManager, MessageManager messageManager) {
        this.inboxManager = inboxManager;
        this.userManager = userManager;
        this.sessionManager = sessionManager;
        this.messageManager = messageManager;
    }

    public boolean register(String userId, String password) {
        if(users.containsKey(userId) || !userManager.add(userId, password))
            return false;
        return userManager.findUser(userId).map(user -> {
            users.put(userId, user);
            LOGGER.info("{}注册成功", userId);
            return true;
        }).orElseGet(() -> {
            LOGGER.info("{}注册失败", userId);
            return false;
        });
    }

    public boolean login(String userId, String password) {
        if(!userManager.check(userId, password))
            return false;
        return userManager.findUser(userId).map(user -> {
            users.put(userId, user);
            LOGGER.info("{}登录成功", userId);
            return true;
        }).orElseGet(() -> {
            LOGGER.info("{}登录，该用户不存在", userId);
            return false;
        });
    }

    public void logout(String userId) {
        userManager.save(users.remove(userId));
        LOGGER.info("{}退出登录", userId);
    }

    public void flushInbox(String userId, Consumer<Message> consumer) {
        sessionManager.getSession(userId).map(UserSession::getSessionId).ifPresent(sessionId -> {
            inboxManager.getMessages(userId).stream()
                    .map(messageManager::getMessage)
                    .flatMap(Optional::stream)
                    .forEach(consumer);
            inboxManager.clearMessages(userId);
        });
    }

    public Optional<String> getUserId(UUID sessionId) {
        return sessionManager.getUserId(sessionId);
    }

    public Optional<User> getUser(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

}
