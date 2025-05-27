package cn.blazeh.achat.server.manager;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum ChannelManager {

    INSTANCE;

    public static final AttributeKey<UUID> SESSION_ID = AttributeKey.valueOf("sessionId");

    private final Map<UUID, Channel> channels = new ConcurrentHashMap<>();

    public void combine(UUID sessionId, Channel channel) {
        channel.attr(SESSION_ID).set(sessionId);
        channels.put(sessionId, channel);
    }

    public Optional<Channel> separate(UUID sessionId) {
        return Optional.ofNullable(channels.remove(sessionId))
                .map(channel -> {
                    channel.attr(SESSION_ID).set(null);
                    return channel;
                });
    }

    public Optional<UUID> separate(Channel channel) {
        return Optional.ofNullable(channel.attr(SESSION_ID).get())
                .map(sessionId -> {
                    channel.attr(SESSION_ID).set(null);
                    channels.remove(sessionId);
                    return sessionId;
                });
    }

    public Optional<Channel> getChannel(UUID uuid) {
        return Optional.ofNullable(channels.get(uuid));
    }

    public Optional<UUID> getSessionId(Channel channel) {
        return Optional.ofNullable(channel.attr(SESSION_ID).get());
    }

}
