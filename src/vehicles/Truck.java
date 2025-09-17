package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {

    private double fuelLevel;
    private final double cargoCapacity = 5000.0;
    private double currentCargo;
    private boolean maintenanceNeeded;

    public Truck(String id, String model, double maxSpeed) {
        super(id, model, maxSpeed, 8);
        this.fuelLevel = 0;
        this.currentCargo = 0;
        this.maintenanceNeeded = false;
    }

    @Override
    public double calculateFuelEfficiency() {
        double baseEfficiency = 8.0;
        if (currentCargo > cargoCapacity * 0.5) {
            return baseEfficiency * 0.90;
        }
        return baseEfficiency;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance <= 0) {
            throw new InvalidOperationException("Distance must be positive.");
        }
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelLevel < fuelNeeded) {
            throw new InsufficientFuelException("Not enough fuel for the journey.");
        }
        consumeFuel(distance);
        addMileage(distance);
        System.out.printf("Truck %s is hauling cargo for %.1f km.\n", getId(), distance);
    }

    @Override
    public String toCsvString() {
        return String.format("Truck,%s,%s,%.1f,%d,%.1f,%.1f,%.1f",
                getId(), getModel(), getMaxSpeed(), getNumWheels(),
                getFuelLevel(), getCargoCapacity(), getCurrentCargo());
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) {
            throw new InvalidOperationException("Refuel amount must be positive.");
        }
        this.fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return this.fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double consumed = distance / calculateFuelEfficiency();
        if (consumed > this.fuelLevel) {
            throw new InsufficientFuelException("Cannot consume more fuel than available.");
        }
        this.fuelLevel -= consumed;
        return consumed;
    }

    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (currentCargo + weight > cargoCapacity) {
            throw new OverloadException("Cargo capacity exceeded.");
        }
        this.currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > currentCargo) {
            throw new InvalidOperationException("Cannot unload more cargo than is loaded.");
        }
        this.currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity() {
        return this.cargoCapacity;
    }

    @Override
    public double getCurrentCargo() {
        return this.currentCargo;
    }

    @Override
    public void scheduleMaintenance() {
        this.maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance() {
        return this.maintenanceNeeded || getCurrentMileage() > 10000;
    }

    @Override
    public void performMaintenance() {
        this.maintenanceNeeded = false;
        System.out.printf("Maintenance performed on Truck %s.\n", getId());
    }
}