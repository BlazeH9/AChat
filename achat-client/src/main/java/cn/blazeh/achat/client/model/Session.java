package cn.blazeh.achat.client.model;

import io.netty.channel.Channel;

import java.util.Optional;
import java.util.UUID;

public class Session {

    public enum AuthState {
        PREPARING,
        READY,
        PENDING,
        DONE
    }

    private String userId;
    private UUID sessionId;
    private Channel channel;
    private AuthState state = AuthState.PREPARING;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Optional<UUID> getSessionId() {
        return Optional.ofNullable(sessionId);
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Optional<Channel> getChannel() {
        return Optional.ofNullable(channel);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public AuthState getAuthState() {
        return state;
    }

    public void setAuthState(AuthState state) {
        this.state = state;
    }
}
