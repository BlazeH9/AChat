package cn.blazeh.achat.server.service;

import cn.blazeh.achat.server.manager.SessionManager;
import cn.blazeh.achat.server.manager.UserManager;

import java.util.UUID;

public enum AuthService {

    INSTANCE;

    public boolean loginAuth(String userId, String password) {
        return UserManager.INSTANCE.check(userId, password);
    }

    public boolean registerAuth(String userId, String password) {
        return UserManager.INSTANCE.add(userId, password);
    }

    public boolean sessionAuth(String sessionId) {
        return sessionAuth(UUID.fromString(sessionId));
    }

    public boolean sessionAuth(UUID sessionId) {
        return SessionManager.INSTANCE.containsSession(sessionId);
    }

}
