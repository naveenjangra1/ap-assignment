package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;

public class Bus extends LandVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {

    private double fuelLevel;
    private final int passengerCapacity = 50;
    private int currentPassengers;
    private final double cargoCapacity = 500.0;
    private double currentCargo;
    private boolean maintenanceNeeded;

    public Bus(String id, String model, double maxSpeed) {
        super(id, model, maxSpeed, 6);
        this.fuelLevel = 0;
        this.currentPassengers = 0;
        this.currentCargo = 0;
        this.maintenanceNeeded = false;
    }

    @Override
    public double calculateFuelEfficiency() {
        return 10.0;
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
        System.out.printf("Bus %s is transporting passengers and cargo for %.1f km.\n", getId(), distance);
    }

    @Override
    public String toCsvString() {
        return String.format("Bus,%s,%s,%.1f,%d,%.1f,%d,%d,%.1f,%.1f",
                getId(), getModel(), getMaxSpeed(), getNumWheels(),
                getFuelLevel(), getPassengerCapacity(), getCurrentPassengers(),
                getCargoCapacity(), getCurrentCargo());
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) {
            throw new InvalidOperationException("Refuel amount must be positive.");
        }
        this.fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() { return this.fuelLevel; }

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
    public void boardPassengers(int count) throws OverloadException {
        if (currentPassengers + count > passengerCapacity) {
            throw new OverloadException("Passenger capacity exceeded.");
        }
        this.currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > currentPassengers) {
            throw new InvalidOperationException("Cannot disembark more passengers than are on board.");
        }
        this.currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity() { return this.passengerCapacity; }

    @Override
    public int getCurrentPassengers() { return this.currentPassengers; }

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
    public double getCargoCapacity() { return this.cargoCapacity; }

    @Override
    public double getCurrentCargo() { return this.currentCargo; }

    @Override
    public void scheduleMaintenance() { this.maintenanceNeeded = true; }

    @Override
    public boolean needsMaintenance() {
        return this.maintenanceNeeded || getCurrentMileage() > 10000;
    }

    @Override
    public void performMaintenance() {
        this.maintenanceNeeded = false;
        System.out.printf("Maintenance performed on Bus %s.\n", getId());
    }
}