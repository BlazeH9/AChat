package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.client.model.MessageFactory;
import cn.blazeh.achat.client.service.ChatService;
import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import io.netty.channel.ChannelHandlerContext;

public class AChatChatHandler implements AChatHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        AChatChat msg = envelope.getChat();
        ChatService.getInstance().receive(MessageFactory.newBuilder()
                .setMessageId(msg.getMessageId())
                .setTimestamp(msg.getTimestamp())
                .setSender(msg.getSenderId())
                .setReceiver(msg.getReceiverId())
                .setType(msg.getType())
                .setContent(msg.getContent())
                .build()
        );
    }

}
