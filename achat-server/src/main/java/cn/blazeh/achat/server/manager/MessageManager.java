package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.server.dao.MessageDao;

import java.util.Optional;

public class MessageManager {

    private final MessageDao messageDao;

    public MessageManager(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public void saveMessage(Message message) {
        messageDao.insertMessage(message);
    }

    public Optional<Message> getMessage(long id) {
        return messageDao.selectMessage(id);
    }

}
