# Bibliothèque

**MATROUF Yassine** et **STAVRAKAKIS Emmanouil**

Application Spring Boot qui cherche des livres avec l’API Open Library et permet de garder une bibliothèque personnelle.

Yassine = comptes, bibliothèque personnelle (ajout, liste, statut, note, commentaire, suppression).

Emmanouil = connexion avec l'API Open Library, recherche de livres et fiche d'un livre.

Lancer : `.\mvnw.cmd spring-boot:run` puis http://localhost:8080/

Les comptes et les livres sont enregistrés dans une base H2 (fichier `data/bibliotheque`). Ils restent après un redémarrage.

## Valeur ajoutée

Open Library est un catalogue public. Il ne garde pas de compte, ni de liste de lecture, ni de note.

L’application ajoute :

- un compte utilisateur
- une bibliothèque par personne
- un statut : à lire, en cours, lu
- une note et un commentaire
- une page pour chercher, ajouter, modifier et supprimer

Le titre, l’auteur, l’année et la couverture sont copiés au moment de l’ajout. Ensuite, la bibliothèque s’affiche sans rappeler Open Library.

## Routes

Les routes de bibliothèque ont besoin du header `X-User-Id` (l’id renvoyé à l’inscription).

| Route | Rôle |
|---|---|
| `GET /api/books/search?q=...` | Rechercher un livre (titre, auteur, ISBN) |
| `GET /api/books/{id}` | Détail d’un livre Open Library (`OL...W` ou `OL...M`) |
| `POST /api/users` | Créer un compte |
| `GET /api/users/{id}` | Récupérer le nom d’un compte |
| `POST /api/library` | Ajouter un livre à sa bibliothèque |
| `GET /api/library` | Lister ses livres (`?status=LU`, `?sort=title`) |
| `PATCH /api/library/{id}/status` | Changer le statut, la note et le commentaire |
| `DELETE /api/library/{id}` | Supprimer un livre |

`{id}` sur `/api/library` est l’id du livre dans notre base, pas celui d’Open Library.
