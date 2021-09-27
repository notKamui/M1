package fr.umlv.rental;

public sealed interface Vehicle permits Camel, Car {
    int year();

    long insuranceCostAt(int year);
}
