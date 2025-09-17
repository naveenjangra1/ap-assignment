package ui;

import exceptions.InvalidOperationException;
import interfaces.*;
import management.FleetManager;
import vehicles.*;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final FleetManager fleetManager = new FleetManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Transportation Fleet Management System!");
        setupDemo();
        runCLI();
        scanner.close();
    }

    private static void setupDemo() {
        System.out.println("\n--- Setting up a demonstration fleet... ---");
        try {
            fleetManager.addVehicle(new Car("C-001", "Toyota Camry", 180));
            fleetManager.addVehicle(new Truck("T-001", "Volvo FH16", 140));
            fleetManager.addVehicle(new Bus("B-001", "Mercedes-Benz Tourismo", 150));
            fleetManager.addVehicle(new Airplane("A-001", "Boeing 747", 900, 35000));
            fleetManager.addVehicle(new CargoShip("S-001", "Emma Maersk", 45, false));
            fleetManager.addVehicle(new CargoShip("S-002", "The Black Pearl", 30, true));
            System.out.println("--- Demo fleet setup complete. ---");

            fleetManager.startAllJourneys(100);
            System.out.println("\n--- Initial Fleet Report ---");
            System.out.println(fleetManager.generateReport());
            fleetManager.saveToFile("my_fleet.csv");
            System.out.println("\nDemo fleet has been saved to my_fleet.csv");

        } catch (InvalidOperationException e) {
            System.err.println("Error setting up demo fleet: " + e.getMessage());
        }
    }

    private static void runCLI() {
        boolean running = true;
        while (running) {
            displayMenu();
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: handleAddVehicle(); break;
                    case 2: handleRemoveVehicle(); break;
                    case 3: handleStartJourney(); break;
                    case 4: handleRefuelAll(); break;
                    case 5: fleetManager.maintainAll(); break;
                    case 6: System.out.println(fleetManager.generateReport()); break;
                    case 7: handleSaveFleet(); break;
                    case 8: handleLoadFleet(); break;
                    case 9: handleSearchByType(); break;
                    case 10: handleListMaintenance(); break;
                    case 11: running = false; break;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
            System.out.println();
        }
        System.out.println("Thank you for using the Fleet Management System. Goodbye!");
    }

    private static void displayMenu() {
        System.out.println("\n========== FLEET MANAGEMENT MENU ==========");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Remove Vehicle");
        System.out.println("3. Start Journey for All Vehicles");
        System.out.println("4. Refuel All Applicable Vehicles");
        System.out.println("5. Perform Maintenance on All Applicable Vehicles");
        System.out.println("6. Generate Fleet Report");
        System.out.println("7. Save Fleet to File");
        System.out.println("8. Load Fleet from File");
        System.out.println("9. Search Vehicles by Type");
        System.out.println("10. List Vehicles Needing Maintenance");
        System.out.println("11. Exit");
        System.out.println("=========================================");
    }

    private static void handleAddVehicle() {
        try {
            System.out.print("Enter vehicle type (Car, Truck, Bus, Airplane, CargoShip): ");
            String type = scanner.nextLine();
            System.out.print("Enter ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter Model: ");
            String model = scanner.nextLine();
            System.out.print("Enter Max Speed (km/h): ");
            double maxSpeed = scanner.nextDouble();
            scanner.nextLine();

            Vehicle vehicle;
            switch (type.toLowerCase()) {
                case "car":
                    vehicle = new Car(id, model, maxSpeed);
                    break;
                case "truck":
                    vehicle = new Truck(id, model, maxSpeed);
                    break;
                case "bus":
                    vehicle = new Bus(id, model, maxSpeed);
                    break;
                case "airplane":
                    System.out.print("Enter Max Altitude (ft): ");
                    double maxAltitude = scanner.nextDouble();
                    scanner.nextLine();
                    vehicle = new Airplane(id, model, maxSpeed, maxAltitude);
                    break;
                case "cargoship":
                    System.out.print("Does it have a sail? (true/false): ");
                    boolean hasSail = scanner.nextBoolean();
                    scanner.nextLine();
                    vehicle = new CargoShip(id, model, maxSpeed, hasSail);
                    break;
                default:
                    System.out.println("Invalid vehicle type.");
                    return;
            }
            fleetManager.addVehicle(vehicle);
        } catch (InputMismatchException e) {
            System.out.println("Invalid numeric input. Please try again.");
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
        }
    }

    private static void handleRemoveVehicle() {
        try {
            System.out.print("Enter the ID of the vehicle to remove: ");
            String id = scanner.nextLine();
            fleetManager.removeVehicle(id);
        } catch (Exception e) {
            System.err.println("Error removing vehicle: " + e.getMessage());
        }
    }

    private static void handleStartJourney() {
        try {
            System.out.print("Enter the distance for the journey (km): ");
            double distance = scanner.nextDouble();
            scanner.nextLine();
            fleetManager.startAllJourneys(distance);
        } catch (InputMismatchException e) {
            System.out.println("Invalid distance entered. Please enter a number.");
            scanner.nextLine();
        }
    }

    private static void handleRefuelAll() {
        try {
            System.out.print("Enter amount of fuel to add (L): ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            List<Vehicle> fuelVehicles = fleetManager.searchByType(FuelConsumable.class);
            for (Vehicle v : fuelVehicles) {
                try {
                    ((FuelConsumable) v).refuel(amount);
                } catch (InvalidOperationException e) {
                    System.err.printf("Could not refuel vehicle %s: %s\n", v.getId(), e.getMessage());
                }
            }
            System.out.println("Refueling complete.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid amount entered. Please enter a number.");
            scanner.nextLine();
        }
    }

    private static void handleSaveFleet() {
        fleetManager.saveToFile("my_fleet.csv");
    }

    private static void handleLoadFleet() {
        fleetManager.loadFromFile("my_fleet.csv");
    }

    private static void handleSearchByType() {
        System.out.print("Enter type to search for (e.g., Car, Truck, FuelConsumable): ");
        String typeStr = scanner.nextLine();
        Class<?> typeClass;

        switch(typeStr.toLowerCase()) {
            case "car": typeClass = Car.class; break;
            case "truck": typeClass = Truck.class; break;
            case "bus": typeClass = Bus.class; break;
            case "airplane": typeClass = Airplane.class; break;
            case "cargoship": typeClass = CargoShip.class; break;
            case "fuelconsumable": typeClass = FuelConsumable.class; break;
            case "cargocarrier": typeClass = CargoCarrier.class; break;
            case "passengercarrier": typeClass = PassengerCarrier.class; break;
            case "maintainable": typeClass = Maintainable.class; break;
            default:
                System.out.println("Unknown or unsupported type for searching.");
                return;
        }

        List<Vehicle> results = fleetManager.searchByType(typeClass);
        System.out.printf("--- Found %d vehicle(s) of type %s ---\n", results.size(), typeStr);
        results.forEach(Vehicle::displayInfo);
    }

    private static void handleListMaintenance() {
        List<Vehicle> maintenanceList = fleetManager.getVehiclesNeedingMaintenance();
        if (maintenanceList.isEmpty()) {
            System.out.println("No vehicles currently need maintenance.");
        } else {
            System.out.printf("--- %d vehicle(s) need maintenance ---\n", maintenanceList.size());
            maintenanceList.forEach(Vehicle::displayInfo);
        }
    }
}