package cn.blazeh.achat.client.model;

import io.netty.channel.Channel;

import java.util.Optional;
import java.util.UUID;

public class Session {

    /**
     * 认证状态枚举类
     */
    public enum AuthState {
        /**
         * 预备阶段，此时尚未连接到服务器
         */
        PREPARING,

        /**
         * 准备阶段，此时已连接到服务器但未发送认证请求
         */
        READY,

        /**
         * 已发送阶段，此时已经向服务器发送了认证请求，但未收到响应
         */
        PENDING,

        /**
         * 完成阶段，此时已经完成了认证
         */
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
