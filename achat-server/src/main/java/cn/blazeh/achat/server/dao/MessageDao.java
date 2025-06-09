package cn.blazeh.achat.server.dao;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.server.manager.DatabaseManager;
import cn.blazeh.achat.server.model.MessageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 消息数据访问对象，提供消息的持久化存储和查询功能。
 * 处理消息记录的增删改查操作
 */
public class MessageDao {

    private static final String INSERT_SQL = "INSERT INTO message " +
            "(message_id, timestamp, sender, receiver, type, content) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ID_SQL = "SELECT * FROM message WHERE message_id = ?";
    private static final String SELECT_MAX_ID_SQL = "SELECT MAX(message_id) FROM message";

    private static final Logger LOGGER = LogManager.getLogger(MessageDao.class);

    private Connection getConnection() {
        return DatabaseManager.INSTANCE.getConnection();
    }

    /**
     * 插入新消息记录
     * @param message 要存储的消息对象
     * @return 插入成功返回true，否则false
     */
    public boolean insertMessage(Message message) {
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setLong(1, message.getMessageId());
            ps.setLong(2, message.getTimestamp());
            ps.setString(3, message.getSender());
            ps.setString(4, message.getReceiver());
            ps.setInt(5, message.getType().getNumber());
            ps.setString(6, message.getContent());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            LOGGER.error("消息{}存储时出现异常", message.getMessageId(), e);
            return false;
        }
    }

    /**
     * 根据消息ID查询消息记录
     * @param id 要查询的消息ID
     * @return 消息实例
     */
    public Optional<Message> selectMessage(long id) {
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(toMessage(rs));
                return Optional.empty();
            }
        } catch(SQLException e) {
            LOGGER.error("读取消息{}时出现异常", id, e);
            return Optional.empty();
        }
    }

    /**
     * 获取当前最大消息ID值
     * @return 当前最大消息ID，无记录时返回0
     */
    public long getMaxId() {
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_MAX_ID_SQL)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getLong(1);
                return 0;
            }
        } catch(SQLException e) {
            LOGGER.error("查询最大消息ID时出现异常", e);
            return 0;
        }
    }

    /**
     * 将结果集转换为消息对象
     * @param rs 数据库查询结果集
     * @return 转换后的消息对象
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

}