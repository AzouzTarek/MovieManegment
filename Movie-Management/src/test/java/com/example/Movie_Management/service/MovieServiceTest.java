package com.example.Movie_Management.service;

import com.example.Movie_Management.model.Movie;
import com.example.Movie_Management.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    void setUp() {
        movie1 = new Movie("The Shawshank Redemption", "Frank Darabont", LocalDate.of(1994, 9, 23), "Drama", 9.3);
        movie1.setId(1L);

        movie2 = new Movie("The Godfather", "Francis Ford Coppola", LocalDate.of(1972, 3, 24), "Crime", 9.2);
        movie2.setId(2L);
    }

    @Test
    void getAllMovies_ShouldReturnAllMovies() {
        // Arrange
        when(movieRepository.findAll()).thenReturn(Arrays.asList(movie1, movie2));

        // Act
        List<Movie> result = movieService.getAllMovies();

        // Assert
        assertEquals(2, result.size());
        assertEquals("The Shawshank Redemption", result.get(0).getTitle());
        assertEquals("The Godfather", result.get(1).getTitle());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void getMovieById_WithExistingId_ShouldReturnMovie() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));

        // Act
        Optional<Movie> result = movieService.getMovieById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("The Shawshank Redemption", result.get().getTitle());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovieById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        when(movieRepository.findById(3L)).thenReturn(Optional.empty());

        // Act
        Optional<Movie> result = movieService.getMovieById(3L);

        // Assert
        assertFalse(result.isPresent());
        verify(movieRepository, times(1)).findById(3L);
    }

    @Test
    void getMoviesByGenre_ShouldReturnMoviesOfThatGenre() {
        // Arrange
        when(movieRepository.findByGenre("Drama")).thenReturn(List.of(movie1));

        // Act
        List<Movie> result = movieService.getMoviesByGenre("Drama");

        // Assert
        assertEquals(1, result.size());
        assertEquals("The Shawshank Redemption", result.get(0).getTitle());
        verify(movieRepository, times(1)).findByGenre("Drama");
    }

    @Test
    void getMoviesByDirector_ShouldReturnMoviesOfThatDirector() {
        // Arrange
        when(movieRepository.findByDirector("Frank Darabont")).thenReturn(List.of(movie1));

        // Act
        List<Movie> result = movieService.getMoviesByDirector("Frank Darabont");

        // Assert
        assertEquals(1, result.size());
        assertEquals("The Shawshank Redemption", result.get(0).getTitle());
        verify(movieRepository, times(1)).findByDirector("Frank Darabont");
    }

    @Test
    void saveMovie_ShouldReturnSavedMovie() {
        // Arrange
        Movie newMovie = new Movie("Pulp Fiction", "Quentin Tarantino", LocalDate.of(1994, 10, 14), "Crime", 8.9);
        when(movieRepository.save(any(Movie.class))).thenReturn(newMovie);

        // Act
        Movie result = movieService.saveMovie(newMovie);

        // Assert
        assertEquals("Pulp Fiction", result.getTitle());
        verify(movieRepository, times(1)).save(newMovie);
    }

    @Test
    void deleteMovie_ShouldCallRepositoryDeleteById() {
        // Arrange
        doNothing().when(movieRepository).deleteById(anyLong());

        // Act
        movieService.deleteMovie(1L);

        // Assert
        verify(movieRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateMovie_WithExistingId_ShouldReturnUpdatedMovie() {
        // Arrange
        Movie updatedMovie = new Movie("The Shawshank Redemption (Updated)", "Frank Darabont",
                LocalDate.of(1994, 9, 23), "Drama", 9.5);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Movie result = movieService.updateMovie(1L, updatedMovie);

        // Assert
        assertEquals("The Shawshank Redemption (Updated)", result.getTitle());
        assertEquals(9.5, result.getRating());
        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void updateMovie_WithNonExistingId_ShouldThrowException() {
        // Arrange
        Movie updatedMovie = new Movie("Non-existing Movie", "Unknown Director",
                LocalDate.of(2000, 1, 1), "Unknown", 5.0);

        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            movieService.updateMovie(99L, updatedMovie);
        });

        verify(movieRepository, times(1)).findById(99L);
        verify(movieRepository, never()).save(any(Movie.class));
    }
}