package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * 客户端服务顶级类，一般不使用，而是使用其子类
 */
public class ClientService {

    private static final Logger LOGGER = LogManager.getLogger(ClientService.class);

    /**
     * 构建消息并发送
     * @param builder 消息构建器
     */
    protected void writeAndFlush(AChatEnvelope.Builder builder) {
        writeAndFlush(builder.build());
    }

    /**
     * 发送消息
     * @param envelope 要发送的消息
     */
    protected void writeAndFlush(AChatEnvelope envelope) {
        LOGGER.debug("尝试发送消息");
        if(getSession().getChannel().isEmpty()) {
            LOGGER.warn("频道为空，消息发送失败");
            return;
        }
        getSession().getChannel()
                .filter(Channel::isActive)
                .ifPresent(channel -> channel.writeAndFlush(envelope));
    }

    /**
     * 获取会话实例
     * @return 会话实例
     */
    protected Session getSession() {
        return SessionManager.INSTANCE.getSession();
    }

    /**
     * 获取会话ID
     * @return 会话ID
     */
    protected String getSessionId() {
        return getSession().getSessionId()
                .map(UUID::toString)
                .orElse("");
    }

    /**
     * 获取网络协议构建起
     * @return 网络协议构建器
     */
    protected AChatEnvelope.Builder getAChatEnvelopeBuilder() {
        return AChatEnvelope.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setSessionId(getSessionId());
    }

}
