package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.manager.MessageManager;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 消息服务，单例模式，负责消息的收发
 */
public class ChatService extends ClientService {

    private static final Logger LOGGER = LogManager.getLogger(ChatService.class);

    private static final ChatService INSTANCE = new ChatService();

    private ChatService() {}

    public static ChatService getInstance() {
        return INSTANCE;
    }

    /**
     * 接收消息
     * @param message 接收到的消息
     */
    public void receive(Message message) {
        LOGGER.info("[{}]<{}> {}", MessageService.parseTimestamp(message.getTimestamp()), message.getSender(), message.getContent());

        MessageManager.INSTANCE.saveMessage(message);
    }

    /**
     * 发送消息
     * @param message 要发送的消息
     */
    public void sendMessage(Message message) {
        if(!getSession().getAuthState().equals(Session.AuthState.DONE)) {
            LOGGER.warn("尚未完成登录，消息发送失败");
            return;
        }
        writeAndFlush(getAChatEnvelopeBuilder()
                .setType(AChatType.CHAT)
                .setChat(message.toAChatChat())
        );
    }

}
