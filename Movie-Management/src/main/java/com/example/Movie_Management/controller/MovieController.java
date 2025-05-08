package com.example.Movie_Management.controller;

import com.example.Movie_Management.model.Movie;
import com.example.Movie_Management.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movie Controller", description = "API pour gérer les films")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les films", description = "Récupère la liste de tous les films disponibles")
    @ApiResponse(responseCode = "200", description = "Liste des films récupérée avec succès")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un film par ID", description = "Récupère un film spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film trouvé",
                    content = @Content(schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "404", description = "Film non trouvé")
    })
    public ResponseEntity<Movie> getMovieById(
            @Parameter(description = "ID du film à récupérer") @PathVariable Long id) {
        return movieService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/genre/{genre}")
    @Operation(summary = "Récupérer les films par genre", description = "Récupère tous les films d'un genre spécifique")
    public ResponseEntity<List<Movie>> getMoviesByGenre(
            @Parameter(description = "Genre des films à récupérer") @PathVariable String genre) {
        return ResponseEntity.ok(movieService.getMoviesByGenre(genre));
    }

    @GetMapping("/director/{director}")
    @Operation(summary = "Récupérer les films par réalisateur", description = "Récupère tous les films d'un réalisateur spécifique")
    public ResponseEntity<List<Movie>> getMoviesByDirector(
            @Parameter(description = "Réalisateur des films à récupérer") @PathVariable String director) {
        return ResponseEntity.ok(movieService.getMoviesByDirector(director));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau film", description = "Ajoute un nouveau film à la base de données")
    @ApiResponse(responseCode = "201", description = "Film créé avec succès")
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        return new ResponseEntity<>(movieService.saveMovie(movie), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un film", description = "Met à jour les informations d'un film existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Film non trouvé")
    })
    public ResponseEntity<Movie> updateMovie(
            @Parameter(description = "ID du film à mettre à jour") @PathVariable Long id,
            @RequestBody Movie movie) {
        try {
            return ResponseEntity.ok(movieService.updateMovie(id, movie));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un film", description = "Supprime un film de la base de données")
    @ApiResponse(responseCode = "204", description = "Film supprimé avec succès")
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "ID du film à supprimer") @PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}