package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.server.model.UserSession;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理器，负责用户会话的创建、维护和销毁 <p>
 * 支持通过用户ID或会话ID进行会话操作，提供线程安全的会话管理
 */
public class SessionManager {

    private final Map<UUID, UserSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, UUID> ids = new ConcurrentHashMap<>();
    private static final long EXPIRE_TIME = 30*60*1000;

    private final Map<String, Object> locks = new ConcurrentHashMap<>();

    /**
     * 为用户创建新的会话，若存在旧会话则先移除。
     *
     * @param userId 用户ID
     * @return 新创建的会话ID
     */
    public UUID addSession(String userId) {
        Object lock = locks.computeIfAbsent(userId, ignored -> new Object());
        synchronized(lock) {
            removeSession(userId);
            UUID sessionId = UUID.randomUUID();
            sessions.put(sessionId, new UserSession(userId, sessionId, System.currentTimeMillis() + EXPIRE_TIME));
            ids.put(userId, sessionId);
            return sessionId;
        }
    }

    /**
     * 根据会话ID移除会话信息
     *
     * @param sessionId 会话ID
     * @return 被移除的会话信息
     */
    public Optional<UserSession> removeSession(UUID sessionId) {
        return getUserId(sessionId).flatMap(this::removeSession);
    }

    /**
     * 根据用户ID移除会话信息
     *
     * @param userId 用户ID
     * @return 被移除的会话信息
     */
    public Optional<UserSession> removeSession(String userId) {
        if(userId == null || userId.isEmpty() || !ids.containsKey(userId))
            return Optional.empty();
        synchronized(locks.get(userId)) {
            return Optional.ofNullable(ids.get(userId))
                    .map(sessionId -> {
                        UserSession session = sessions.remove(sessionId);
                        ids.remove(userId);
                        return session;
                    });
        }
    }

    /**
     * 根据会话ID获取会话信息
     *
     * @param sessionId 会话ID
     * @return 对应的会话信息
     */
    public Optional<UserSession> getSession(UUID sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    /**
     * 根据用户ID获取会话信息
     *
     * @param userId 用户ID
     * @return 对应的会话信息
     */
    public Optional<UserSession> getSession(String userId) {
        return Optional.ofNullable(ids.get(userId)).flatMap(this::getSession);
    }

    /**
     * 根据会话ID获取关联的用户ID
     *
     * @param sessionId 会话ID
     * @return 关联的用户ID
     */
    public Optional<String> getUserId(UUID sessionId) {
        return Optional.ofNullable(sessionId)
                .map(sessions::get)
                .map(UserSession::getUserId);
    }

    /**
     * 根据用户ID获取关联的会话ID
     *
     * @param userId 用户ID
     * @return 关联的会话ID
     */
    public Optional<UUID> getSessionId(String userId) {
        return Optional.ofNullable(userId).map(ids::get);
    }

    /**
     * 检查指定会话ID是否存在
     *
     * @param sessionId 会话ID
     * @return 存在返回true，否则false
     */
    public boolean containsSession(UUID sessionId) {
        return sessions.containsKey(sessionId);
    }

}