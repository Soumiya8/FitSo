package model;

public class PlayerState {
    private int userId;
    private int totalEnergy;
    private int coins;
    private String petName;

    public PlayerState() {}

    public PlayerState(int userId, int totalEnergy, int coins, String petName) {
        this.userId = userId;
        this.totalEnergy = totalEnergy;
        this.coins = coins;
        this.petName = petName;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTotalEnergy() { return totalEnergy; }
    public void setTotalEnergy(int totalEnergy) { this.totalEnergy = totalEnergy; }

    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
}
