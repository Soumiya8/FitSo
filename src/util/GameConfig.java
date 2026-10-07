package util;

/**
 * FitSo Game Configuration Constants
 * Pure static configuration class holding game rules and reward constants.
 */
public final class GameConfig {

    private GameConfig() {} // Non-instantiable

    // Level formula: level = 1 + totalEnergy / LEVEL_STEP (capped at MAX_LEVEL)
    public static final int LEVEL_STEP = 50;
    public static final int MAX_LEVEL = 10;

    // Workout reward rules
    public static final int MIN_REWARD_MINUTES = 5;
    public static final int MAX_REWARDED_PER_DAY = 3;

    // Default MET fallback
    public static final double DEFAULT_WEIGHT_KG = 70.0;

    // World thresholds (level required to unlock)
    public static final int WORLD_BEDROOM_MIN_LEVEL = 1;
    public static final int WORLD_BACKYARD_MIN_LEVEL = 3;
    public static final int WORLD_PARK_MIN_LEVEL = 5;
    public static final int WORLD_ADVENTURE_MIN_LEVEL = 8;

    // Reward Tiers (duration based)
    public static RewardTier getRewardForDuration(int durationMin) {
        if (durationMin < 5) {
            return new RewardTier(0, 0);
        } else if (durationMin <= 14) {
            return new RewardTier(5, 10);
        } else if (durationMin <= 30) {
            return new RewardTier(10, 25);
        } else {
            return new RewardTier(20, 50);
        }
    }

    public static class RewardTier {
        public final int energy;
        public final int coins;

        public RewardTier(int energy, int coins) {
            this.energy = energy;
            this.coins = coins;
        }
    }
}
