package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;

public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {

    private double fuelLevel;
    private final int passengerCapacity = 5;
    private int currentPassengers;
    private boolean maintenanceNeeded;

    public Car(String id, String model, double maxSpeed) {
        super(id, model, maxSpeed, 4);
        this.fuelLevel = 0;
        this.currentPassengers = 0;
        this.maintenanceNeeded = false;
    }

    @Override
    public double calculateFuelEfficiency() {
        return 15.0;
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
        System.out.printf("Car %s is driving on the road for %.1f km.\n", getId(), distance);
    }

    @Override
    public String toCsvString() {
        return String.format("Car,%s,%s,%.1f,%d,%.1f,%d,%d",
                getId(), getModel(), getMaxSpeed(), getNumWheels(),
                getFuelLevel(), getPassengerCapacity(), getCurrentPassengers());
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) {
            throw new InvalidOperationException("Refuel amount must be positive.");
        }
        this.fuelLevel += amount;
        System.out.printf("Car %s refueled with %.1f L. Current fuel: %.1f L\n", getId(), amount, this.fuelLevel);
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
    public int getPassengerCapacity() {
        return this.passengerCapacity;
    }

    @Override
    public int getCurrentPassengers() {
        return this.currentPassengers;
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
        System.out.printf("Maintenance performed on Car %s.\n", getId());
    }
}
