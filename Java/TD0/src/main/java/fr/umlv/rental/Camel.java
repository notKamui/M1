package fr.umlv.rental;

public record Camel(int year) implements Vehicle {
    @Override
    public long insuranceCostAt(int year) {
        if (year < year())
            throw new IllegalArgumentException("Year should be higher than DoB");
        return (year - this.year) * 100L;
    }

    @Override
    public String toString() {
        return "camel %d".formatted(year);
    }
}
