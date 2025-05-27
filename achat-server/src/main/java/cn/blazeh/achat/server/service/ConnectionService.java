package cn.blazeh.achat.server.service;

import cn.blazeh.achat.server.manager.ChannelManager;
import cn.blazeh.achat.server.manager.SessionManager;
import io.netty.channel.Channel;

import java.util.UUID;

public enum ConnectionService {

    INSTANCE;

    public UUID establish(String userId, Channel channel) {
        UUID sessionId = SessionManager.INSTANCE.addSession(userId);
        ChannelManager.INSTANCE.combine(sessionId, channel);
        UserService.INSTANCE.login(userId);
        return sessionId;
    }

    public void terminate(String userId) {
        SessionManager.INSTANCE.removeSession(userId)
                .ifPresent(session -> {
                    UserService.INSTANCE.logout(userId);
                    ChannelManager.INSTANCE.separate(session.getSessionId());
                });
    }

    public void terminate(Channel channel) {
        ChannelManager.INSTANCE.separate(channel)
                .flatMap(SessionManager.INSTANCE::removeSession)
                .ifPresent(session -> UserService.INSTANCE.logout(session.getUserId()));
    }
}
