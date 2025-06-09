package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.manager.MessageManager;
import cn.blazeh.achat.client.model.MessageFactory;
import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息服务管理类，处理临时消息的创建、存储和转换
 */
public class MessageService extends ClientService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger LOGGER = LogManager.getLogger(MessageService.class);
    /** 临时消息存储映射表 */
    private final Map<Long, Message> tempMessages = new ConcurrentHashMap<>();

    private static final MessageService INSTANCE = new MessageService();

    private MessageService() {}

    /**
     * 获取消息服务单例实例
     * @return 消息服务实例
     */
    public static MessageService getInstance() {
        return INSTANCE;
    }

    /**
     * 创建新的临时消息对象
     * @param receiverId 消息接收者ID
     * @param content 消息内容
     * @return 创建的临时消息对象
     */
    public Message newTempMessage(String receiverId, String content) {
        Message message = MessageFactory.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setSender(getSession().getUserId())
                .setReceiver(receiverId)
                .setType(MessageType.TEXT)
                .setContent(content)
                .build();
        addTempMessage(message);
        return message;
    }

    /**
     * 添加消息到临时存储
     * @param message 要添加的消息对象
     */
    public void addTempMessage(Message message) {
        tempMessages.put(message.getMessageId(), message);
    }

    /**
     * 从临时存储中移除消息
     * @param messageId 要移除的消息ID
     */
    public void removeTempMessage(long messageId) {
        tempMessages.remove(messageId);
    }

    /**
     * 保存临时消息到持久化存储
     * @param orgMsgId 原始消息ID
     * @param newMsgId 新分配的消息ID
     */
    public void saveTempMessage(long orgMsgId, long newMsgId) {
        Message msg = tempMessages.get(orgMsgId);
        if(msg == null)
            return;
        boolean result = MessageManager.INSTANCE.saveMessage(MessageFactory.newBuilder()
                .setMessageId(newMsgId)
                .setTimestamp(msg.getTimestamp())
                .setSender(msg.getSender())
                .setReceiver(msg.getReceiver())
                .setType(msg.getType())
                .setContent(msg.getContent())
                .build()
        );
        if(result) {
            tempMessages.remove(orgMsgId);
            LOGGER.debug("消息已保存，原ID：{}，新ID：{}", orgMsgId, newMsgId);
        } else {
            LOGGER.warn("消息保存失败，ID：{}", orgMsgId);
        }
    }

    /**
     * 将时间戳转换为可读格式
     * @param timestamp 要转换的时间戳
     * @return 格式化后的日期时间字符串
     */
    public static String parseTimestamp(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

}