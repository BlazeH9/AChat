package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import io.netty.channel.ChannelHandlerContext;

public class AChatChatHandler implements AChatHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        AChatChat msg = envelope.getChat();
        System.out.println("<" + msg.getSenderId() + "> " + msg.getContent());
    }
}
