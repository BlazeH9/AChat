package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.handler.AChatUndefinedHandler;
import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AChatClientHandler extends SimpleChannelInboundHandler<AChatEnvelope> {

    private static final AChatHandler[] handlers = {
            new AChatUndefinedHandler(), new AChatUndefinedHandler(),
            new AChatAuthHandler(), new AChatChatHandler()
    };

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AChatEnvelope msg) {
        handlers[msg.getType().getNumber()].handle(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
