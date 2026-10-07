package model;

import java.util.Date;

public class Workout {
    private int workoutId;
    private int userId;
    private int typeId;
    private String typeName; // Joined field for UI convenience
    private Date workoutDate;
    private int durationMin;
    private String intensity;
    private int caloriesBurned;
    private int energyEarned;
    private int coinsEarned;
    private String notes;

    public Workout() {}

    public Workout(int workoutId, int userId, int typeId, Date workoutDate, int durationMin, 
                   String intensity, int caloriesBurned, int energyEarned, int coinsEarned, String notes) {
        this.workoutId = workoutId;
        this.userId = userId;
        this.typeId = typeId;
        this.workoutDate = workoutDate;
        this.durationMin = durationMin;
        this.intensity = intensity;
        this.caloriesBurned = caloriesBurned;
        this.energyEarned = energyEarned;
        this.coinsEarned = coinsEarned;
        this.notes = notes;
    }

    public int getWorkoutId() { return workoutId; }
    public void setWorkoutId(int workoutId) { this.workoutId = workoutId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public Date getWorkoutDate() { return workoutDate; }
    public void setWorkoutDate(Date workoutDate) { this.workoutDate = workoutDate; }

    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }

    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }

    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public int getEnergyEarned() { return energyEarned; }
    public void setEnergyEarned(int energyEarned) { this.energyEarned = energyEarned; }

    public int getCoinsEarned() { return coinsEarned; }
    public void setCoinsEarned(int coinsEarned) { this.coinsEarned = coinsEarned; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
