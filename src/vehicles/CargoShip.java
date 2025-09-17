package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;

public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {

    private final double cargoCapacity = 50000.0;
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double fuelLevel;

    public CargoShip(String id, String model, double maxSpeed, boolean hasSail) {
        super(id, model, maxSpeed, hasSail);
        this.currentCargo = 0;
        this.maintenanceNeeded = false;
        this.fuelLevel = hasSail() ? 0 : 50000;
    }

    @Override
    public double calculateFuelEfficiency() {
        return hasSail() ? 0 : 4.0;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance <= 0) {
            throw new InvalidOperationException("Distance must be positive.");
        }
        if (!hasSail()) {
            double fuelNeeded = distance / calculateFuelEfficiency();
            if (fuelLevel < fuelNeeded) {
                throw new InsufficientFuelException("Not enough fuel for the journey.");
            }
            consumeFuel(distance);
        }
        addMileage(distance);
        System.out.printf("CargoShip %s is sailing with cargo for %.1f km.\n", getId(), distance);
    }

    @Override
    public String toCsvString() {
        return String.format("CargoShip,%s,%s,%.1f,%b,%.1f,%.1f",
                getId(), getModel(), getMaxSpeed(), hasSail(),
                getCargoCapacity(), getCurrentCargo());
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (hasSail()) {
            throw new InvalidOperationException("A sailing ship cannot be refueled.");
        }
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
        if (hasSail()) {
            return 0;
        }
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
        System.out.printf("Maintenance performed on CargoShip %s.\n", getId());
    }
}