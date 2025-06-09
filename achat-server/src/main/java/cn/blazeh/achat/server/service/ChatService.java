package cn.blazeh.achat.server.service;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto;
import cn.blazeh.achat.server.handler.AChatServerHandler;
import cn.blazeh.achat.server.manager.InboxManager;
import cn.blazeh.achat.server.manager.MessageManager;
import cn.blazeh.achat.server.manager.UserManager;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * 聊天服务类，负责处理消息的发送、存储和离线管理逻辑 <p>
 * 提供在线消息实时发送、用户状态验证等功能
 */
public class ChatService {

    private static final Logger LOGGER = LogManager.getLogger(ChatService.class);

    private final ConnectionService connectionService;
    private final InboxManager inboxManager;
    private final MessageManager messageManager;
    private final UserManager userManager;

    public ChatService(ConnectionService connectionService, InboxManager inboxManager, MessageManager messageManager, UserManager userManager) {
        this.connectionService = connectionService;
        this.inboxManager = inboxManager;
        this.messageManager = messageManager;
        this.userManager = userManager;
    }

    /**
     * 处理聊天消息的核心逻辑： <p>
     * 1. 若接收者在线：立即发送消息并返回消息ID <p>
     * 2. 若接收者不在线但已注册：存储为离线消息并返回消息ID  <p>
     * 3. 若接收者未注册：忽略消息并返回错误码
     *
     * @param builder 消息构建器，包含消息内容（发送者、接收者、消息体等）
     * @return 处理结果标识： <p>
     *         >0: 成功处理的消息ID <p>
     *         -1: 在线消息发送失败 <p>
     *         -2: 接收者未注册
     */
    public long processChat(Message.MessageBuilder builder) {
        return connectionService.getSessionId(builder.getReceiver()).map(sessionId -> {
            Message message = builder.build();
            messageManager.saveMessage(message);
            LOGGER.debug("接收者{}在线，立即发送消息", message.getReceiver());
            if(sendPrivateMessage(sessionId, message)) {
                LOGGER.debug("消息发送成功");
                return message.getMessageId();
            } else {
                LOGGER.debug("消息发送失败");
                return -1L;
            }
        }).orElseGet(() -> {
            if(userManager.hasRegistered(builder.getReceiver())) {
                Message message = builder.build();
                messageManager.saveMessage(message);
                inboxManager.addMessage(message.getReceiver(), message.getMessageId());
                LOGGER.debug("接收者{}不在线，已暂存于服务器", message.getReceiver());
                return message.getMessageId();
            } else {
                LOGGER.debug("接收者{}未注册，消息已忽略", builder.getReceiver());
                return -2L;
            }
        });
    }

    /**
     * 通过会话ID发送私聊消息
     *
     * @param sessionId 接收者的会话ID
     * @param msg 待发送的消息对象
     * @return 发送是否成功
     */
    public boolean sendPrivateMessage(UUID sessionId, Message msg) {
        return connectionService.getChannel(sessionId)
                .map(channel -> sendPrivateMessage(channel, msg))
                .orElse(false);
    }

    /**
     * 发送私聊消息
     *
     * @param channel Netty通信通道
     * @param msg 待发送的消息对象
     * @return 发送是否成功
     */
    public boolean sendPrivateMessage(Channel channel, Message msg) {
        if(channel == null || !channel.isActive())
            return false;
        channel.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                .setType(MessageProto.AChatType.CHAT)
                .setChat(msg.toAChatChat())
                .build()
        );
        LOGGER.debug("发送消息：{} -> {}: {}", msg.getSender(), msg.getReceiver(), msg.getContent());
        return true;
    }
}