# Bibliotheque — Spring Boot

Application Spring Boot pour rechercher des livres via l'API publique Open Library.

## Endpoints

- `GET /api/books/search?q=harry%20potter` — recherche jusqu'à 10 livres.
- `GET /api/books/OL82563W` — détails d'une œuvre Open Library (identifiant de type `OL...W`).
- `GET /api/books/OL...M` — détails d'une édition (identifiant de type `OL...M`).

Le service renvoie le JSON fourni par Open Library. Les identifiants utilisés dans `/api/books/{id}` sont les identifiants courts Open Library sans le préfixe `/works/` ou `/books/`.

## Prérequis

- Java 21
- Maven

L'application utilise Spring Boot Web MVC et appelle directement `https://openlibrary.org`.

## Lancer l'application

Depuis la racine du projet :

```bash
./mvnw spring-boot:run
```

Sous Windows :

```cmd
mvnw.cmd spring-boot:run
```

## Tester

Une fois l'application démarrée :

- Recherche : `http://localhost:8080/api/books/search?q=harry%20potter`
- Détails d'une œuvre : `http://localhost:8080/api/books/OL82563W`

Une connexion Internet est nécessaire pour interroger Open Library.
