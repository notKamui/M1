package fr.umlv.json;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("static-method")
public class JSONPrinterTest {
    @Test @Tag("Q1")
    public void testToJSONPersonPartial() {
        var person = new Person("John", "Doe");
        var personJSON = JSONPrinter.toJSON(person);
        assertEquals("John", IncompleteJSONParser.parse(personJSON).get("firstName"));
        assertEquals("Doe", IncompleteJSONParser.parse(personJSON).get("lastName"));
    }
    @Test @Tag("Q1")
    public void testToJSONPerson() {
        var person = new Person("John", "Doe");
        var personJSON = JSONPrinter.toJSON(person);
        assertEquals(
            Map.of("firstName", "John", "lastName", "Doe"), IncompleteJSONParser.parse(personJSON)
        );
    }

    @Test @Tag("Q1")
    public void testToJSONAlienPartial() {
        var alien = new Alien(100, "Saturn");
        var alienJSON = JSONPrinter.toJSON(alien);
        assertEquals("Saturn", IncompleteJSONParser.parse(alienJSON).get("planet"));
        assertEquals(100, IncompleteJSONParser.parse(alienJSON).get("age"));
    }
    @Test @Tag("Q1")
    public void testToJSONAlien() {
        var alien = new Alien(100, "saturn");
        var alienJSON = JSONPrinter.toJSON(alien);
        assertEquals(Map.of("age", 100, "planet", "saturn"), IncompleteJSONParser.parse(alienJSON));
    }

    @Test @Tag("Q2")
    public void testToJSONPropertyWithAName() {
        record Book(@JSONProperty("book-title") String title,
                    @JSONProperty("book-author") String author,
                    @JSONProperty("book-price") int price) { }

        var book = new Book("The Girl with The Dragon Tattoo", "Stieg Larsson", 100);
        var bookJSON = JSONPrinter.toJSON(book);
        assertEquals(
            Map.of("book-title", "The Girl with The Dragon Tattoo", "book-author", "Stieg Larsson", "book-price", 100),
            IncompleteJSONParser.parse(bookJSON)
        );
    }


    @Test @Tag("Q3")
    public void testToJSONPropertyEmpty() {
        record Book(@JSONProperty String book_title, String an_author, int price) { }

        var book = new Book("The Girl with The Dragon Tattoo", "Stieg Larsson", 100);
        var bookJSON = JSONPrinter.toJSON(book);
        assertEquals(
            Map.of("book-title", "The Girl with The Dragon Tattoo", "an_author", "Stieg Larsson", "price", 100),
            IncompleteJSONParser.parse(bookJSON)
        );
    }
}
