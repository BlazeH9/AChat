package cn.blazeh.achat.server.service;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto;
import cn.blazeh.achat.server.handler.AChatServerHandler;
import cn.blazeh.achat.server.manager.InboxManager;
import cn.blazeh.achat.server.manager.MessageManager;
import cn.blazeh.achat.server.manager.UserManager;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class ChatService {

    private static final Logger LOGGER = LogManager.getLogger(ChatService.class);

    private final ConnectionService connectionService;
    private final InboxManager inboxManager;
    private final MessageManager messageManager;
    private final UserManager userManager;

    public ChatService(ConnectionService connectionService, InboxManager inboxManager, MessageManager messageManager, UserManager userManager) {
        this.connectionService = connectionService;
        this.inboxManager = inboxManager;
        this.messageManager = messageManager;
        this.userManager = userManager;
    }

    public long processChat(Message.MessageBuilder builder) {
        return connectionService.getSessionId(builder.getReceiver()).map(sessionId -> {
            Message message = builder.build();
            messageManager.saveMessage(message);
            LOGGER.debug("接收者{}在线，立即发送消息", message.getReceiver());
            if(sendPrivateMessage(sessionId, message)) {
                LOGGER.debug("消息发送成功");
                return message.getMessageId();
            } else {
                LOGGER.debug("消息发送失败");
                return -1L;
            }
        }).orElseGet(() -> {
            if(userManager.hasRegistered(builder.getReceiver())) {
                Message message = builder.build();
                messageManager.saveMessage(message);
                inboxManager.addMessage(message.getReceiver(), message.getMessageId());
                LOGGER.debug("接收者{}不在线，已暂存于服务器", message.getReceiver());
                return message.getMessageId();
            } else {
                LOGGER.debug("接收者{}未注册，消息已忽略", builder.getReceiver());
                return -2L;
            }
        });
    }

    public boolean sendPrivateMessage(UUID sessionId, Message msg) {
        return connectionService.getChannel(sessionId)
                .map(channel ->sendPrivateMessage(channel, msg))
                .orElse(false);
    }

    public boolean sendPrivateMessage(Channel channel, Message msg) {
        if(channel == null || !channel.isActive())
            return false;
        channel.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                .setType(MessageProto.AChatType.CHAT)
                .setChat(msg.toAChatChat())
                .build()
        );
        LOGGER.debug("发送消息：{} -> {}: {}", msg.getSender(), msg.getReceiver(), msg.getContent());
        return true;
    }

}
