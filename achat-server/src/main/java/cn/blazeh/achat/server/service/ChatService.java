package cn.blazeh.achat.server.service;

import cn.blazeh.achat.common.proto.MessageProto;
import cn.blazeh.achat.server.handler.AChatServerHandler;
import cn.blazeh.achat.server.manager.ChannelManager;
import cn.blazeh.achat.server.manager.MessageManager;
import cn.blazeh.achat.server.manager.SessionManager;
import cn.blazeh.achat.server.manager.UserManager;
import cn.blazeh.achat.server.model.Message;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public enum ChatService {

    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger(ChatService.class);

    public void processChat(Message.MessageBuilder builder) {
        SessionManager.INSTANCE.getSession(builder.getReceiver()).ifPresentOrElse(session -> {
            Message message = builder.build();
            LOGGER.debug("接收者{}在线，立即发送消息", message.getReceiver());
            if(sendPrivateMessage(session.getSessionId(), builder.build()))
                LOGGER.debug("消息发送成功");
            else
                LOGGER.debug("消息发送失败");
        }, () -> {
            if(UserManager.INSTANCE.hasRegistered(builder.getReceiver())) {
                Message message = builder.build();
                saveMessage(message);
                LOGGER.debug("接收者{}不在线，已暂存于服务器", message.getReceiver());
            } else {
                LOGGER.debug("接收者{}未注册，消息已忽略", builder.getReceiver());
            }
        });
    }

    public boolean sendPrivateMessage(UUID sessionId, Message msg) {
        return ChannelManager.INSTANCE.getChannel(sessionId)
                .filter(Channel::isActive)
                .map(channel -> {
                    channel.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                            .setType(MessageProto.AChatType.CHAT)
                            .setChat(msg.toAChatChat())
                            .build()
                    );

                    LOGGER.debug("发送消息：{} -> {}: {}", msg.getSender(), msg.getReceiver(), msg.getContent());
                    return true;
                })
                .orElse(false);
    }

    public void saveMessage(Message message) {
        MessageManager.INSTANCE.addMessage(message);
        InboxService.INSTANCE.addMessage(message.getReceiver(), message.getMessageId());
    }

}
