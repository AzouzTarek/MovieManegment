package com.example.Movie_Management.service;

import com.example.Movie_Management.model.Movie;
import com.example.Movie_Management.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    public List<Movie> getMoviesByDirector(String director) {
        return movieRepository.findByDirector(director);
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

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