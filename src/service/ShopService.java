package service;

import dao.ItemDao;
import dao.PlayerStateDao;
import model.Item;
import model.PlayerState;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ShopService {

    private final ItemDao itemDao = new ItemDao();
    private final PlayerStateDao playerStateDao = new PlayerStateDao();

    public List<Item> getShopItems(int userId) throws SQLException {
        return itemDao.findAllWithUserStatus(userId);
    }

    public boolean purchaseItem(int userId, int itemId) throws SQLException, IllegalArgumentException {
        Item item = itemDao.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Deduct coins atomically
                boolean success = playerStateDao.deductCoins(userId, item.getPriceCoins(), conn);
                if (!success) {
                    conn.rollback();
                    throw new IllegalArgumentException("Insufficient coins balance! Workout to earn more coins.");
                }

                // Insert purchase record
                itemDao.insertPurchase(userId, itemId, conn);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean toggleEquipItem(int userId, int itemId, String slot, boolean currentlyEquipped) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (currentlyEquipped) {
                    // Unequip item
                    itemDao.setEquipped(userId, itemId, false, conn);
                } else {
                    // Unequip any item currently equipped in this slot first (1 item per slot rule)
                    itemDao.unequipSlot(userId, slot, conn);
                    // Equip new item
                    itemDao.setEquipped(userId, itemId, true, conn);
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
