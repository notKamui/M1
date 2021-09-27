package fr.umlv.rental;

public record Camel(int year) implements Vehicle {
    @Override
    public long insuranceCostAt(int year) {
        return this.year * 100L;
    }

    @Override
    public String toString() {
        return "camel %d".formatted(year);
    }
}
