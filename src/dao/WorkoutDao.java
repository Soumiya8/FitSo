package dao;

import model.Workout;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkoutDao {

    public int insert(Workout workout, Connection conn) throws SQLException {
        String sql = "INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes) " +
                     "VALUES (?, ?, TRUNC(?), ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"WORKOUT_ID"})) {
            stmt.setInt(1, workout.getUserId());
            stmt.setInt(2, workout.getTypeId());
            stmt.setDate(3, new java.sql.Date(workout.getWorkoutDate().getTime()));
            stmt.setInt(4, workout.getDurationMin());
            stmt.setString(5, workout.getIntensity());
            stmt.setInt(6, workout.getCaloriesBurned());
            stmt.setInt(7, workout.getEnergyEarned());
            stmt.setInt(8, workout.getCoinsEarned());
            stmt.setString(9, workout.getNotes());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Workout> findByUserId(int userId) throws SQLException {
        List<Workout> list = new ArrayList<>();
        String sql = "SELECT w.*, wt.type_name FROM workouts w " +
                     "JOIN workout_types wt ON w.type_id = wt.type_id " +
                     "WHERE w.user_id = ? ORDER BY w.workout_date DESC, w.workout_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapWorkout(rs));
                }
            }
        }
        return list;
    }

    public int countRewardedToday(int userId, Date date, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM workouts WHERE user_id = ? AND TRUNC(workout_date) = TRUNC(?) AND energy_earned > 0";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, new java.sql.Date(date.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countTotalWorkouts(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM workouts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getMaxSingleMinutes(int userId) throws SQLException {
        String sql = "SELECT NVL(MAX(duration_min), 0) FROM workouts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalCalories(int userId) throws SQLException {
        String sql = "SELECT NVL(SUM(calories_burned), 0) FROM workouts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Date> getDistinctWorkoutDates(int userId) throws SQLException {
        List<Date> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT TRUNC(workout_date) AS d FROM workouts WHERE user_id = ? ORDER BY d ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dates.add(rs.getDate("d"));
                }
            }
        }
        return dates;
    }

    public Date getLastWorkoutDate(int userId) throws SQLException {
        String sql = "SELECT MAX(TRUNC(workout_date)) AS max_date FROM workouts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("max_date");
                }
            }
        }
        return null;
    }

    // Report 1: Daily Calories for the past N days
    public Map<Date, Integer> getDailyCalories(int userId, int days) throws SQLException {
        Map<Date, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT TRUNC(workout_date) AS wdate, SUM(calories_burned) AS total_cal " +
                     "FROM workouts WHERE user_id = ? AND workout_date >= TRUNC(SYSDATE) - ? " +
                     "GROUP BY TRUNC(workout_date) ORDER BY wdate ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, days);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getDate("wdate"), rs.getInt("total_cal"));
                }
            }
        }
        return map;
    }

    // Report 2: Workouts per week for the past N weeks
    public Map<String, Integer> getWorkoutsPerWeek(int userId, int weeks) throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT TO_CHAR(TRUNC(workout_date, 'IW'), 'YYYY-MM-DD') AS week_start, COUNT(*) AS cnt " +
                     "FROM workouts WHERE user_id = ? AND workout_date >= TRUNC(SYSDATE) - (? * 7) " +
                     "GROUP BY TRUNC(workout_date, 'IW') ORDER BY week_start ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, weeks);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("week_start"), rs.getInt("cnt"));
                }
            }
        }
        return map;
    }

    private Workout mapWorkout(ResultSet rs) throws SQLException {
        Workout w = new Workout(
            rs.getInt("workout_id"),
            rs.getInt("user_id"),
            rs.getInt("type_id"),
            rs.getDate("workout_date"),
            rs.getInt("duration_min"),
            rs.getString("intensity"),
            rs.getInt("calories_burned"),
            rs.getInt("energy_earned"),
            rs.getInt("coins_earned"),
            rs.getString("notes")
        );
        try {
            w.setTypeName(rs.getString("type_name"));
        } catch (SQLException ignored) {}
        return w;
    }
}
