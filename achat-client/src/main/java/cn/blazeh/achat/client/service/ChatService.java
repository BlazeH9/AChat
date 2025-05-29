package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.proto.MessageProto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatService extends ClientService {

    private static final Logger LOGGER = LogManager.getLogger(ChatService.class);

    public void sendMessage(String receiver, String message) {
        if(!getSession().getAuthState().equals(Session.AuthState.DONE)) {
            LOGGER.warn("尚未完成登录，消息发送失败");
            return;
        }
        writeAndFlush(getAChatEnvelopeBuilder()
                .setType(AChatType.CHAT)
                .setChat(AChatChat.newBuilder()
                        .setMessageId(System.currentTimeMillis())
                        .setSenderId(getSession().getUserId())
                        .setReceiverId(receiver)
                        .setType(MessageType.TEXT)
                        .setContent(message)
                )
        );
    }

}
