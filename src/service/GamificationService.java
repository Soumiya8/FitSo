package service;

import util.GameConfig;

import java.util.Date;

public class GamificationService {

    public enum CharacterState { IDLE, HAPPY, TIRED, CELEBRATION }
    public enum PetState { NORMAL, HAPPY, SLEEPY, CELEBRATION }
    public enum WorldStage { BEDROOM, BACKYARD, PARK, ADVENTURE_ZONE }

    // Estimate calories burned
    public int calculateCalories(double metValue, double weightKg, int durationMin) {
        if (weightKg <= 0) weightKg = GameConfig.DEFAULT_WEIGHT_KG;
        double calories = metValue * weightKg * (durationMin / 60.0);
        return (int) Math.round(calories);
    }

    // Derived level formula: level = 1 + totalEnergy / LEVEL_STEP (capped at 10)
    public int calculateLevel(int totalEnergy) {
        int level = 1 + (totalEnergy / GameConfig.LEVEL_STEP);
        return Math.min(level, GameConfig.MAX_LEVEL);
    }

    // Progress percentage to next level
    public int calculateLevelProgressPercent(int totalEnergy) {
        int level = calculateLevel(totalEnergy);
        if (level >= GameConfig.MAX_LEVEL) return 100;
        int currentLevelBaseEnergy = (level - 1) * GameConfig.LEVEL_STEP;
        int energyInCurrentLevel = totalEnergy - currentLevelBaseEnergy;
        return (energyInCurrentLevel * 100) / GameConfig.LEVEL_STEP;
    }

    // Derived world stage based on level
    public WorldStage getWorldStage(int level) {
        if (level >= GameConfig.WORLD_ADVENTURE_MIN_LEVEL) {
            return WorldStage.ADVENTURE_ZONE;
        } else if (level >= GameConfig.WORLD_PARK_MIN_LEVEL) {
            return WorldStage.PARK;
        } else if (level >= GameConfig.WORLD_BACKYARD_MIN_LEVEL) {
            return WorldStage.BACKYARD;
        } else {
            return WorldStage.BEDROOM;
        }
    }

    // Character State derivation
    public CharacterState deriveCharacterState(boolean celebrating, boolean workedOutToday, Date lastWorkoutDate) {
        if (celebrating) return CharacterState.CELEBRATION;
        if (workedOutToday) return CharacterState.HAPPY;
        if (lastWorkoutDate == null) return CharacterState.TIRED;

        long diffMs = new Date().getTime() - lastWorkoutDate.getTime();
        long diffDays = diffMs / (1000 * 60 * 60 * 24);

        if (diffDays <= 2) {
            return CharacterState.IDLE;
        } else {
            return CharacterState.TIRED;
        }
    }

    // Pet State derivation
    public PetState derivePetState(boolean celebrating, boolean workedOutToday, Date lastWorkoutDate) {
        if (celebrating) return PetState.CELEBRATION;
        if (workedOutToday) return PetState.HAPPY;
        if (lastWorkoutDate == null) return PetState.SLEEPY;

        long diffMs = new Date().getTime() - lastWorkoutDate.getTime();
        long diffDays = diffMs / (1000 * 60 * 60 * 24);

        if (diffDays <= 2) {
            return PetState.NORMAL;
        } else {
            return PetState.SLEEPY;
        }
    }

    // Motivational prompt generator
    public String getPromptMessage(CharacterState state, String petName) {
        switch (state) {
            case CELEBRATION:
                return "Awesome job! You and " + petName + " are crushing your goals!";
            case HAPPY:
                return "Great effort today! " + petName + " is feeling energized!";
            case IDLE:
                return "Ready for today's mission? " + petName + " is waiting for you!";
            case TIRED:
            default:
                return petName + " misses you! Log a workout to boost your energy!";
        }
    }
}
