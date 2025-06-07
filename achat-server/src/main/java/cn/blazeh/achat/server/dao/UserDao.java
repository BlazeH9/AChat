package cn.blazeh.achat.server.dao;

import cn.blazeh.achat.server.manager.DatabaseManager;
import cn.blazeh.achat.server.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {

    private static final Logger LOGGER = LogManager.getLogger(UserDao.class);

    private static final String INSERT_SQL = "INSERT INTO user (user_id, password) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT * FROM user WHERE user_id = ?";
    private static final String UPDATE_SQL = "UPDATE user SET password = ? WHERE user_id = ?";

    private Connection getConnection() {
        return DatabaseManager.INSTANCE.getConnection();
    }

    public boolean insertUser(User user) {
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getPassword());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            LOGGER.error("存储用户{}的数据时出现异常", user.getUserId(), e);
            return false;
        }
    }

    public Optional<User> selectUser(String userId) {
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_SQL)) {
            ps.setString(1, userId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    return Optional.of(new User(rs.getString("user_id"), rs.getString("password")));
            }
            return Optional.empty();
        } catch(SQLException e) {
            LOGGER.error("查询用户{}的数据时出现异常", userId, e);
            return Optional.empty();
        }
    }

    public boolean updateUser(User user) {
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            LOGGER.error("更新用户{}的数据时出现异常", user.getUserId(), e);
            return false;
        }
    }

}
