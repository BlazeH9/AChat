package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.client.AChatClient;
import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.handler.AChatUndefinedHandler;
import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 客户端处理器，根据消息类型进行消息转发
 */
public class AChatClientHandler extends SimpleChannelInboundHandler<AChatEnvelope> {

    private static final Logger LOGGER = LogManager.getLogger(AChatClientHandler.class);

    private final AChatClient client;
    private final AChatHandler[] handlers;

    public AChatClientHandler(AChatClient client) {
        this.client = client;
        handlers = new AChatHandler[] {
                new AChatUndefinedHandler(), new AChatUndefinedHandler(),
                new AChatAuthHandler(client), new AChatChatHandler(client), new AChatSendHandler()
        };
    }

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
