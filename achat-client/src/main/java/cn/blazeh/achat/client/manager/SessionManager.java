package cn.blazeh.achat.client.manager;

import cn.blazeh.achat.client.model.Session;

import java.util.concurrent.atomic.AtomicReference;

public enum SessionManager {

    INSTANCE;

    private final AtomicReference<Session> SESSION = new AtomicReference<>(new Session());

    public Session getSession() {
        return SESSION.get();
    }

}
