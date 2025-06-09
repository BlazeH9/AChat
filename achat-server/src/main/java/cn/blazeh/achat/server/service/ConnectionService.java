package cn.blazeh.achat.server.service;

import cn.blazeh.achat.server.manager.ChannelManager;
import cn.blazeh.achat.server.manager.SessionManager;
import io.netty.channel.Channel;

import java.util.Optional;
import java.util.UUID;

/**
 * 连接相关服务，可以实现Channel, SessionID, UserID三者间的相互转换
 */
public class ConnectionService {

    private final ChannelManager channelManager;
    private final SessionManager sessionManager;

    public ConnectionService(ChannelManager channelManager, SessionManager sessionManager) {
        this.channelManager = channelManager;
        this.sessionManager = sessionManager;
    }

    /**
     * 用户建立连接时调用，将会新建会话ID,Channel和用户ID进行绑定
     * @param userId 用户ID
     * @param channel 频道
     * @return 会话ID
     */
    public UUID establish(String userId, Channel channel) {
        UUID sessionId = sessionManager.addSession(userId);
        channelManager.combine(sessionId, channel);
        return sessionId;
    }

    /**
     * 连接中止时调用，会移除用户ID绑定的信息
     * @param userId 用户ID
     */
    public void terminate(String userId) {
        sessionManager.removeSession(userId)
                .ifPresent(session -> channelManager.separate(session.getSessionId()));
    }

    /**
     * 连接中止时调用，会移除Channel绑定的信息
     * @param channel 频道
     */
    public void terminate(Channel channel) {
        channelManager.separate(channel).ifPresent(sessionManager::removeSession);
    }

    /**
     * 通过会话ID获取用户ID
     * @param sessionId 会话ID
     * @return 用户ID
     */
    public Optional<String> getUserId(UUID sessionId) {
        return sessionManager.getUserId(sessionId);
    }

    /**
     * 通过Channel获取用户ID
     * @param channel 要获取用户ID的Channel
     * @return 用户ID
     */
    public Optional<String> getUserId(Channel channel) {
        return getSessionId(channel).flatMap(this::getUserId);
    }

    /**
     * 通过channel获取会话ID
     * @param channel 要获取会话ID的channel
     * @return 会话ID
     */
    public Optional<UUID> getSessionId(Channel channel) {
        return channelManager.getSessionId(channel);
    }

    /**
     * 通过用户ID获取会话ID
     * @param userId 用户ID
     * @return 会话ID
     */
    public Optional<UUID> getSessionId(String userId) {
        return sessionManager.getSessionId(userId);
    }

    /**
     * 通过会话ID获取channel
     * @param sessionId 会话ID
     * @return channel
     */
    public Optional<Channel> getChannel(UUID sessionId) {
        return channelManager.getChannel(sessionId);
    }

    /**
     * 通过用户ID获取channel
     * @param userId 用户ID
     * @return channel
     */
    public Optional<Channel> getChannel(String userId) {
        return sessionManager.getSessionId(userId).flatMap(this::getChannel);
    }

}
