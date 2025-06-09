package cn.blazeh.achat.server.manager;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通道管理器，维护会话ID与channel的关联关系。
 * 提供通道与会话的绑定和解绑操作
 */
public class ChannelManager {

    public static final AttributeKey<UUID> SESSION_ID = AttributeKey.valueOf("sessionId");

    private final Map<UUID, Channel> channels = new ConcurrentHashMap<>();

    public void combine(UUID sessionId, Channel channel) {
        channel.attr(SESSION_ID).set(sessionId);
        channels.put(sessionId, channel);
    }

    /**
     * 解除指定会话ID的channel绑定
     * @param sessionId 要解绑的会话ID
     * @return 解绑的通道
     */
    public Optional<Channel> separate(UUID sessionId) {
        return Optional.ofNullable(channels.remove(sessionId))
                .map(channel -> {
                    channel.attr(SESSION_ID).set(null);
                    return channel;
                });
    }

    /**
     * 解除指定channel的会话绑定
     * @param channel 要解绑的channel
     * @return 解绑的会话ID
     */
    public Optional<UUID> separate(Channel channel) {
        return Optional.ofNullable(channel.attr(SESSION_ID).get())
                .map(sessionId -> {
                    channel.attr(SESSION_ID).set(null);
                    channels.remove(sessionId);
                    return sessionId;
                });
    }

    /**
     * 获取指定会话ID关联的channel
     * @param uuid 会话ID
     * @return 关联的通道
     */
    public Optional<Channel> getChannel(UUID uuid) {
        return Optional.ofNullable(channels.get(uuid));
    }

    /**
     * 获取channel关联的会话ID
     * @param channel 要查询的channel
     * @return 关联的会话ID
     */
    public Optional<UUID> getSessionId(Channel channel) {
        return Optional.ofNullable(channel.attr(SESSION_ID).get());
    }

}