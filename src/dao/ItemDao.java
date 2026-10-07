package dao;

import model.Item;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDao {

    public List<Item> findAllWithUserStatus(int userId) throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "  CASE WHEN ui.user_id IS NOT NULL THEN 1 ELSE 0 END AS is_owned, " +
                     "  NVL(ui.is_equipped, 'N') AS is_equipped " +
                     "FROM items i " +
                     "LEFT JOIN user_items ui ON i.item_id = ui.item_id AND ui.user_id = ? " +
                     "ORDER BY i.item_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Item item = mapItem(rs);
                    item.setOwned(rs.getInt("is_owned") == 1);
                    item.setEquipped("Y".equalsIgnoreCase(rs.getString("is_equipped")));
                    list.add(item);
                }
            }
        }
        return list;
    }

    public List<Item> getEquippedItems(int userId) throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT i.* FROM items i " +
                     "JOIN user_items ui ON i.item_id = ui.item_id " +
                     "WHERE ui.user_id = ? AND ui.is_equipped = 'Y'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Item item = mapItem(rs);
                    item.setOwned(true);
                    item.setEquipped(true);
                    list.add(item);
                }
            }
        }
        return list;
    }

    public Item findById(int itemId) throws SQLException {
        String sql = "SELECT * FROM items WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapItem(rs);
                }
            }
        }
        return null;
    }

    public void insertPurchase(int userId, int itemId, Connection conn) throws SQLException {
        String sql = "INSERT INTO user_items (user_id, item_id, purchased_on, is_equipped) VALUES (?, ?, SYSDATE, 'N')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        }
    }

    public void unequipSlot(int userId, String slot, Connection conn) throws SQLException {
        String sql = "UPDATE user_items SET is_equipped = 'N' " +
                     "WHERE user_id = ? AND item_id IN (SELECT item_id FROM items WHERE slot = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, slot);
            stmt.executeUpdate();
        }
    }

    public void setEquipped(int userId, int itemId, boolean equipped, Connection conn) throws SQLException {
        String sql = "UPDATE user_items SET is_equipped = ? WHERE user_id = ? AND item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, equipped ? "Y" : "N");
            stmt.setInt(2, userId);
            stmt.setInt(3, itemId);
            stmt.executeUpdate();
        }
    }

    private Item mapItem(ResultSet rs) throws SQLException {
        return new Item(
            rs.getInt("item_id"),
            rs.getString("item_name"),
            rs.getString("slot"),
            rs.getInt("price_coins"),
            rs.getString("image_file")
        );
    }
}
