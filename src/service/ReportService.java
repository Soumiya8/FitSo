package service;

import dao.MeasurementDao;
import dao.WorkoutDao;
import model.Workout;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportService {

    private final WorkoutDao workoutDao = new WorkoutDao();
    private final MeasurementDao measurementDao = new MeasurementDao();

    public static class WeeklySummary {
        public final int totalWorkouts;
        public final int totalMinutes;
        public final int totalCalories;
        public final int totalEnergy;

        public WeeklySummary(int totalWorkouts, int totalMinutes, int totalCalories, int totalEnergy) {
            this.totalWorkouts = totalWorkouts;
            this.totalMinutes = totalMinutes;
            this.totalCalories = totalCalories;
            this.totalEnergy = totalEnergy;
        }
    }

    public Map<Date, Integer> getDailyCaloriesLast7Days(int userId) throws SQLException {
        return workoutDao.getDailyCalories(userId, 7);
    }

    public Map<String, Integer> getWorkoutsPerWeekLast8Weeks(int userId) throws SQLException {
        return workoutDao.getWorkoutsPerWeek(userId, 8);
    }

    public Map<Date, Double> getWeightHistory(int userId) throws SQLException {
        return measurementDao.getWeightHistory(userId);
    }

    public WeeklySummary getWeeklySummary(int userId) throws SQLException {
        List<Workout> workouts = workoutDao.findByUserId(userId);
        long sevenDaysAgoMs = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);

        int count = 0;
        int minutes = 0;
        int calories = 0;
        int energy = 0;

        for (Workout w : workouts) {
            if (w.getWorkoutDate().getTime() >= sevenDaysAgoMs) {
                count++;
                minutes += w.getDurationMin();
                calories += w.getCaloriesBurned();
                energy += w.getEnergyEarned();
            }
        }
        return new WeeklySummary(count, minutes, calories, energy);
    }
}
