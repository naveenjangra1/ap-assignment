package vehicles;

public abstract class WaterVehicle extends Vehicle {

    private boolean hasSail;

    public WaterVehicle(String id, String model, double maxSpeed, boolean hasSail) {
        super(id, model, maxSpeed);
        this.hasSail = hasSail;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / getMaxSpeed();
        return baseTime * 1.15;
    }

    public boolean hasSail() {
        return hasSail;
    }
}