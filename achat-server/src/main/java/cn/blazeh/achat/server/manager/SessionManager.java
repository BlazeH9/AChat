package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.server.model.UserSession;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<UUID, UserSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, UUID> ids = new ConcurrentHashMap<>();
    private static final long EXPIRE_TIME = 30*60*1000;

    private final Map<String, Object> locks = new ConcurrentHashMap<>();

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

    public Optional<UserSession> removeSession(UUID sessionId) {
        return getUserId(sessionId).flatMap(this::removeSession);
    }

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

    public Optional<UserSession> getSession(UUID sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public Optional<UserSession> getSession(String userId) {
        return Optional.ofNullable(ids.get(userId)).flatMap(this::getSession);
    }

    public Optional<String> getUserId(UUID sessionId) {
        return Optional.ofNullable(sessionId)
                .map(sessions::get)
                .map(UserSession::getUserId);
    }

    public Optional<UUID> getSessionId(String userId) {
        return Optional.ofNullable(userId).map(ids::get);
    }

    public boolean containsSession(UUID sessionId) {
        return sessions.containsKey(sessionId);
    }

}
