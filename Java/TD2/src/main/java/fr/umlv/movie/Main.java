package fr.umlv.movie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        var moviesFile = Path.of("movies.txt");
        try (var movies = Files.lines(moviesFile)) {
            System.out.println(movies.count());
        }

        var movies = Movies.movies(moviesFile);
        movies.stream()
                .flatMap(movie -> movie.actors().stream())
                .limit(20)
                .forEach(System.out::println);
    }
}
