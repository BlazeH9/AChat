package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.common.MessageProto.AChatMessage;
import cn.blazeh.achat.common.MessageProto.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AChatClientHandler extends SimpleChannelInboundHandler<AChatMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AChatMessage msg) {
        if (msg.getType() == MessageType.HEARTBEAT)
            return;
        System.out.println("<" + msg.getSenderId() + "> " + msg.getContent());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
