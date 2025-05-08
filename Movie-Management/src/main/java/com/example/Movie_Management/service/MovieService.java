package com.example.Movie_Management.service;

import com.example.Movie_Management.model.Movie;
import com.example.Movie_Management.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Cacheable(value = "movies", key = "'all'")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Cacheable(value = "movies", key = "#id")
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    @Cacheable(value = "moviesByGenre", key = "#genre")
    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    @Cacheable(value = "moviesByDirector", key = "#director")
    public List<Movie> getMoviesByDirector(String director) {
        return movieRepository.findByDirector(director);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "movies", key = "'all'"),
                    @CacheEvict(value = "moviesByGenre", allEntries = true),
                    @CacheEvict(value = "moviesByDirector", allEntries = true)
            }
    )
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "movies", key = "#id"),
                    @CacheEvict(value = "movies", key = "'all'"),
                    @CacheEvict(value = "moviesByGenre", allEntries = true),
                    @CacheEvict(value = "moviesByDirector", allEntries = true)
            }
    )
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    @Caching(
            put = {
                    @CachePut(value = "movies", key = "#id")
            },
            evict = {
                    @CacheEvict(value = "movies", key = "'all'"),
                    @CacheEvict(value = "moviesByGenre", allEntries = true),
                    @CacheEvict(value = "moviesByDirector", allEntries = true)
            }
    )
    public Movie updateMovie(Long id, Movie movieDetails) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));

        movie.setTitle(movieDetails.getTitle());
        movie.setDirector(movieDetails.getDirector());
        movie.setReleaseDate(movieDetails.getReleaseDate());
        movie.setGenre(movieDetails.getGenre());
        movie.setRating(movieDetails.getRating());

        return movieRepository.save(movie);
    }
}