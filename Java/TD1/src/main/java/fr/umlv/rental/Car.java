package fr.umlv.rental;

import java.util.Objects;

public record Car(String model, int year) {
    public Car {
        Objects.requireNonNull(model);
    }
}
