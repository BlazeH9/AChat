package cn.blazeh.achat.server.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.AChatChat;
import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import cn.blazeh.achat.server.service.ChatService;
import cn.blazeh.achat.server.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AChatChatHandler implements AChatHandler {

    private static final Logger LOGGER = LogManager.getLogger(AChatChatHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        UserService.INSTANCE.getUserId(envelope.getSessionId()).ifPresent(senderId -> {
            AChatChat msg = envelope.getChat();
            String receiverId = msg.getReceiverId();
            LOGGER.debug("收到客户端消息：{} -> {} : {}", senderId, receiverId, msg.getContent());
            if(ChatService.INSTANCE.sendPrivateMessage(senderId, msg)) {
                LOGGER.debug("消息转发至 {}", receiverId);
            } else {
                LOGGER.debug("用户 {} 已离线或不存在，消息转发失败", receiverId);
            }
        });
    }

}
