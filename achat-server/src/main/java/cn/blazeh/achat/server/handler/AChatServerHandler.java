package cn.blazeh.achat.server.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.handler.AChatUndefinedHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import cn.blazeh.achat.server.service.AuthService;
import cn.blazeh.achat.server.service.ConnectionService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AChatServerHandler extends SimpleChannelInboundHandler<AChatEnvelope> {

    private static final Logger LOGGER = LogManager.getLogger(AChatServerHandler.class);

    private final AChatHandler[] handlers = {
            new AChatUndefinedHandler(), new AChatHeartbeatHandler(), new AChatAuthHandler(),
            new AChatChatHandler()
    };

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("客户端建立连接：{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("客户端断开连接：{}", ctx.channel().remoteAddress());
        ConnectionService.INSTANCE.terminate(ctx.channel());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AChatEnvelope msg) {
        LOGGER.debug("收到客户端数据包：{}", msg.getType());
        if(!msg.getType().equals(AChatType.AUTH) && !AuthService.INSTANCE.sessionAuth(msg.getSessionId())) {
            LOGGER.warn("数据包中携带非法Session ID：{}",  ctx.channel().remoteAddress());
            return;
        }
        handlers[msg.getType().getNumber()].handle(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.warn("客户端异常断开连接: {}", ctx.channel().remoteAddress(), cause);
        ConnectionService.INSTANCE.terminate(ctx.channel());
        ctx.close();
    }

    public static AChatEnvelope.Builder getEnvelopeBuilder() {
        return AChatEnvelope.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setSessionId("");
    }

}
