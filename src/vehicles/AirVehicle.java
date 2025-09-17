package vehicles;

public abstract class AirVehicle extends Vehicle {

    private double maxAltitude;

    public AirVehicle(String id, String model, double maxSpeed, double maxAltitude) {
        super(id, model, maxSpeed);
        this.maxAltitude = maxAltitude;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / getMaxSpeed();
        return baseTime * 0.95;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }
}
