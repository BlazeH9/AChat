package cn.blazeh.achat.client.manager;

import cn.blazeh.achat.client.handler.AChatClientHandler;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public enum ConnectionManager {

    INSTANCE;

    private final EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public Optional<ChannelFuture> connect(String host, int port) throws InterruptedException {
        if(isConnected())
            return Optional.empty();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(AChatEnvelope.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(new AChatClientHandler());
                    }
                });
        ChannelFuture future = bootstrap.connect(host, port).sync();
        future.addListener((ChannelFutureListener) f -> {
            SessionManager.INSTANCE.getSession().setChannel(future.channel());
            SessionManager.INSTANCE.getSession().setAuthState(Session.AuthState.READY);
            connected.set(true);
            System.out.println("已连接至服务器");
        });
        return Optional.of(future);
    }

    public void disconnect() {
        if(!isConnected())
            return;
        group.shutdownGracefully();
        SessionManager.INSTANCE.getSession().getChannel()
                .ifPresent(channel -> channel.closeFuture().syncUninterruptibly());
        SessionManager.INSTANCE.getSession().setChannel(null);
        SessionManager.INSTANCE.getSession().setAuthState(Session.AuthState.PREPARING);
        connected.set(false);
    }

    public boolean isConnected() {
        return connected.get();
    }

}
