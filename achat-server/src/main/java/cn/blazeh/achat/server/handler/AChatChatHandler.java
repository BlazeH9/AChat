package cn.blazeh.achat.server.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto.*;
import cn.blazeh.achat.server.model.MessageFactory;
import cn.blazeh.achat.server.service.ChatService;
import cn.blazeh.achat.server.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class AChatChatHandler implements AChatHandler {

    private static final Logger LOGGER = LogManager.getLogger(AChatChatHandler.class);

    private final ChatService chatService;
    private final UserService userService;

    public AChatChatHandler(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        UUID sessionId = UUID.fromString(envelope.getSessionId());
        userService.getUserId(sessionId).ifPresent(senderId -> {
            AChatChat chat = envelope.getChat();
            String receiverId = chat.getReceiverId();
            LOGGER.debug("收到客户端消息：{} -> {} : {}", senderId, receiverId, chat.getContent());
            Message.MessageBuilder builder = MessageFactory.newBuilder()
                    .setTimestamp(System.currentTimeMillis())
                    .setType(chat.getType())
                    .setSender(senderId)
                    .setReceiver(receiverId)
                    .setContent(chat.getContent());
            long messageId = chatService.processChat(builder);
            if(messageId >= 0) {
                ctx.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                        .setType(AChatType.SEND)
                        .setSend(AChatSend.newBuilder()
                                .setSuccess(true)
                                .setOrgMsgId(chat.getMessageId())
                                .setNewMsgId(messageId)
                                .setError("")
                        )
                        .build()
                );
            } else {
                ctx.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                        .setType(AChatType.SEND)
                        .setSend(AChatSend.newBuilder()
                                .setSuccess(false)
                                .setOrgMsgId(chat.getMessageId())
                                .setNewMsgId(-1)
                                .setError(messageId == -2 ? chat.getReceiverId()+"未注册" : "发送失败")
                        )
                        .build()
                );
            }
        });
    }

}
