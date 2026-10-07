package model;

import java.util.Date;

public class Goal {
    private int goalId;
    private int userId;
    private String goalType; // LOSE_WEIGHT, GAIN_MUSCLE, STAY_FIT
    private Double targetWeightKg;
    private int weeklyWorkouts;
    private Date startDate;
    private boolean active;

    public Goal() {}

    public Goal(int goalId, int userId, String goalType, Double targetWeightKg, 
                int weeklyWorkouts, Date startDate, boolean active) {
        this.goalId = goalId;
        this.userId = userId;
        this.goalType = goalType;
        this.targetWeightKg = targetWeightKg;
        this.weeklyWorkouts = weeklyWorkouts;
        this.startDate = startDate;
        this.active = active;
    }

    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }

    public Double getTargetWeightKg() { return targetWeightKg; }
    public void setTargetWeightKg(Double targetWeightKg) { this.targetWeightKg = targetWeightKg; }

    public int getWeeklyWorkouts() { return weeklyWorkouts; }
    public void setWeeklyWorkouts(int weeklyWorkouts) { this.weeklyWorkouts = weeklyWorkouts; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
