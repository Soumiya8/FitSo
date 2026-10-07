package dao;

import model.WorkoutType;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutTypeDao {

    public List<WorkoutType> findAll() throws SQLException {
        List<WorkoutType> list = new ArrayList<>();
        String sql = "SELECT * FROM workout_types ORDER BY type_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapWorkoutType(rs));
            }
        }
        return list;
    }

    public WorkoutType findById(int typeId) throws SQLException {
        String sql = "SELECT * FROM workout_types WHERE type_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, typeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapWorkoutType(rs);
                }
            }
        }
        return null;
    }

    private WorkoutType mapWorkoutType(ResultSet rs) throws SQLException {
        return new WorkoutType(
            rs.getInt("type_id"),
            rs.getString("type_name"),
            rs.getDouble("met_value")
        );
    }
}
