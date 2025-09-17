package management;

import exceptions.InvalidOperationException;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import vehicles.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FleetManager {

    private final List<Vehicle> fleet;

    public FleetManager() {
        this.fleet = new ArrayList<>();
    }

    public void addVehicle(Vehicle v) throws InvalidOperationException {
        for (Vehicle vehicle : fleet) {
            if (vehicle.getId().equalsIgnoreCase(v.getId())) {
                throw new InvalidOperationException("Duplicate vehicle ID: " + v.getId() + ". Cannot add vehicle.");
            }
        }
        fleet.add(v);
        System.out.println("Vehicle " + v.getId() + " added to the fleet.");
    }

    public void removeVehicle(String id) throws InvalidOperationException {
        boolean removed = fleet.removeIf(vehicle -> vehicle.getId().equalsIgnoreCase(id));
        if (!removed) {
            throw new InvalidOperationException("Vehicle with ID " + id + " not found. Cannot remove.");
        }
        System.out.println("Vehicle " + id + " removed from the fleet.");
    }

    public void startAllJourneys(double distance) {
        System.out.println("\n--- Starting all journeys for " + distance + " km ---");
        for (Vehicle vehicle : fleet) {
            try {
                vehicle.move(distance);
            } catch (Exception e) {
                System.err.printf("Could not complete journey for vehicle %s: %s\n", vehicle.getId(), e.getMessage());
            }
        }
        System.out.println("--- All journeys concluded ---");
    }

    public double getTotalFuelConsumption(double distance) {
        return fleet.stream()
                .filter(v -> v instanceof FuelConsumable)
                .mapToDouble(v -> distance / v.calculateFuelEfficiency())
                .sum();
    }

    public void maintainAll() {
        System.out.println("\n--- Checking for and performing maintenance ---");
        for (Vehicle vehicle : fleet) {
            if (vehicle instanceof Maintainable) {
                Maintainable maintainableVehicle = (Maintainable) vehicle;
                if (maintainableVehicle.needsMaintenance()) {
                    maintainableVehicle.performMaintenance();
                }
            }
        }
        System.out.println("--- Maintenance checks complete ---");
    }

    public List<Vehicle> searchByType(Class<?> type) {
        return fleet.stream()
                .filter(type::isInstance)
                .collect(Collectors.toList());
    }

    public void sortFleetByEfficiency() {
        Collections.sort(fleet);
    }

    public String generateReport() {
        if (fleet.isEmpty()) {
            return "Fleet Report: The fleet is currently empty.";
        }

        StringBuilder report = new StringBuilder();
        report.append("================ FLEET REPORT ================\n");
        report.append("Total Vehicles: ").append(fleet.size()).append("\n\n");

        Map<String, Long> countByType = fleet.stream()
                .collect(Collectors.groupingBy(v -> v.getClass().getSimpleName(), Collectors.counting()));
        report.append("Vehicles by Type:\n");
        countByType.forEach((type, count) -> report.append(String.format("  - %s: %d\n", type, count)));

        double avgEfficiency = fleet.stream()
                .filter(v -> v instanceof FuelConsumable && v.calculateFuelEfficiency() > 0)
                .mapToDouble(Vehicle::calculateFuelEfficiency)
                .average()
                .orElse(0.0);
        report.append(String.format("\nAverage Fuel Efficiency: %.2f km/l\n", avgEfficiency));

        double totalMileage = fleet.stream().mapToDouble(Vehicle::getCurrentMileage).sum();
        report.append(String.format("Total Fleet Mileage: %.1f km\n\n", totalMileage));

        List<Vehicle> needsMaintenance = getVehiclesNeedingMaintenance();
        report.append("Maintenance Status:\n");
        if (needsMaintenance.isEmpty()) {
            report.append("  All vehicles are in good condition.\n");
        } else {
            report.append("  Vehicles needing maintenance: ").append(needsMaintenance.size()).append("\n");
            needsMaintenance.forEach(v -> report.append(String.format("    - ID: %s, Mileage: %.1f km\n", v.getId(), v.getCurrentMileage())));
        }
        report.append("==============================================");
        return report.toString();
    }

    public List<Vehicle> getVehiclesNeedingMaintenance() {
        return fleet.stream()
                .filter(v -> v instanceof Maintainable)
                .filter(v -> ((Maintainable) v).needsMaintenance())
                .collect(Collectors.toList());
    }

    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Vehicle vehicle : fleet) {
                writer.println(vehicle.toCsvString());
            }
            System.out.println("Fleet successfully saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving fleet to file: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        fleet.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    Vehicle vehicle = createVehicleFromCsv(data);
                    fleet.add(vehicle);
                } catch (Exception e) {
                    System.err.println("Skipping malformed line in " + filename + ": " + line + " (" + e.getMessage() + ")");
                }
            }
            System.out.println("Fleet successfully loaded from " + filename);
        } catch (IOException e) {
            System.err.println("Error loading fleet from file: " + e.getMessage());
        }
    }

    private Vehicle createVehicleFromCsv(String[] data) {
        String type = data[0];
        String id = data[1];
        String model = data[2];
        double maxSpeed = Double.parseDouble(data[3]);

        switch (type) {
            case "Car":
                return new Car(id, model, maxSpeed);
            case "Truck":
                return new Truck(id, model, maxSpeed);
            case "Bus":
                return new Bus(id, model, maxSpeed);
            case "Airplane":
                double maxAltitude = Double.parseDouble(data[4]);
                return new Airplane(id, model, maxSpeed, maxAltitude);
            case "CargoShip":
                boolean hasSail = Boolean.parseBoolean(data[4]);
                return new CargoShip(id, model, maxSpeed, hasSail);
            default:
                throw new IllegalArgumentException("Unknown vehicle type in file: " + type);
        }
    }
}
