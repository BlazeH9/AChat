package cn.blazeh.achat.server.service;

import cn.blazeh.achat.server.manager.ChannelManager;
import cn.blazeh.achat.server.manager.SessionManager;
import io.netty.channel.Channel;

import java.util.Optional;
import java.util.UUID;

public class ConnectionService {

    private final ChannelManager channelManager;
    private final SessionManager sessionManager;

    public ConnectionService(ChannelManager channelManager, SessionManager sessionManager) {
        this.channelManager = channelManager;
        this.sessionManager = sessionManager;
    }

    public UUID establish(String userId, Channel channel) {
        UUID sessionId = sessionManager.addSession(userId);
        channelManager.combine(sessionId, channel);
        return sessionId;
    }

    public void terminate(String userId) {
        sessionManager.removeSession(userId)
                .ifPresent(session -> channelManager.separate(session.getSessionId()));
    }

    public void terminate(Channel channel) {
        channelManager.separate(channel).ifPresent(sessionManager::removeSession);
    }

    public Optional<String> getUserId(UUID sessionId) {
        return sessionManager.getUserId(sessionId);
    }

    public Optional<String> getUserId(Channel channel) {
        return getSessionId(channel).flatMap(this::getUserId);
    }

    public Optional<UUID> getSessionId(Channel channel) {
        return channelManager.getSessionId(channel);
    }

    public Optional<UUID> getSessionId(String userId) {
        return sessionManager.getSessionId(userId);
    }

    public Optional<Channel> getChannel(UUID sessionId) {
        return channelManager.getChannel(sessionId);
    }

    public Optional<Channel> getChannel(String userId) {
        return sessionManager.getSessionId(userId).flatMap(this::getChannel);
    }

}
