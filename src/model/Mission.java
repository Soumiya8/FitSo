package model;

public class Mission {
    private int missionId;
    private String title;
    private String metricType; // WORKOUT_COUNT, SINGLE_MINUTES, STREAK_DAYS, TOTAL_ENERGY, TOTAL_CALORIES, MEASUREMENT_COUNT
    private int targetValue;
    private int rewardEnergy;
    private int rewardCoins;

    public Mission() {}

    public Mission(int missionId, String title, String metricType, int targetValue, int rewardEnergy, int rewardCoins) {
        this.missionId = missionId;
        this.title = title;
        this.metricType = metricType;
        this.targetValue = targetValue;
        this.rewardEnergy = rewardEnergy;
        this.rewardCoins = rewardCoins;
    }

    public int getMissionId() { return missionId; }
    public void setMissionId(int missionId) { this.missionId = missionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }

    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }

    public int getRewardEnergy() { return rewardEnergy; }
    public void setRewardEnergy(int rewardEnergy) { this.rewardEnergy = rewardEnergy; }

    public int getRewardCoins() { return rewardCoins; }
    public void setRewardCoins(int rewardCoins) { this.rewardCoins = rewardCoins; }
}
