package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;

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