package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class ClientService {

    private static final Logger LOGGER = LogManager.getLogger(ClientService.class);

    protected void writeAndFlush(AChatEnvelope.Builder builder) {
        writeAndFlush(builder.build());
    }

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

    protected Session getSession() {
        return SessionManager.INSTANCE.getSession();
    }

    protected String getSessionId() {
        return getSession().getSessionId()
                .map(UUID::toString)
                .orElse("");
    }

    protected AChatEnvelope.Builder getAChatEnvelopeBuilder() {
        return AChatEnvelope.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setSessionId(getSessionId());
    }

}
