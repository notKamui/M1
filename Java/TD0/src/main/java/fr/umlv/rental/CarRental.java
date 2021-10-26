package fr.umlv.rental;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CarRental {
    private final List<Vehicle> vehicles = new ArrayList<>();

    public void add(Vehicle vehicle) {
        Objects.requireNonNull(vehicle);
        vehicles.add(vehicle);
    }

    public void remove(Vehicle vehicle) {
        Objects.requireNonNull(vehicle);
        if (!vehicles.remove(vehicle)) { // remove, then if was not in the list ->
            throw new IllegalStateException("Given car is not rentable");
        }
    }

    public List<Vehicle> findAllByYear(int year) {
        return vehicles.stream()
                .filter((vehicle) -> vehicle.year() == year)
                .toList();
    }

    public long insuranceCostAt(int year) {
        return vehicles.stream()
                .mapToLong((vehicle) -> vehicle.insuranceCostAt(year))
                .sum();
    }

    public Optional<Car> findACarByModel(String model) {
        Objects.requireNonNull(model);
        for (var vehicle : vehicles) {
            switch (vehicle) {
                case Car car -> {
                    if (car.model().equals(model)) {
                        return Optional.of(car);
                    }
                }
                case Camel ignored -> {}
            }
        }
        return Optional.empty();
    }

//    public Optional<Car> findACarByModel(String model) {
//        Objects.requireNonNull(model);
//        return vehicles.stream()
//                .filter((vehicle) -> vehicle instanceof Car)
//                .map((vehicle) -> (Car)vehicle) // *vomit*
//                .filter((car) -> car.model().equals(model))
//                .findAny();
//    }

//    public Optional<Car> findACarByModel(String model) {
//        Objects.requireNonNull(model);
//        for (var vehicle : vehicles) {
//            if (vehicle instanceof Car car) {
//                if (car.model().equals(model)) {
//                    return Optional.of(car);
//                }
//            }
//        }
//        return Optional.empty();
//    }

//    @Override
//    public String toString() {
//        var str = new StringBuilder();
//        for (var car : cars) {
//            str.append(car.toString()).append("\n");
//        }
//        return str.toString().stripTrailing();
//    }

    @Override
    public String toString() {
        return vehicles.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }
}
