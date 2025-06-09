package cn.blazeh.achat.client.manager;

import cn.blazeh.achat.client.AChatClient;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 连接Manager，单例模式，负责网络连接的建立、断开和状态管理
 */
public enum ConnectionManager {

    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger(ConnectionManager.class);

    private final EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    private final AtomicBoolean connected = new AtomicBoolean(false);

    /**
     * 连接到指定服务器
     * @param client 聊天客户端实例
     * @param host 服务器主机地址
     * @param port 服务器端口号
     * @return 连接结果的ChannelFuture
     */
    public Optional<ChannelFuture> connect(AChatClient client, String host, int port) throws InterruptedException {
        if(isConnected())
            return Optional.empty();
        LOGGER.info("正在连接服务器 {}:{}", host, port);
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
                                .addLast(new AChatClientHandler(client));
                    }
                });
        ChannelFuture future = bootstrap.connect(host, port).sync();
        future.addListener((ChannelFutureListener) f -> {
            SessionManager.INSTANCE.getSession().setChannel(future.channel());
            SessionManager.INSTANCE.getSession().setAuthState(Session.AuthState.READY);
            connected.set(true);
            LOGGER.info("已连接至服务器 {}:{}", host, port);
        });
        return Optional.of(future);
    }

    /**
     * 断开当前连接
     */
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

    /**
     * 检查当前连接状态
     * @return 是否已建立连接
     */
    public boolean isConnected() {
        return connected.get();
    }

}