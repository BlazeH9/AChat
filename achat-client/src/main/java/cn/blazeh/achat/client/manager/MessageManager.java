package cn.blazeh.achat.client.manager;

import cn.blazeh.achat.client.dao.MessageDao;
import cn.blazeh.achat.common.model.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum MessageManager {

    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger(MessageManager.class);

    private final MessageDao dao;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private static final int DEFAULT_LIMIT = 50;

    private volatile long latestMessageId;

    MessageManager() {
        this.dao = new MessageDao();
        this.latestMessageId = readOperation(dao::getLatestMessageId, 0L, "获取最新消息ID时出现异常");
    }

    public long getLatestMessageId() {
        return latestMessageId;
    }

    private <T> T readOperation(Callable<T> callable, T defaultValue, String error, Object... args) {
        try {
            lock.readLock().lock();
            return callable.call();
        } catch(Exception e) {
            LOGGER.error(error, args, e);
            return defaultValue;
        } finally {
            lock.readLock().unlock();
        }
    }

    private <T> T writeOperation(Callable<T> callable, T defaultValue, String error, Object... args) {
        try {
            lock.writeLock().lock();
            return callable.call();
        } catch(Exception e) {
            LOGGER.error(error, args, e);
            return defaultValue;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean saveMessage(Message message) {
        return writeOperation(() -> {
            boolean result = dao.insertMessage(message);
            if(result && message.getMessageId() > latestMessageId)
                latestMessageId = message.getMessageId();
            return result;
        }, false, "消息{}保存时出现异常", message.getMessageId());
    }

    public boolean deleteMessage(long messageId) {
        return writeOperation(() -> dao.deleteMessage(messageId),
                false, "消息{}删除时出现异常", messageId
        );
    }

    public Optional<Message> getMessageById(long messageId) {
        return readOperation(() -> dao.selectMessageById(messageId),
                Optional.empty(), "通过ID查找消息{}时出现异常", messageId
        );
    }

    public List<Message> getUserMessages(String userId) {
        return getUserMessages(userId, DEFAULT_LIMIT, 0);
    }

    public List<Message> getUserMessages(String userId, int limit, int offset) {
        return readOperation(() -> dao.selectUserMessages(userId, limit, offset),
                List.of(), "查找用户{}的消息时出现异常", userId
        );
    }

    public List<Message> getConversationMessages(String user1, String user2) {
        return getConversationMessages(user1, user2, DEFAULT_LIMIT, 0);
    }

    public List<Message> getConversationMessages(String user1, String user2, int limit, int offset) {
        return readOperation(() -> dao.selectConversationMessages(user1, user2, limit, offset),
                List.of(), "查找用户{}与用户{}之间的消息时出现异常", user1, user2
        );
    }

    public List<String> getContacts() {
        return readOperation(dao::getContacts, List.of(), "获取联系人列表时出现异常");
    }

}
