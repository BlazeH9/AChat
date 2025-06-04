package cn.blazeh.achat.server.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import cn.blazeh.achat.server.model.Message;
import cn.blazeh.achat.server.service.ChatService;
import cn.blazeh.achat.server.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class AChatChatHandler implements AChatHandler {

    private static final Logger LOGGER = LogManager.getLogger(AChatChatHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        UserService.INSTANCE.getUserId(UUID.fromString(envelope.getSessionId())).ifPresent(senderId -> {
            AChatChat chat = envelope.getChat();
            String receiverId = chat.getReceiverId();
            LOGGER.debug("收到客户端消息：{} -> {} : {}", senderId, receiverId, chat.getContent());
            Message.MessageBuilder builder = Message.newBuilder()
                    .setTimestamp(System.currentTimeMillis())
                    .setType(chat.getType())
                    .setSender(senderId)
                    .setReceiver(receiverId)
                    .setContent(chat.getContent());
            ChatService.INSTANCE.processChat(builder);
        });
    }

}
