package dao;

import model.PlayerState;
import util.DBConnection;

import java.sql.*;

public class PlayerStateDao {

    public PlayerState findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM player_state WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPlayerState(rs);
                }
            }
        }
        return null;
    }

    public void insert(PlayerState state, Connection conn) throws SQLException {
        String sql = "INSERT INTO player_state (user_id, total_energy, coins, pet_name) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, state.getUserId());
            stmt.setInt(2, state.getTotalEnergy());
            stmt.setInt(3, state.getCoins());
            stmt.setString(4, state.getPetName() != null ? state.getPetName() : "Buddy");
            stmt.executeUpdate();
        }
    }

    public void addEnergyAndCoins(int userId, int energyDelta, int coinsDelta, Connection conn) throws SQLException {
        String sql = "UPDATE player_state SET total_energy = total_energy + ?, coins = coins + ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, energyDelta);
            stmt.setInt(2, coinsDelta);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        }
    }

    public boolean deductCoins(int userId, int coinsAmount, Connection conn) throws SQLException {
        String sql = "UPDATE player_state SET coins = coins - ? WHERE user_id = ? AND coins >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, coinsAmount);
            stmt.setInt(2, userId);
            stmt.setInt(3, coinsAmount);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePetName(int userId, String petName) throws SQLException {
        String sql = "UPDATE player_state SET pet_name = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, petName);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    private PlayerState mapPlayerState(ResultSet rs) throws SQLException {
        return new PlayerState(
            rs.getInt("user_id"),
            rs.getInt("total_energy"),
            rs.getInt("coins"),
            rs.getString("pet_name")
        );
    }
}
