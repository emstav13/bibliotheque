
# 📚 Bibliothèque

Application web développée avec **Java et Spring Boot**, permettant de rechercher des livres grâce à l’API publique Open Library.

## 🎯 Objectif

L’objectif du projet est de créer une application facilitant la recherche et la consultation d’informations sur des livres, à travers une API REST et une interface web intuitive.

## ⚙️ Fonctionnalités

- Recherche de livres par titre ou auteur.
- Consultation des détails d’un livre.
- Récupération des données depuis Open Library au format JSON.
- Affichage des résultats dans une interface web.
- Présentation des titres, auteurs, années de publication et couvertures disponibles.

## 🛠️ Technologies

- Java 21
- Spring Boot
- Spring Web MVC
- RestClient
- Maven
- HTML, CSS et JavaScript
- API REST Open Library

## 🚀 Lancement du projet

Prérequis : JDK 21 et une connexion Internet.

Sous Windows, exécuter à la racine du projet :

```powershell
.\mvnw.cmd spring-boot:run
```

Puis ouvrir l’application :

http://localhost:8080/

## 👥 Organisation

Le projet est réparti en deux parties :

- **Personne 1 — Recherche de livres :** intégration de l’API Open Library et récupération des informations sur les livres.
- **Personne 2 — Bibliothèque personnelle :** gestion des livres enregistrés et de leur statut de lecture.

---

*Projet réalisé dans le cadre d’un développement d’application web avec Spring Boot et une API REST.*
