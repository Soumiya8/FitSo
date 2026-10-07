package dao;

import model.Mission;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MissionDao {

    public List<Mission> findAll() throws SQLException {
        List<Mission> list = new ArrayList<>();
        String sql = "SELECT * FROM missions ORDER BY mission_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapMission(rs));
            }
        }
        return list;
    }

    public Set<Integer> findCompletedMissionIds(int userId) throws SQLException {
        Set<Integer> set = new HashSet<>();
        String sql = "SELECT mission_id FROM user_missions WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    set.add(rs.getInt("mission_id"));
                }
            }
        }
        return set;
    }

    public void insertCompletion(int userId, int missionId, Connection conn) throws SQLException {
        String sql = "INSERT INTO user_missions (user_id, mission_id, completed_on) VALUES (?, ?, SYSDATE)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, missionId);
            stmt.executeUpdate();
        }
    }

    private Mission mapMission(ResultSet rs) throws SQLException {
        return new Mission(
            rs.getInt("mission_id"),
            rs.getString("title"),
            rs.getString("metric_type"),
            rs.getInt("target_value"),
            rs.getInt("reward_energy"),
            rs.getInt("reward_coins")
        );
    }
}
