package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.server.dao.MessageDao;

import java.util.Optional;

/**
 * 消息Manager，通过MessageDao完成消息的增查
 */
public class MessageManager {

    private final MessageDao messageDao;

    public MessageManager(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    /**
     * 将消息持久化到数据库中
     * @param message 消息
     */
    public void saveMessage(Message message) {
        messageDao.insertMessage(message);
    }

    /**
     * 通过消息ID获取消息
     * @param id 消息ID
     * @return 与之对应的消息
     */
    public Optional<Message> getMessage(long id) {
        return messageDao.selectMessage(id);
    }

}
