package model;

public class Item {
    private int itemId;
    private String itemName;
    private String slot; // CAP, SHOES, PET_ACC, ROOM, OUTFIT
    private int priceCoins;
    private String imageFile;
    private boolean owned;    // Joined user field for UI convenience
    private boolean equipped; // Joined user field for UI convenience

    public Item() {}

    public Item(int itemId, String itemName, String slot, int priceCoins, String imageFile) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.slot = slot;
        this.priceCoins = priceCoins;
        this.imageFile = imageFile;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public int getPriceCoins() { return priceCoins; }
    public void setPriceCoins(int priceCoins) { this.priceCoins = priceCoins; }

    public String getImageFile() { return imageFile; }
    public void setImageFile(String imageFile) { this.imageFile = imageFile; }

    public boolean isOwned() { return owned; }
    public void setOwned(boolean owned) { this.owned = owned; }

    public boolean isEquipped() { return equipped; }
    public void setEquipped(boolean equipped) { this.equipped = equipped; }
}
