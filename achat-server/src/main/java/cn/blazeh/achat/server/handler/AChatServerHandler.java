package cn.blazeh.achat.server.handler;

import cn.blazeh.achat.common.MessageProto.AChatMessage;
import cn.blazeh.achat.common.MessageProto.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AChatServerHandler extends SimpleChannelInboundHandler<AChatMessage> {

    private static final Map<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端建立连接：" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("客户端断开连接: " + ctx.channel().remoteAddress());
        userChannelMap.entrySet().stream()
                .filter(entry -> entry.getValue() == ctx.channel())
                .findFirst()
                .ifPresent(entry -> {
                    String userId = entry.getKey();
                    userChannelMap.remove(userId);
                    System.out.println("用户 " + userId + " 已离线");
                });
        ctx.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AChatMessage msg) {
        System.out.println("收到客户端消息: " + msg.getSenderId() + " -> " + msg.getReceiverId() + ": " + msg.getContent());

        if (!userChannelMap.containsKey(msg.getSenderId())) {
            userChannelMap.put(msg.getSenderId(), ctx.channel());
            System.out.println("用户 " + msg.getSenderId() + " 已上线");
        }

        if (msg.getType() == MessageType.HEARTBEAT) {
            AChatMessage heartbeatResponse = AChatMessage.newBuilder()
                    .setType(MessageType.HEARTBEAT)
                    .setSenderId("server")
                    .setReceiverId(msg.getSenderId())
                    .setTimestamp(System.currentTimeMillis())
                    .setContent("pong")
                    .build();
            ctx.writeAndFlush(heartbeatResponse);
            return;
        }


        String receiverId = msg.getReceiverId();
        Channel receiverChannel = userChannelMap.get(receiverId);

        if (receiverChannel != null && receiverChannel.isActive()) {
            receiverChannel.writeAndFlush(msg);
            System.out.println("消息转发至 " + receiverId);
        } else {
            System.out.println("用户 " + receiverId + " 已离线或不存在，消息转发失败");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        userChannelMap.entrySet().stream()
                .filter(entry -> entry.getValue() == ctx.channel())
                .findFirst()
                .ifPresent(entry -> {
                    userChannelMap.remove(entry.getKey());
                    System.out.println("用户 " + entry.getKey() + " 因异常报错而被强制下线");
                });
        ctx.close();
    }
}
