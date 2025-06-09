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

/**
 * 用户实力相关服务
 */
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

    /**
     * 用户注册方法
     * @param userId 用户ID
     * @param password 密码
     * @return 是否注册成功
     */
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

    /**
     * 用户登录方法
     * @param userId 用户ID
     * @param password 密码
     * @return 是否登录成功
     */
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

    /**
     * 用户退出登录方法
     * @param userId 用户ID
     */
    public void logout(String userId) {
        userManager.save(users.remove(userId));
        LOGGER.info("{}退出登录", userId);
    }

    /**
     * 对指定用户的信箱执行一系列操作，操作完成后将清空信箱
     * @param userId 用户ID
     * @param consumer 要执行的操作
     */
    public void flushInbox(String userId, Consumer<Message> consumer) {
        sessionManager.getSession(userId).map(UserSession::getSessionId).ifPresent(sessionId -> {
            inboxManager.getMessages(userId).stream()
                    .map(messageManager::getMessage)
                    .flatMap(Optional::stream)
                    .forEach(consumer);
            inboxManager.clearMessages(userId);
        });
    }

    /**
     * 通过会话ID获取用户ID
     * @param sessionId 会话ID
     * @return 用户ID
     */
    public Optional<String> getUserId(UUID sessionId) {
        return sessionManager.getUserId(sessionId);
    }

    /**
     * 通过用户ID获取对应的用户实例
     * @param userId 用户ID
     * @return 用户实例
     */
    public Optional<User> getUser(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

}
