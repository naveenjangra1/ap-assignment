package vehicles;

public abstract class LandVehicle extends Vehicle {

    private int numWheels;

    public LandVehicle(String id, String model, double maxSpeed, int numWheels) {
        super(id, model, maxSpeed);
        this.numWheels = numWheels;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / getMaxSpeed();
        return baseTime * 1.10;
    }

    public int getNumWheels() {
        return numWheels;
    }
}