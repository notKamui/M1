package fr.umlv.rental;

import java.util.Objects;

public record Car(String model, int year) implements Vehicle {
    public Car {
        Objects.requireNonNull(model);
    }

    @Override
    public long insuranceCostAt(int year) {
        return year - this.year < 10 ? 200 : 500;
    }

    @Override
    public String toString() {
        return "%s %d".formatted(model, year);
    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Car car = (Car) o;
//        return year == car.year && model.equals(car.model);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(model, year);
//    }
}
