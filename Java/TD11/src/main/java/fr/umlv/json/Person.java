package fr.umlv.json;

import static java.util.Objects.requireNonNull;

public record Person(String firstName, String lastName) {
    public Person {
        requireNonNull(firstName);
        requireNonNull(lastName);
    }
}
