package cn.blazeh.achat.server.service;

import cn.blazeh.achat.server.manager.SessionManager;
import cn.blazeh.achat.server.manager.UserManager;

import java.util.UUID;

public class AuthService {

    private final UserManager userManager;
    private final SessionManager sessionManager;

    public AuthService(UserManager userManager, SessionManager sessionManager) {
        this.userManager = userManager;
        this.sessionManager = sessionManager;
    }

    public boolean loginAuth(String userId, String password) {
        return userManager.check(userId, password);
    }

    public boolean registerAuth(String userId, String password) {
        return userManager.add(userId, password);
    }

    public boolean sessionAuth(String sessionId) {
        return sessionAuth(UUID.fromString(sessionId));
    }

    public boolean sessionAuth(UUID sessionId) {
        return sessionManager.containsSession(sessionId);
    }

}
