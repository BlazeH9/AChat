package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.server.dao.MessageDao;
import cn.blazeh.achat.server.model.Message;

import java.util.Optional;

public enum MessageManager {

    INSTANCE;

    public void addMessage(Message message) {
        MessageDao.insertMessage(message);
    }

    public Optional<Message> getMessage(long id) {
        return MessageDao.selectMessage(id);
    }

}
