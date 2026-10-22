# Bibliotheque — Spring Boot

Application Spring Boot pour rechercher des livres via l'API publique Open Library.

## API

- `GET /api/books/search?q=harry%20potter` — recherche jusqu'à 10 livres.
- `GET /api/books/{id}` — détails d'une œuvre (`OL...W`) ou d'une édition (`OL...M`).

## Prérequis

- Java 21
- Maven

L'application utilise `spring-boot-starter-web` et appelle directement `https://openlibrary.org`.