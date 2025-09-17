package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;

public abstract class Vehicle implements Comparable<Vehicle> {

    private final String id;
    private final String model;
    private final double maxSpeed;
    private double currentMileage;

    public Vehicle(String id, String model, double maxSpeed) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle ID cannot be empty or null.");
        }
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = 0.0;
    }

    public abstract void move(double distance) throws InvalidOperationException, InsufficientFuelException;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance);
    public abstract String toCsvString();

    public void displayInfo() {
        System.out.println("--- Vehicle Info ---");
        System.out.printf("ID: %s\n", id);
        System.out.printf("Model: %s\n", model);
        System.out.printf("Max Speed: %.1f km/h\n", maxSpeed);
        System.out.printf("Current Mileage: %.1f km\n", currentMileage);
    }

    public double getCurrentMileage() {
        return currentMileage;
    }

    public String getId() {
        return id;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public String getModel() {
        return model;
    }

    protected void addMileage(double distance) {
        if (distance > 0) {
            this.currentMileage += distance;
        }
    }

    @Override
    public int compareTo(Vehicle other) {
        return Double.compare(other.calculateFuelEfficiency(), this.calculateFuelEfficiency());
    }
}
