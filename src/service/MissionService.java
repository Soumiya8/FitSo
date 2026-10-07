package service;

import dao.MeasurementDao;
import dao.MissionDao;
import dao.PlayerStateDao;
import dao.WorkoutDao;
import model.Mission;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class MissionService {

    private final MissionDao missionDao = new MissionDao();
    private final WorkoutDao workoutDao = new WorkoutDao();
    private final MeasurementDao measurementDao = new MeasurementDao();
    private final PlayerStateDao playerStateDao = new PlayerStateDao();

    /**
     * Mission evaluation loop.
     * Evaluates uncompleted missions and awards rewards.
     * Repeats until no new mission completes in a pass.
     * @return List of newly completed missions
     */
    public List<Mission> evaluateMissions(int userId, Connection conn) throws SQLException {
        List<Mission> newlyCompleted = new ArrayList<>();
        boolean newMissionUnlockedInPass;

        do {
            newMissionUnlockedInPass = false;
            List<Mission> allMissions = missionDao.findAll();
            Set<Integer> completedIds = missionDao.findCompletedMissionIds(userId);

            for (Mission mission : allMissions) {
                if (completedIds.contains(mission.getMissionId())) {
                    continue; // Skip already completed
                }

                int currentValue = calculateMetricValue(userId, mission.getMetricType());

                if (currentValue >= mission.getTargetValue()) {
                    // Mission completed!
                    missionDao.insertCompletion(userId, mission.getMissionId(), conn);
                    playerStateDao.addEnergyAndCoins(userId, mission.getRewardEnergy(), mission.getRewardCoins(), conn);

                    newlyCompleted.add(mission);
                    newMissionUnlockedInPass = true;
                    break; // Break inner loop to re-evaluate remaining missions with updated state
                }
            }
        } while (newMissionUnlockedInPass);

        return newlyCompleted;
    }

    public int calculateMetricValue(int userId, String metricType) throws SQLException {
        switch (metricType) {
            case "WORKOUT_COUNT":
                return workoutDao.countTotalWorkouts(userId);
            case "SINGLE_MINUTES":
                return workoutDao.getMaxSingleMinutes(userId);
            case "STREAK_DAYS":
                return calculateStreakDays(userId);
            case "TOTAL_ENERGY":
                model.PlayerState state = playerStateDao.findByUserId(userId);
                return state != null ? state.getTotalEnergy() : 0;
            case "TOTAL_CALORIES":
                return workoutDao.getTotalCalories(userId);
            case "MEASUREMENT_COUNT":
                return measurementDao.countTotalMeasurements(userId);
            default:
                return 0;
        }
    }

    public int calculateStreakDays(int userId) throws SQLException {
        List<Date> dates = workoutDao.getDistinctWorkoutDates(userId);
        if (dates.isEmpty()) return 0;

        int maxStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < dates.size(); i++) {
            long prev = dates.get(i - 1).getTime();
            long curr = dates.get(i).getTime();
            long diffDays = (curr - prev) / (1000 * 60 * 60 * 24);

            if (diffDays == 1) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else if (diffDays > 1) {
                currentStreak = 1;
            }
        }
        return maxStreak;
    }
}
