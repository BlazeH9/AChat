package cn.blazeh.achat.client.dao;

import cn.blazeh.achat.client.manager.DatabaseManager;
import cn.blazeh.achat.client.model.MessageFactory;
import cn.blazeh.achat.common.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private Connection getConnection() {
        return DatabaseManager.INSTANCE.getConnection();
    }

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

    public boolean deleteMessage(long messageId) throws SQLException {
        try(PreparedStatement ps = getConnection().prepareStatement(DELETE_SQL)) {
            ps.setLong(1, messageId);
            return ps.executeUpdate() > 0;
        }
    }

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

    public List<Message> selectUserMessages(String userId, int limit, int offset) throws SQLException {
        List<Message> messages = new ArrayList<>();
        try(PreparedStatement ps = getConnection().prepareStatement(SELECT_USER_SQL)) {
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

    public List<Message> selectConversationMessages(String user1, String user2, int limit, int offset) throws SQLException {
        List<Message> messages = new ArrayList<>();
        try(PreparedStatement ps = getConnection().prepareStatement(SELECT_CONVERSATION_SQL)) {
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

    public long getLatestMessageId() throws SQLException {
        try(Statement ps = getConnection().createStatement();
             ResultSet rs = ps.executeQuery(SELECT_LATEST_MESSAGE_ID_SQL)) {
            if(rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

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
