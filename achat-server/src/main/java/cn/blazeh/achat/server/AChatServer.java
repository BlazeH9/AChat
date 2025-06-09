package cn.blazeh.achat.server;

import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import cn.blazeh.achat.server.dao.MessageDao;
import cn.blazeh.achat.server.dao.UserDao;
import cn.blazeh.achat.server.handler.AChatServerHandler;
import cn.blazeh.achat.server.manager.*;
import cn.blazeh.achat.server.service.*;
import cn.blazeh.achat.server.util.IdGenerator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;

/**
 * 服务端主类
 */
public class AChatServer {

    private static final Logger LOGGER = LogManager.getLogger(AChatServer.class);
    private static final int PORT = 8080;

    private MessageDao messageDao;
    private UserDao userDao;

    private ChannelManager channelManager;
    private InboxManager inboxManager;
    private MessageManager messageManager;
    private SessionManager sessionManager;
    private UserManager userManager;

    private AuthService authService;
    private ChatService chatService;
    private ConnectionService connectionService;
    private UserService userService;

    private static final String BANNER = """
            
                  ___           ___           ___           ___                \s
                 /  /\\         /  /\\         /__/\\         /  /\\          ___  \s
                /  /::\\       /  /:/         \\  \\:\\       /  /::\\        /  /\\ \s
               /  /:/\\:\\     /  /:/           \\__\\:\\     /  /:/\\:\\      /  /:/ \s
              /  /:/~/::\\   /  /:/  ___   ___ /  /::\\   /  /:/~/::\\    /  /:/  \s
             /__/:/ /:/\\:\\ /__/:/  /  /\\ /__/\\  /:/\\:\\ /__/:/ /:/\\:\\  /  /::\\  \s
             \\  \\:\\/:/__\\/ \\  \\:\\ /  /:/ \\  \\:\\/:/__\\/ \\  \\:\\/:/__\\/ /__/:/\\:\\ \s
              \\  \\::/       \\  \\:\\  /:/   \\  \\::/       \\  \\::/      \\__\\/  \\:\\\s
               \\  \\:\\        \\  \\:\\/:/     \\  \\:\\        \\  \\:\\           \\  \\:\\
                \\  \\:\\        \\  \\::/       \\  \\:\\        \\  \\:\\           \\__\\/
                 \\__\\/         \\__\\/         \\__\\/         \\__\\/               \s
            
                2025 AChat - A Simple Chat Server\s
            
            """;

    /**
     * 初始化DAO层
     */
    public void initDao() {
        LOGGER.info("正在加载数据库，地址：{}", DatabaseManager.INSTANCE.getUrl());
        messageDao = new MessageDao();
        userDao = new UserDao();
        IdGenerator.setCounter(messageDao.getMaxId());
        LOGGER.info("数据库加载完成");
    }

    /**
     * 初始化Manager层
     */
    public void initManager() {
        channelManager = new ChannelManager();
        inboxManager = new InboxManager();
        messageManager = new MessageManager(messageDao);
        sessionManager = new SessionManager();
        userManager = new UserManager(userDao);
        LOGGER.info("管理器加载完成");
    }

    /**
     * 初始化Service层
     */
    public void initService() {
        connectionService = new ConnectionService(channelManager, sessionManager);
        authService = new AuthService(userManager, sessionManager);
        chatService = new ChatService(connectionService, inboxManager, messageManager, userManager);
        userService = new UserService(inboxManager, userManager, sessionManager, messageManager);
        LOGGER.info("服务加载完成");
    }

    /**
     * 启动服务器
     * @throws Exception
     */
    public void start() throws Exception {
        EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(0, NioIoHandler.newFactory());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(AChatEnvelope.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new AChatServerHandler(authService, chatService, connectionService, userService));
                        }
                    });
            ChannelFuture future = bootstrap.bind(PORT).sync();
            LOGGER.info("服务器已开启，监听端口：{}", PORT);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info("服务器启动中");
        System.out.println(BANNER);
        AChatServer server = new AChatServer();
        server.initDao();
        server.initManager();
        server.initService();
        server.start();
    }
}
