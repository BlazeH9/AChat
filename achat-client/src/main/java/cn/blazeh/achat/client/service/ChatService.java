package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.proto.MessageProto.*;

public class ChatService extends ClientService {

    public void sendMessage(String receiver, String message) {
        if(!getSession().getAuthState().equals(Session.AuthState.DONE)) {
            System.out.println("尚未完成登录，请先登录！");
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
