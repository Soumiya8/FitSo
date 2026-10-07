package dao;

import model.BodyMeasurement;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MeasurementDao {

    public boolean saveOrUpdate(BodyMeasurement m, Connection conn) throws SQLException {
        // First check if measurement exists for user today
        String checkSql = "SELECT measurement_id FROM body_measurements WHERE user_id = ? AND TRUNC(measured_on) = TRUNC(?)";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, m.getUserId());
            checkStmt.setDate(2, new java.sql.Date(m.getMeasuredOn().getTime()));
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Update existing
                    int existingId = rs.getInt("measurement_id");
                    String updateSql = "UPDATE body_measurements SET weight_kg = ?, waist_cm = ?, chest_cm = ?, hips_cm = ? WHERE measurement_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, m.getWeightKg());
                        setOptionalDouble(updateStmt, 2, m.getWaistCm());
                        setOptionalDouble(updateStmt, 3, m.getChestCm());
                        setOptionalDouble(updateStmt, 4, m.getHipsCm());
                        updateStmt.setInt(5, existingId);
                        return updateStmt.executeUpdate() > 0;
                    }
                }
            }
        }

        // Insert new
        String insertSql = "INSERT INTO body_measurements (user_id, measured_on, weight_kg, waist_cm, chest_cm, hips_cm) VALUES (?, TRUNC(?), ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, m.getUserId());
            insertStmt.setDate(2, new java.sql.Date(m.getMeasuredOn().getTime()));
            insertStmt.setDouble(3, m.getWeightKg());
            setOptionalDouble(insertStmt, 4, m.getWaistCm());
            setOptionalDouble(insertStmt, 5, m.getChestCm());
            setOptionalDouble(insertStmt, 6, m.getHipsCm());
            return insertStmt.executeUpdate() > 0;
        }
    }

    public List<BodyMeasurement> findByUserId(int userId) throws SQLException {
        List<BodyMeasurement> list = new ArrayList<>();
        String sql = "SELECT * FROM body_measurements WHERE user_id = ? ORDER BY measured_on DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMeasurement(rs));
                }
            }
        }
        return list;
    }

    public Double findLatestWeight(int userId) throws SQLException {
        String sql = "SELECT weight_kg FROM body_measurements WHERE user_id = ? ORDER BY measured_on DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("weight_kg");
                }
            }
        }
        return null;
    }

    public int countTotalMeasurements(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM body_measurements WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // Report 3: Weight over time
    public Map<Date, Double> getWeightHistory(int userId) throws SQLException {
        Map<Date, Double> map = new LinkedHashMap<>();
        String sql = "SELECT TRUNC(measured_on) AS mdate, weight_kg FROM body_measurements WHERE user_id = ? ORDER BY mdate ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getDate("mdate"), rs.getDouble("weight_kg"));
                }
            }
        }
        return map;
    }

    private void setOptionalDouble(PreparedStatement stmt, int idx, Double val) throws SQLException {
        if (val != null) {
            stmt.setDouble(idx, val);
        } else {
            stmt.setNull(idx, Types.NUMERIC);
        }
    }

    private BodyMeasurement mapMeasurement(ResultSet rs) throws SQLException {
        double waist = rs.getDouble("waist_cm");
        Double waistObj = rs.wasNull() ? null : waist;
        double chest = rs.getDouble("chest_cm");
        Double chestObj = rs.wasNull() ? null : chest;
        double hips = rs.getDouble("hips_cm");
        Double hipsObj = rs.wasNull() ? null : hips;

        return new BodyMeasurement(
            rs.getInt("measurement_id"),
            rs.getInt("user_id"),
            rs.getDate("measured_on"),
            rs.getDouble("weight_kg"),
            waistObj,
            chestObj,
            hipsObj
        );
    }
}
