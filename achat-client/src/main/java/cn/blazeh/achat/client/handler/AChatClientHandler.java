package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.handler.AChatUndefinedHandler;
import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AChatClientHandler extends SimpleChannelInboundHandler<AChatEnvelope> {

    private static final Logger LOGGER = LogManager.getLogger(AChatClientHandler.class);

    private static final AChatHandler[] handlers = {
            new AChatUndefinedHandler(), new AChatUndefinedHandler(),
            new AChatAuthHandler(), new AChatChatHandler(), new AChatSendHandler()
    };

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AChatEnvelope msg) {
        handlers[msg.getType().getNumber()].handle(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("连接异常断开", cause);
        ctx.close();
    }
}
