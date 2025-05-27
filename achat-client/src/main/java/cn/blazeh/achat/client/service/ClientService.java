package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.Channel;

import java.util.UUID;

public class ClientService {

    protected void writeAndFlush(AChatEnvelope.Builder builder) {
        writeAndFlush(builder.build());
    }

    protected void writeAndFlush(AChatEnvelope envelope) {
        System.out.println("尝试发送消息");
        if(getSession().getChannel().isEmpty()) {
            System.out.println("频道为空，消息发送失败");
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
