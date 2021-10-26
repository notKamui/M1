package fr.umlv.movie;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("static-method")
public class MoviesTest {
  @Test
  @Tag("Q1")
  public void newMovie() {
    var movie =
        new Movie("Clerks. (1994)", List.of("Kevin Smith", "Brian O'Halloran", "Jeff Anderson"));
    assertAll(
        () -> assertEquals("Clerks. (1994)", movie.title()),
        () -> assertEquals("Kevin Smith", movie.actors().get(0)),
        () -> assertEquals("Brian O'Halloran", movie.actors().get(1)),
        () -> assertEquals("Jeff Anderson", movie.actors().get(2)));
  }

  @Test
  @Tag("Q1")
  public void newMovieNonMutable() {
    var actors = new ArrayList<String>();
    actors.add("Matt Damon");
    var movie = new Movie("The Martian", actors);
    actors.add("Jimmy Kimmel");
    assertEquals(List.of("Matt Damon"), movie.actors());
  }

  @Test
  @Tag("Q1")
  public void newMovieNonMutable2() {
    var actors = new ArrayList<String>();
    actors.add("Matt Damon");
    var movie = new Movie("The Martian", actors);
    assertThrows(UnsupportedOperationException.class, () -> movie.actors().add("Jimmy Kimmel"));
  }

  @Test
  @Tag("Q1")
  public void newMovieNPE() {
    assertAll(
        () -> assertThrows(NullPointerException.class, () -> new Movie(null, List.of())),
        () -> assertThrows(NullPointerException.class, () -> new Movie("Indiana Jones", null)));
  }

  @Test
  @Tag("Q2")
  public void movies() throws IOException {
    var path = Path.of("movies.txt");
    var movies = Movies.movies(path);
    assertEquals(14_129, movies.size());
  }

  @Test
  @Tag("Q2")
  public void moviesNonModifiable() throws IOException {
    var path = Path.of("movies.txt");
    var movies = Movies.movies(path);
    assertThrows(UnsupportedOperationException.class, () -> {
      movies.add(new Movie("The Revenge 2", List.of("Angela Basset")));
    });
  }

  @Test
  @Tag("Q3")
  public void movieMap() throws IOException {
    var path = Path.of("movies.txt");
    var movies = Movies.movies(path);
    var movieMap = Movies.movieMap(movies);

    var expectedMovies =
        List.of(
            new Movie("Corps perdus (1990)", List.of("Tchéky Karyo", "Laura Morante")),
            new Movie(
                "Vampires: Los Muertos (2002)",
                List.of("Jon Bon Jovi", "Tim Guinee", "Arly Jover")),
            new Movie(
                "Clerks: Sell Out (2002)",
                List.of("Jeff Anderson", "Jason Mewes", "Brian O'Halloran", "Kevin Smith")),
            new Movie(
                "Punchdrunk Knuckle Love (2002)",
                List.of("Philip Seymour Hoffman", "Adam Sandler", "Emily Watson")),
            new Movie("Spirit: Stallion of the Cimarron (2002)", List.of("Matt Damon")));
    expectedMovies.forEach(movie -> assertEquals(movie, movieMap.get(movie.title())));
  }

  @Test
  @Tag("Q3")
  public void movieMapNonModifiable() throws IOException {
    var path = Path.of("movies.txt");
    var movies = Movies.movies(path);
    var movieMap = Movies.movieMap(movies);

    assertThrows(UnsupportedOperationException.class, () -> {
      movieMap.put("Hailey Basset", new Movie("The revenge", List.of("Hailey Basset")));
    });
  }

  @Test
  @Tag("Q4")
  public void numberOfUniqueActors() throws IOException {
    var path = Path.of("movies.txt");
    var movies = Movies.movies(path);
    var numberOfUniqueActors = Movies.numberOfUniqueActors(movies);

    assertEquals(170_509L, numberOfUniqueActors);
  }

  @Test
  @Tag("Q5")
  public void numberOfMoviesByActor() throws IOException {
    var path = Path.of("movies.txt");
    var movies = Movies.movies(path);
    var numberOfMoviesByActor = Movies.numberOfMoviesByActor(movies);

    var expectedActorCouples =
        List.of(
            Map.entry("Emily Watson", 9L),
            Map.entry("Kevin Smith", 13L),
            Map.entry("Adam Sandler", 17L),
            Map.entry("Matt Damon", 21L),
            Map.entry("Tchéky Karyo", 22L));
    expectedActorCouples.forEach(
        entry -> assertEquals(entry.getValue(), numberOfMoviesByActor.get(entry.getKey())));
  }

  @Test
  @Tag("Q6")
  public void actorInMostMovies() throws IOException {
    var path = Path.of("movies.txt");
    var movies = Movies.movies(path);
    var numberOfMoviesByActor = Movies.numberOfMoviesByActor(movies);
    var actorInMostMovies = Movies.actorInMostMovies(numberOfMoviesByActor).orElseThrow();

    assertAll(
        () -> assertEquals("Frank Welker", actorInMostMovies.actor()),
        () -> assertEquals(92, actorInMostMovies.movieCount()));
  }
}
