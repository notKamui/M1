package fr.umlv.movie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Movies {
    private Movies() {}

    public static List<Movie> movies(Path config) throws IOException {
        Objects.requireNonNull(config);
        try (var movies = Files.lines(config)) {
            return movies
                    .map(movie -> movie.split(";"))
                    .map(parts -> new Movie(parts[0], Arrays.stream(parts).skip(1).toList()))
                    .toList();
        }
    }

    public static Map<String, Movie> movieMap(List<Movie> movies) {
        Objects.requireNonNull(movies);
        return movies.stream().collect(Collectors.toUnmodifiableMap(Movie::title, movie -> movie));
    }

    public static long numberOfUniqueActors(List<Movie> movies) {
        Objects.requireNonNull(movies);
        return movies.stream()
                .flatMap(movie -> movie.actors().stream())
                .distinct()
                .count();
    }

    public static Map<String, Long> numberOfMoviesByActor(List<Movie> movies) {
        Objects.requireNonNull(movies);
        return movies.stream()
                .flatMap(movie -> movie.actors().stream())
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
    }

//    public static Optional<ActorMovieCount> actorInMostMovies(Map<String, Long> actorsByNMovies) {
//        Objects.requireNonNull(actorsByNMovies);
//        return actorsByNMovies.entrySet().stream()
//                .map(entry -> new ActorMovieCount(entry.getKey(), entry.getValue()))
//                .collect(
//                        Collectors.maxBy(Comparator.comparingLong(ActorMovieCount::movieCount))
//                );
//    }

    public static Optional<ActorMovieCount> actorInMostMovies(Map<String, Long> actorsByNMovies) {
        Objects.requireNonNull(actorsByNMovies);
        return actorsByNMovies.entrySet().stream()
                .map(entry -> new ActorMovieCount(entry.getKey(), entry.getValue()))
                .max(Comparator.comparingLong(ActorMovieCount::movieCount));
    }
}
