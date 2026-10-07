package dao;

import model.User;
import util.DBConnection;

import java.sql.*;

public class UserDao {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public int insert(User user, Connection conn) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, salt, full_name, gender, birth_date, height_cm, created_on) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"USER_ID"})) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getGender());
            if (user.getBirthDate() != null) {
                stmt.setDate(7, new java.sql.Date(user.getBirthDate().getTime()));
            } else {
                stmt.setNull(7, Types.DATE);
            }
            stmt.setDouble(8, user.getHeightCm());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, full_name = ?, gender = ?, birth_date = ?, height_cm = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getGender());
            if (user.getBirthDate() != null) {
                stmt.setDate(4, new java.sql.Date(user.getBirthDate().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setDouble(5, user.getHeightCm());
            stmt.setInt(6, user.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("salt"),
            rs.getString("full_name"),
            rs.getString("gender"),
            rs.getDate("birth_date"),
            rs.getDouble("height_cm"),
            rs.getDate("created_on")
        );
    }
}
