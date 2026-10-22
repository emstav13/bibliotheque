package fr.bibliotheque.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenLibraryService {

    private final RestClient restClient;

    public OpenLibraryService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://openlibrary.org")
                .build();
    }

    public JsonNode searchBooks(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Le paramètre q est obligatoire.");
        }

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.json")
                        .queryParam("q", query.trim())
                        .queryParam("limit", 10)
                        .build())
                .retrieve()
                .body(JsonNode.class);
    }

    /**
     * Accepts a work ID (OL...W) or an edition ID (OL...M).
     */
    public JsonNode getBookDetails(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identifiant du livre est obligatoire.");
        }

        String normalizedId = id.trim();

        if (normalizedId.matches("OL[0-9]+W")) {
            return restClient.get()
                    .uri("/works/{id}.json", normalizedId)
                    .retrieve()
                    .body(JsonNode.class);
        }

        if (normalizedId.matches("OL[0-9]+M")) {
            return restClient.get()
                    .uri("/books/{id}.json", normalizedId)
                    .retrieve()
                    .body(JsonNode.class);
        }

        throw new IllegalArgumentException(
                "Identifiant invalide. Utilisez un identifiant Open Library du type OL...W ou OL...M."
        );
    }
}
