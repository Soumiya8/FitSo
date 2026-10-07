package model;

import java.util.Date;

public class BodyMeasurement {
    private int measurementId;
    private int userId;
    private Date measuredOn;
    private double weightKg;
    private Double waistCm;
    private Double chestCm;
    private Double hipsCm;

    public BodyMeasurement() {}

    public BodyMeasurement(int measurementId, int userId, Date measuredOn, double weightKg, 
                           Double waistCm, Double chestCm, Double hipsCm) {
        this.measurementId = measurementId;
        this.userId = userId;
        this.measuredOn = measuredOn;
        this.weightKg = weightKg;
        this.waistCm = waistCm;
        this.chestCm = chestCm;
        this.hipsCm = hipsCm;
    }

    public int getMeasurementId() { return measurementId; }
    public void setMeasurementId(int measurementId) { this.measurementId = measurementId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Date getMeasuredOn() { return measuredOn; }
    public void setMeasuredOn(Date measuredOn) { this.measuredOn = measuredOn; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public Double getWaistCm() { return waistCm; }
    public void setWaistCm(Double waistCm) { this.waistCm = waistCm; }

    public Double getChestCm() { return chestCm; }
    public void setChestCm(Double chestCm) { this.chestCm = chestCm; }

    public Double getHipsCm() { return hipsCm; }
    public void setHipsCm(Double hipsCm) { this.hipsCm = hipsCm; }
}
