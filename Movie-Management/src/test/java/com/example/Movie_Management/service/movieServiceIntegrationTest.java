package com.example.Movie_Management.service;

import com.example.Movie_Management.model.Movie;
import com.example.Movie_Management.repository.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class movieServiceIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();

        testMovie = new Movie(
                "Inception",
                "Christopher Nolan",
                LocalDate.of(2010, 7, 16),
                "Sci-Fi",
                8.8
        );

        testMovie = movieRepository.save(testMovie);
    }

    @AfterEach
    void tearDown() {
        movieRepository.deleteAll();
    }

    @Test
    void getAllMovies_ShouldReturnAllMovies() {
        Movie anotherMovie = new Movie(
                "The Matrix",
                "The Wachowskis",
                LocalDate.of(1999, 3, 31),
                "Sci-Fi",
                8.7
        );
        movieRepository.save(anotherMovie);

        List<Movie> movies = movieService.getAllMovies();

        assertEquals(2, movies.size());
        assertTrue(movies.stream().anyMatch(m -> m.getTitle().equals("Inception")));
        assertTrue(movies.stream().anyMatch(m -> m.getTitle().equals("The Matrix")));
    }

    @Test
    void getMovieById_WithExistingId_ShouldReturnMovie() {
        Optional<Movie> foundMovie = movieService.getMovieById(testMovie.getId());

        assertTrue(foundMovie.isPresent());
        assertEquals("Inception", foundMovie.get().getTitle());
        assertEquals("Christopher Nolan", foundMovie.get().getDirector());
    }

    @Test
    void getMovieById_WithNonExistingId_ShouldReturnEmpty() {
        Optional<Movie> foundMovie = movieService.getMovieById(999L);

        assertFalse(foundMovie.isPresent());
    }

    @Test
    void getMoviesByGenre_ShouldReturnMoviesOfThatGenre() {
        Movie anotherSciFiMovie = new Movie(
                "The Matrix",
                "The Wachowskis",
                LocalDate.of(1999, 3, 31),
                "Sci-Fi",
                8.7
        );
        Movie dramaMovie = new Movie(
                "The Shawshank Redemption",
                "Frank Darabont",
                LocalDate.of(1994, 9, 23),
                "Drama",
                9.3
        );
        movieRepository.saveAll(List.of(anotherSciFiMovie, dramaMovie));

        List<Movie> sciFiMovies = movieService.getMoviesByGenre("Sci-Fi");
        List<Movie> dramaMovies = movieService.getMoviesByGenre("Drama");

        assertEquals(2, sciFiMovies.size());
        assertEquals(1, dramaMovies.size());
        assertTrue(sciFiMovies.stream().allMatch(m -> m.getGenre().equals("Sci-Fi")));
        assertTrue(dramaMovies.stream().allMatch(m -> m.getGenre().equals("Drama")));
    }

    @Test
    void saveMovie_ShouldSaveAndReturnMovie() {
        Movie newMovie = new Movie(
                "Interstellar",
                "Christopher Nolan",
                LocalDate.of(2014, 11, 7),
                "Sci-Fi",
                8.6
        );

        Movie savedMovie = movieService.saveMovie(newMovie);

        assertNotNull(savedMovie.getId());
        assertEquals("Interstellar", savedMovie.getTitle());

        Optional<Movie> foundMovie = movieRepository.findById(savedMovie.getId());
        assertTrue(foundMovie.isPresent());
        assertEquals("Interstellar", foundMovie.get().getTitle());
    }

    @Test
    void updateMovie_WithExistingId_ShouldUpdateAndReturnMovie() {
        Movie updatedMovie = new Movie(
                "Inception (Director's Cut)",
                "Christopher Nolan",
                LocalDate.of(2010, 7, 16),
                "Sci-Fi",
                9.0
        );

        Movie result = movieService.updateMovie(testMovie.getId(), updatedMovie);

        assertEquals("Inception (Director's Cut)", result.getTitle());
        assertEquals(9.0, result.getRating());

        Optional<Movie> foundMovie = movieRepository.findById(testMovie.getId());
        assertTrue(foundMovie.isPresent());
        assertEquals("Inception (Director's Cut)", foundMovie.get().getTitle());
        assertEquals(9.0, foundMovie.get().getRating());
    }

    @Test
    void updateMovie_WithNonExistingId_ShouldThrowException() {
        Movie updatedMovie = new Movie(
                "Non-existing Movie",
                "Unknown Director",
                LocalDate.of(2000, 1, 1),
                "Unknown",
                5.0
        );

        assertThrows(RuntimeException.class, () -> {
            movieService.updateMovie(999L, updatedMovie);
        });
    }

    @Test
    void deleteMovie_ShouldRemoveMovieFromDatabase() {
        movieService.deleteMovie(testMovie.getId());

        Optional<Movie> foundMovie = movieRepository.findById(testMovie.getId());
        assertFalse(foundMovie.isPresent());
    }
}
