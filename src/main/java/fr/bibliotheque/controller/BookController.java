package fr.bibliotheque.controller;

import tools.jackson.databind.JsonNode;
import fr.bibliotheque.service.OpenLibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final OpenLibraryService openLibraryService;

    public BookController(OpenLibraryService openLibraryService) {
        this.openLibraryService = openLibraryService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam("q") String query) {
        try {
            JsonNode result = openLibraryService.searchBooks(query);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", exception.getMessage()));
        } catch (RestClientResponseException exception) {
            return ResponseEntity.status(exception.getStatusCode())
                    .body(Map.of("error", "Open Library a refusé la requête."));
        } catch (RestClientException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Impossible de contacter Open Library."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookDetails(@PathVariable String id) {
        try {
            JsonNode result = openLibraryService.getBookDetails(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", exception.getMessage()));
        } catch (RestClientResponseException exception) {
            return ResponseEntity.status(exception.getStatusCode())
                    .body(Map.of("error", "Livre introuvable dans Open Library."));
        } catch (RestClientException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Impossible de contacter Open Library."));
        }
    }
}
