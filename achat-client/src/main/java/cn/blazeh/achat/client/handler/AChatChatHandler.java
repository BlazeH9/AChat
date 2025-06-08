package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.client.AChatClient;
import cn.blazeh.achat.client.model.MessageFactory;
import cn.blazeh.achat.client.service.ChatService;
import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto.*;
import io.netty.channel.ChannelHandlerContext;

public class AChatChatHandler implements AChatHandler {

    private final AChatClient client;

    public AChatChatHandler(AChatClient client) {
        this.client = client;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        AChatChat msg = envelope.getChat();
        Message message = MessageFactory.newBuilder()
                .setMessageId(msg.getMessageId())
                .setTimestamp(msg.getTimestamp())
                .setSender(msg.getSenderId())
                .setReceiver(msg.getReceiverId())
                .setType(msg.getType())
                .setContent(msg.getContent())
                .build();
        ChatService.getInstance().receive(message);
        client.getChatFrame().receiveMessage(message);
    }

}
