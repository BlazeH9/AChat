package cn.blazeh.achat.client;

import cn.blazeh.achat.common.MessageProto.AChatMessage;
import cn.blazeh.achat.common.MessageProto.MessageType;
import cn.blazeh.achat.client.handler.AChatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AChatClient {

    private final String uid;
    private Channel channel;

    private final AChatMessage heartbeat;


    public AChatClient(String uid) {
        this.uid = uid;

        heartbeat = AChatMessage.newBuilder()
                .setMessageId(System.currentTimeMillis())
                .setSenderId(uid)
                .setReceiverId("server")
                .setType(MessageType.HEARTBEAT)
                .setContent("ping")
                .setTimestamp(System.currentTimeMillis())
                .build();
    }

    public void run() throws InterruptedException, IOException {
        EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        try {
            connect(group);
            System.out.println("已连接至服务器，你的id为：" + uid);
            System.out.println("用法：<用户id> <消息>");
            startHeartbeat();
            processUserInput();
        } finally {
            group.shutdownGracefully();
        }
    }

    private void connect(EventLoopGroup group) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(AChatMessage.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(new AChatClientHandler());
                    }
                });
        ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
        channel = future.channel();
    }

    private void startHeartbeat() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (channel != null && channel.isActive())
                channel.writeAndFlush(heartbeat);
        }, 0, 30, TimeUnit.SECONDS);
    }

    private void processUserInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = reader.readLine();
            if (line == null || line.isEmpty() || line.equals("exit"))
                break;
            String[] args = line.split("\\s+", 2);
            if (args.length <= 1) {
                System.out.println("格式错误，用法：<用户id> <消息>");
            } else {
                channel.writeAndFlush(AChatMessage.newBuilder()
                        .setMessageId(System.currentTimeMillis())
                        .setSenderId(uid)
                        .setReceiverId(args[0])
                        .setType(MessageType.TEXT)
                        .setContent(args[1])
                        .setTimestamp(System.currentTimeMillis())
                        .build()
                );
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new AChatClient(args[0]).run();
    }

}
