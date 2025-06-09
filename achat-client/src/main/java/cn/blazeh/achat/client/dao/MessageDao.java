package cn.blazeh.achat.client.dao;

import cn.blazeh.achat.client.manager.DatabaseManager;
import cn.blazeh.achat.client.model.MessageFactory;
import cn.blazeh.achat.common.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 消息数据访问对象，提供消息的数据库操作接口，实现消息的增删查改功能
 */
public class MessageDao {

    private static final String INSERT_SQL =
            "INSERT INTO messages (message_id, timestamp, sender, receiver, type, content) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String DELETE_SQL =
            "DELETE FROM messages WHERE message_id = ?";

    private static final String SELECT_ID_SQL =
            "SELECT message_id FROM messages WHERE message_id = ?";

    private static final String SELECT_USER_SQL =
            "SELECT * FROM messages " +
                    "WHERE sender = ? OR receiver = ? " +
                    "ORDER BY message_id DESC " +
                    "LIMIT ? OFFSET ?";

    private static final String SELECT_CONVERSATION_SQL =
            "SELECT * FROM messages " +
                    "WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                    "ORDER BY message_id DESC " +
                    "LIMIT ? OFFSET ?";

    private static final String SELECT_LATEST_MESSAGE_ID_SQL =
            "SELECT MAX(message_id) FROM messages";

    private static final String SELECT_CONTACTS_SQL = "SELECT sender FROM messages UNION SELECT receiver FROM messages;";

    /**
     * 获取数据库连接
     * @return 数据库连接对象
     */
    private Connection getConnection() {
        return DatabaseManager.INSTANCE.getConnection();
    }

    /**
     * 结果集转消息对象
     * @param rs 查询结果集
     * @return 消息对象实例
     */
    private Message toMessage(ResultSet rs) throws SQLException {
        return MessageFactory.newBuilder()
                .setMessageId(rs.getLong("message_id"))
                .setTimestamp(rs.getLong("timestamp"))
                .setSender(rs.getString("sender"))
                .setReceiver(rs.getString("receiver"))
                .setType(MessageFactory.getType(rs.getInt("type")))
                .setContent(rs.getString("content"))
                .build();
    }

    /**
     * 插入新消息
     * @param message 待插入的消息对象
     * @return 插入操作是否成功
     */
    public boolean insertMessage(Message message) throws SQLException {
        try(PreparedStatement ps = getConnection().prepareStatement(INSERT_SQL)) {
            ps.setLong(1, message.getMessageId());
            ps.setLong(2, message.getTimestamp());
            ps.setString(3, message.getSender());
            ps.setString(4, message.getReceiver());
            ps.setInt(5, message.getType().getNumber());
            ps.setString(6, message.getContent());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 删除指定消息
     * @param messageId 目标消息ID
     * @return 删除操作是否成功
     */
    public boolean deleteMessage(long messageId) throws SQLException {
        try(PreparedStatement ps = getConnection().prepareStatement(DELETE_SQL)) {
            ps.setLong(1, messageId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 通过ID查询消息
     * @param messageId 目标消息ID
     * @return 消息对象的Optional封装
     */
    public Optional<Message> selectMessageById(long messageId) throws SQLException {
        try(PreparedStatement ps = getConnection().prepareStatement(SELECT_ID_SQL)) {
            ps.setLong(1, messageId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    return Optional.of(toMessage(rs));
            }
        }
        return Optional.empty();
    }

    /**
     * 查询用户相关消息
     * @param userId 目标用户ID
     * @param limit 返回消息最大数量
     * @param offset 消息偏移量
     * @return 消息列表
     */
    public List<Message> selectUserMessages(String userId, int limit, int offset) throws SQLException {
        List<Message> messages = new ArrayList<>();
        try(PreparedStatement ps = getConnection().prepareStatement(SELECT_USER_SQL)) {
            // 设置查询参数
            ps.setString(1, userId);
            ps.setString(2, userId);
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    messages.add(toMessage(rs));
                }
            }
        }
        return messages;
    }

    /**
     * 查询会话消息
     * @param user1 用户1的ID
     * @param user2 用户2的ID
     * @param limit 返回消息最大数量
     * @param offset 消息偏移量
     * @return 消息列表
     */
    public List<Message> selectConversationMessages(String user1, String user2, int limit, int offset) throws SQLException {
        List<Message> messages = new ArrayList<>();
        try(PreparedStatement ps = getConnection().prepareStatement(SELECT_CONVERSATION_SQL)) {
            // 设置双向查询参数
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);
            ps.setInt(5, limit);
            ps.setInt(6, offset);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    messages.add(toMessage(rs));
                }
            }
        }
        return messages;
    }

    /**
     * 获取最新消息ID
     * @return 当前最大消息ID
     */
    public long getLatestMessageId() throws SQLException {
        try(Statement ps = getConnection().createStatement();
            ResultSet rs = ps.executeQuery(SELECT_LATEST_MESSAGE_ID_SQL)) {
            if(rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    /**
     * 获取所有联系人
     * @return 联系人ID列表
     */
    public List<String> getContacts() throws SQLException {
        List<String> contacts = new ArrayList<>();
        try(PreparedStatement ps = getConnection().prepareStatement(SELECT_CONTACTS_SQL)) {
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    contacts.add(rs.getString(1));
                }
            }
        }
        return contacts;
    }

}