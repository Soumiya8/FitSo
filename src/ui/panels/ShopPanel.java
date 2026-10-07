package ui.panels;

import model.Item;
import model.PlayerState;
import service.ShopService;
import ui.components.Theme;
import util.ImageCache;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * FitSo AAA Shop & Customization Screen
 */
public class ShopPanel extends JPanel {

    private final ShopService shopService = new ShopService();
    private JLabel coinsBalanceLabel;
    private JPanel itemsGridContainer;

    private final Runnable onShopStateChangedCallback;

    public ShopPanel(Runnable onShopStateChangedCallback) {
        this.onShopStateChangedCallback = onShopStateChangedCallback;
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = Theme.createTitleLabel("🛍️  Item Shop & Wardrobe");
        coinsBalanceLabel = new JLabel("🪙  0 COINS");
        coinsBalanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        coinsBalanceLabel.setForeground(Theme.GOLD_GLOW);

        header.add(title, BorderLayout.WEST);
        header.add(coinsBalanceLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        itemsGridContainer = new JPanel(new GridLayout(0, 3, 16, 16));
        itemsGridContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(itemsGridContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);

        loadShopItems();
    }

    public void loadShopItems() {
        if (Session.getInstance().getCurrentUser() == null) return;
        int userId = Session.getInstance().getCurrentUser().getUserId();
        PlayerState state = Session.getInstance().getCurrentPlayerState();

        if (state != null) {
            coinsBalanceLabel.setText("🪙  " + state.getCoins() + " COINS");
        }

        itemsGridContainer.removeAll();
        try {
            List<Item> items = shopService.getShopItems(userId);
            if (items.isEmpty()) items = createDefaultFallbackItems();

            for (Item item : items) {
                JPanel itemCard = createItemCard(item, userId, state != null ? state.getCoins() : 0);
                itemsGridContainer.add(itemCard);
            }
        } catch (Exception e) {
            List<Item> fallback = createDefaultFallbackItems();
            for (Item item : fallback) {
                itemsGridContainer.add(createItemCard(item, userId, state != null ? state.getCoins() : 0));
            }
        }
        itemsGridContainer.revalidate();
        itemsGridContainer.repaint();
    }

    private List<Item> createDefaultFallbackItems() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(1, "Sporty Cap", "CAP", 100, "cap.png"));
        list.add(new Item(2, "Running Shoes", "SHOES", 150, "shoes.png"));
        list.add(new Item(3, "Pet Bandana", "PET_ACC", 120, "pet_bandana.png"));
        list.add(new Item(4, "Room Poster", "ROOM", 200, "room_poster.png"));
        list.add(new Item(5, "Workout Outfit", "OUTFIT", 250, "outfit.png"));
        return list;
    }

    private JPanel createItemCard(Item item, int userId, int userCoins) {
        JPanel card = Theme.createGlassCard(20);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel slotTag = new JLabel("  " + item.getSlot() + "  ");
        slotTag.setFont(Theme.FONT_SMALL);
        slotTag.setForeground(Theme.CYAN_NEON);
        slotTag.setBorder(BorderFactory.createLineBorder(Theme.CYAN_NEON, 1));

        JLabel priceTag = new JLabel("🪙 " + item.getPriceCoins());
        priceTag.setFont(Theme.FONT_BOLD);
        priceTag.setForeground(Theme.GOLD_GLOW);

        cardHeader.add(slotTag, BorderLayout.WEST);
        cardHeader.add(priceTag, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        JLabel imgLabel = new JLabel(ImageCache.getIcon("assets/items/" + item.getImageFile(), 80, 80), SwingConstants.CENTER);

        JPanel centerContent = new JPanel(new BorderLayout(0, 8));
        centerContent.setOpaque(false);
        centerContent.add(imgLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(item.getItemName(), SwingConstants.CENTER);
        nameLabel.setFont(Theme.FONT_SUBTITLE);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        centerContent.add(nameLabel, BorderLayout.SOUTH);

        card.add(centerContent, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        actionPanel.setOpaque(false);

        if (!item.isOwned()) {
            JButton buyBtn = Theme.createGradientButton("BUY (" + item.getPriceCoins() + " 🪙)", Theme.GOLD_GLOW, new Color(245, 175, 20));
            buyBtn.setPreferredSize(new Dimension(160, 36));
            if (userCoins < item.getPriceCoins()) {
                buyBtn.setEnabled(false);
                buyBtn.setToolTipText("Not enough coins! Log workouts to earn more.");
            }
            buyBtn.addActionListener(e -> performPurchase(userId, item));
            actionPanel.add(buyBtn);
        } else {
            boolean equipped = item.isEquipped();
            String btnText = equipped ? "✓ EQUIPPED" : "EQUIP ITEM";
            Color bg = equipped ? Theme.GREEN_EMERALD : Theme.PANEL_BG_LIGHT;
            Color hover = equipped ? new Color(0, 180, 80) : Theme.BLUE_ELECTRIC;

            JButton equipBtn = Theme.createGradientButton(btnText, bg, hover);
            equipBtn.setPreferredSize(new Dimension(160, 36));
            equipBtn.addActionListener(e -> performToggleEquip(userId, item));
            actionPanel.add(equipBtn);
        }

        card.add(actionPanel, BorderLayout.SOUTH);
        return card;
    }

    private void performPurchase(int userId, Item item) {
        try {
            boolean success = shopService.purchaseItem(userId, item.getItemId());
            if (success) {
                PlayerState state = Session.getInstance().getCurrentPlayerState();
                state.setCoins(state.getCoins() - item.getPriceCoins());

                JOptionPane.showMessageDialog(this, "🎉  Successfully purchased " + item.getItemName() + "!", "Purchased", JOptionPane.INFORMATION_MESSAGE);
                loadShopItems();
                if (onShopStateChangedCallback != null) onShopStateChangedCallback.run();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️  " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performToggleEquip(int userId, Item item) {
        try {
            shopService.toggleEquipItem(userId, item.getItemId(), item.getSlot(), item.isEquipped());
            loadShopItems();
            if (onShopStateChangedCallback != null) onShopStateChangedCallback.run();
        } catch (Exception e) {
            item.setEquipped(!item.isEquipped());
            loadShopItems();
            if (onShopStateChangedCallback != null) onShopStateChangedCallback.run();
        }
    }
}
