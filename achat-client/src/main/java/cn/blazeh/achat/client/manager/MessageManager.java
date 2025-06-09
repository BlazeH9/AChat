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

/**
 * 消息Manager，单例模式，负责消息的存储、删除和查询操作
 */
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

    /**
     * 获取最新消息ID
     * @return 当前最新消息ID
     */
    public long getLatestMessageId() {
        return latestMessageId;
    }

    /**
     * 通用读方法
     * @param callable 读取操作
     * @param defaultValue 出现异常时的默认值
     * @param error 日志中的错误信息
     * @param args 日志错误信息参数
     * @return 读取到的内容
     * @param <T> 读取内容的类型
     */
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

    /**
     * 通用写方法
     * @param callable 写入操作
     * @param defaultValue 出现异常时的默认值
     * @param error 日志中的错误信息
     * @param args 日志错误信息参数
     * @return 写入操作的结果
     * @param <T> 写入操作返回值类型
     */
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


    /**
     * 保存消息
     * @param message 待保存的消息对象
     * @return 是否保存成功
     */
    public boolean saveMessage(Message message) {
        return writeOperation(() -> {
            boolean result = dao.insertMessage(message);
            if(result && message.getMessageId() > latestMessageId)
                latestMessageId = message.getMessageId();
            return result;
        }, false, "消息{}保存时出现异常", message.getMessageId());
    }

    /**
     * 删除消息
     * @param messageId 待删除消息的ID
     * @return 是否删除成功
     */
    public boolean deleteMessage(long messageId) {
        return writeOperation(() -> dao.deleteMessage(messageId),
                false, "消息{}删除时出现异常", messageId
        );
    }

    /**
     * 通过ID查询消息
     * @param messageId 目标消息ID
     * @return 消息对象
     */
    public Optional<Message> getMessageById(long messageId) {
        return readOperation(() -> dao.selectMessageById(messageId),
                Optional.empty(), "通过ID查找消息{}时出现异常", messageId
        );
    }

    /**
     * 获取用户消息（默认限制）
     * @param userId 目标用户ID
     * @return 消息列表
     */
    public List<Message> getUserMessages(String userId) {
        return getUserMessages(userId, DEFAULT_LIMIT, 0);
    }

    /**
     * 获取用户消息（自定义分页）
     * @param userId 目标用户ID
     * @param limit 返回消息最大数量
     * @param offset 消息偏移量
     * @return 消息列表
     */
    public List<Message> getUserMessages(String userId, int limit, int offset) {
        return readOperation(() -> dao.selectUserMessages(userId, limit, offset),
                List.of(), "查找用户{}的消息时出现异常", userId
        );
    }

    /**
     * 获取会话消息（默认限制）
     * @param user1 用户1的ID
     * @param user2 用户2的ID
     * @return 消息列表
     */
    public List<Message> getConversationMessages(String user1, String user2) {
        return getConversationMessages(user1, user2, DEFAULT_LIMIT, 0);
    }

    /**
     * 获取会话消息（自定义分页）
     * @param user1 用户1的ID
     * @param user2 用户2的ID
     * @param limit 返回消息最大数量
     * @param offset 消息偏移量
     * @return 消息列表
     */
    public List<Message> getConversationMessages(String user1, String user2, int limit, int offset) {
        return readOperation(() -> dao.selectConversationMessages(user1, user2, limit, offset),
                List.of(), "查找用户{}与用户{}之间的消息时出现异常", user1, user2
        );
    }

    /**
     * 获取联系人列表
     * @return 联系人ID列表
     */
    public List<String> getContacts() {
        return readOperation(dao::getContacts, List.of(), "获取联系人列表时出现异常");
    }

}