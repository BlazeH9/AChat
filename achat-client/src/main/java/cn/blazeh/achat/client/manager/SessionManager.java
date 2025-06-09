package cn.blazeh.achat.client.manager;

import cn.blazeh.achat.client.model.Session;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 会话Manager，单例模式，管理会话实例
 */
public enum SessionManager {

    INSTANCE;

    private final AtomicReference<Session> SESSION = new AtomicReference<>(new Session());

    /**
     * 获取会话实例
     * @return
     */
    public Session getSession() {
        return SESSION.get();
    }

}
