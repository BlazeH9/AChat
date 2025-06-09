package cn.blazeh.achat.server.service;

import cn.blazeh.achat.server.manager.SessionManager;
import cn.blazeh.achat.server.manager.UserManager;

import java.util.UUID;

/**
 * 用户认证服务，提供登录、注册和会话验证功能
 */
public class AuthService {

    private final UserManager userManager;
    private final SessionManager sessionManager;

    public AuthService(UserManager userManager, SessionManager sessionManager) {
        this.userManager = userManager;
        this.sessionManager = sessionManager;
    }

    /**
     * 验证用户登录凭证
     * @param userId 用户ID
     * @param password 用户密码
     * @return 验证成功返回true，否则false
     */
    public boolean loginAuth(String userId, String password) {
        return userManager.check(userId, password);
    }

    /**
     * 注册新用户。
     * @param userId 用户ID
     * @param password 用户密码
     * @return 注册成功返回true，用户已存在返回false
     */
    public boolean registerAuth(String userId, String password) {
        return userManager.add(userId, password);
    }

    /**
     * 验证会话ID有效性
     * @param sessionId 会话ID字符串
     * @return 会话有效返回true，否则false
     */
    public boolean sessionAuth(String sessionId) {
        return sessionAuth(UUID.fromString(sessionId));
    }

    /**
     * 验证会话ID有效性
     * @param sessionId 会话UUID
     * @return 会话有效返回true，否则false
     */
    public boolean sessionAuth(UUID sessionId) {
        return sessionManager.containsSession(sessionId);
    }
}