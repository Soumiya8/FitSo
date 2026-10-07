package dao;

import model.Goal;
import util.DBConnection;

import java.sql.*;

public class GoalDao {

    public Goal findActiveGoal(int userId) throws SQLException {
        String sql = "SELECT * FROM goals WHERE user_id = ? AND is_active = 'Y' ORDER BY start_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapGoal(rs);
                }
            }
        }
        return null;
    }

    public boolean saveOrUpdateGoal(Goal goal) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Deactivate previous active goals for user
                String deactivateSql = "UPDATE goals SET is_active = 'N' WHERE user_id = ?";
                try (PreparedStatement deactStmt = conn.prepareStatement(deactivateSql)) {
                    deactStmt.setInt(1, goal.getUserId());
                    deactStmt.executeUpdate();
                }

                // Insert new active goal
                String insertSql = "INSERT INTO goals (user_id, goal_type, target_weight_kg, weekly_workouts, start_date, is_active) VALUES (?, ?, ?, ?, SYSDATE, 'Y')";
                try (PreparedStatement insStmt = conn.prepareStatement(insertSql)) {
                    insStmt.setInt(1, goal.getUserId());
                    insStmt.setString(2, goal.getGoalType());
                    if (goal.getTargetWeightKg() != null) {
                        insStmt.setDouble(3, goal.getTargetWeightKg());
                    } else {
                        insStmt.setNull(3, Types.NUMERIC);
                    }
                    insStmt.setInt(4, goal.getWeeklyWorkouts());
                    insStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private Goal mapGoal(ResultSet rs) throws SQLException {
        double tw = rs.getDouble("target_weight_kg");
        Double targetWeight = rs.wasNull() ? null : tw;

        return new Goal(
            rs.getInt("goal_id"),
            rs.getInt("user_id"),
            rs.getString("goal_type"),
            targetWeight,
            rs.getInt("weekly_workouts"),
            rs.getDate("start_date"),
            "Y".equals(rs.getString("is_active"))
        );
    }
}
