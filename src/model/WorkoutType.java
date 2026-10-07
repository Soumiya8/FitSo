package model;

public class WorkoutType {
    private int typeId;
    private String typeName;
    private double metValue;

    public WorkoutType() {}

    public WorkoutType(int typeId, String typeName, double metValue) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.metValue = metValue;
    }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public double getMetValue() { return metValue; }
    public void setMetValue(double metValue) { this.metValue = metValue; }

    @Override
    public String toString() {
        return typeName;
    }
}
