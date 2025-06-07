package cn.blazeh.achat.server.handler;

import cn.blazeh.achat.common.handler.AChatUndefinedHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import cn.blazeh.achat.server.service.AuthService;
import cn.blazeh.achat.server.service.ChatService;
import cn.blazeh.achat.server.service.ConnectionService;
import cn.blazeh.achat.server.service.UserService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ChannelHandler.Sharable
public class AChatServerHandler extends SimpleChannelInboundHandler<AChatEnvelope> {

    private static final Logger LOGGER = LogManager.getLogger(AChatServerHandler.class);

    private final AuthService authService;
    private final ChatService chatService;
    private final ConnectionService connectionService;
    private final UserService userService;

    public AChatServerHandler(AuthService authService, ChatService chatService,
                              ConnectionService connectionService, UserService userService) {
        this.authService = authService;
        this.chatService = chatService;
        this.connectionService = connectionService;
        this.userService = userService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("客户端建立连接：{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("客户端断开连接：{}", ctx.channel().remoteAddress());
        connectionService.getUserId(ctx.channel()).ifPresent(userService::logout);
        connectionService.terminate(ctx.channel());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AChatEnvelope msg) {
        LOGGER.debug("收到客户端数据包：{}", msg.getType());
        if(!msg.getType().equals(AChatType.AUTH) && !authService.sessionAuth(msg.getSessionId())) {
            LOGGER.warn("数据包中携带非法Session ID：{}",  ctx.channel().remoteAddress());
            return;
        }
        switch(msg.getType()) {
            case AUTH -> new AChatAuthHandler(chatService, connectionService, userService).handle(ctx, msg);
            case CHAT -> new AChatChatHandler(chatService, userService).handle(ctx, msg);
            case HEARTBEAT -> new AChatHeartbeatHandler().handle(ctx, msg);
            default -> new AChatUndefinedHandler().handle(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.warn("客户端异常断开连接: {}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }

    public static AChatEnvelope.Builder getEnvelopeBuilder() {
        return AChatEnvelope.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setSessionId("");
    }

}
