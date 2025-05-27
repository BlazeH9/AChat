package cn.blazeh.achat.server.model;

import java.util.UUID;

public class UserSession {

    private final String userId;
    private final UUID sessionId;
    private final long expireTime;

    public UserSession(String userId, UUID sessionId, long expireTime) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.expireTime = expireTime;
    }

    public String getUserId() {
        return userId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public long getExpireTime() {
        return expireTime;
    }
}
