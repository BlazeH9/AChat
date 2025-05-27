package cn.blazeh.achat.server.service;

import cn.blazeh.achat.common.proto.MessageProto.*;
import cn.blazeh.achat.server.handler.AChatServerHandler;
import cn.blazeh.achat.server.manager.ChannelManager;
import cn.blazeh.achat.server.manager.SessionManager;
import cn.blazeh.achat.server.model.UserSession;
import cn.blazeh.achat.server.util.IdGenerator;
import io.netty.channel.Channel;

public enum ChatService {

    INSTANCE;

    public boolean sendPrivateMessage(String senderId, AChatChat msg) {
        return SessionManager.INSTANCE.getSession(msg.getReceiverId())
                .map(UserSession::getSessionId)
                .flatMap(ChannelManager.INSTANCE::getChannel)
                .filter(Channel::isActive)
                .map(channel -> {
                    System.out.println("私聊消息: " + senderId + " -> " + msg.getReceiverId() + ": " + msg.getContent());
                    channel.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                            .setType(AChatType.CHAT)
                            .setChat(msg.toBuilder()
                                    .setMessageId(IdGenerator.nextId())
                                    .setSenderId(senderId)
                            )
                            .build()
                    );
                    return true;
                })
                .orElse(false);
    }
}
