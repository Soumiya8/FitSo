package service;

import dao.MeasurementDao;
import dao.PlayerStateDao;
import dao.WorkoutDao;
import dao.WorkoutTypeDao;
import model.BodyMeasurement;
import model.Mission;
import model.Workout;
import model.WorkoutType;
import util.DBConnection;
import util.GameConfig;
import util.Validator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class WorkoutService {

    private final WorkoutDao workoutDao = new WorkoutDao();
    private final WorkoutTypeDao workoutTypeDao = new WorkoutTypeDao();
    private final MeasurementDao measurementDao = new MeasurementDao();
    private final PlayerStateDao playerStateDao = new PlayerStateDao();
    private final GamificationService gamificationService = new GamificationService();
    private final MissionService missionService = new MissionService();

    public static class WorkoutResult {
        public final Workout workout;
        public final int energyEarned;
        public final int coinsEarned;
        public final List<Mission> completedMissions;

        public WorkoutResult(Workout workout, int energyEarned, int coinsEarned, List<Mission> completedMissions) {
            this.workout = workout;
            this.energyEarned = energyEarned;
            this.coinsEarned = coinsEarned;
            this.completedMissions = completedMissions;
        }
    }

    public WorkoutResult logWorkout(int userId, int typeId, Date date, int durationMin, 
                                    String intensity, Integer overriddenCalories, String notes) throws SQLException, IllegalArgumentException {
        // Step 1: Validate input
        Validator.validateWorkout(durationMin, overriddenCalories != null ? overriddenCalories : 0);

        WorkoutType type = workoutTypeDao.findById(typeId);
        if (type == null) {
            throw new IllegalArgumentException("Invalid workout type selected.");
        }

        // Step 2: Compute calories
        int calories;
        if (overriddenCalories != null && overriddenCalories > 0) {
            calories = overriddenCalories;
        } else {
            Double latestWeight = measurementDao.findLatestWeight(userId);
            double weight = latestWeight != null ? latestWeight : GameConfig.DEFAULT_WEIGHT_KG;
            calories = gamificationService.calculateCalories(type.getMetValue(), weight, durationMin);
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Step 3: Compute rewards based on daily workout cap
                int todayRewardedCount = workoutDao.countRewardedToday(userId, date, conn);
                int energyEarned = 0;
                int coinsEarned = 0;

                if (todayRewardedCount < GameConfig.MAX_REWARDED_PER_DAY) {
                    GameConfig.RewardTier reward = GameConfig.getRewardForDuration(durationMin);
                    energyEarned = reward.energy;
                    coinsEarned = reward.coins;
                }

                // Step 4: INSERT Workout
                Workout workout = new Workout(0, userId, typeId, date, durationMin, intensity, calories, energyEarned, coinsEarned, notes);
                int workoutId = workoutDao.insert(workout, conn);
                workout.setWorkoutId(workoutId);
                workout.setTypeName(type.getTypeName());

                // Step 5: UPDATE PlayerState balances
                if (energyEarned > 0 || coinsEarned > 0) {
                    playerStateDao.addEnergyAndCoins(userId, energyEarned, coinsEarned, conn);
                }

                // Step 6: Mission Evaluation Loop
                List<Mission> completedMissions = missionService.evaluateMissions(userId, conn);

                conn.commit();
                return new WorkoutResult(workout, energyEarned, coinsEarned, completedMissions);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean logMeasurement(BodyMeasurement measurement) throws SQLException, IllegalArgumentException {
        Validator.validateMeasurement(measurement.getWeightKg(), measurement.getWaistCm(), measurement.getChestCm(), measurement.getHipsCm());
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean saved = measurementDao.saveOrUpdate(measurement, conn);
                // Run mission loop as measurements can trigger "Measure Up" or "Measurement Count" missions!
                missionService.evaluateMissions(measurement.getUserId(), conn);
                conn.commit();
                return saved;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
