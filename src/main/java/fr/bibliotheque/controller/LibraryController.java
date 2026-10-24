package fr.bibliotheque.controller;

import fr.bibliotheque.dto.AddLibraryBookRequest;
import fr.bibliotheque.dto.UpdateStatusRequest;
import fr.bibliotheque.model.LibraryBook;
import fr.bibliotheque.model.ReadingStatus;
import fr.bibliotheque.model.User;
import fr.bibliotheque.repository.LibraryBookRepository;
import fr.bibliotheque.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryBookRepository libraryBookRepository;
    private final UserRepository userRepository;

    public LibraryController(LibraryBookRepository libraryBookRepository, UserRepository userRepository) {
        this.libraryBookRepository = libraryBookRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> addBook(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody AddLibraryBookRequest request) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur introuvable."));
        }

        if (request.getOpenLibraryId() == null || request.getOpenLibraryId().isBlank()
                || request.getTitle() == null || request.getTitle().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "L'identifiant Open Library et le titre sont obligatoires."));
        }

        String openLibraryId = request.getOpenLibraryId().trim();
        if (libraryBookRepository.existsByUserIdAndOpenLibraryId(userId, openLibraryId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ce livre est déjà dans la bibliothèque."));
        }

        ReadingStatus status = ReadingStatus.A_LIRE;
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                status = ReadingStatus.valueOf(request.getStatus().trim());
            } catch (IllegalArgumentException exception) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Statut invalide. Utilisez A_LIRE, EN_COURS ou LU."));
            }
        }

        LibraryBook book = new LibraryBook();
        book.setUser(user);
        book.setOpenLibraryId(openLibraryId);
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor());
        book.setCoverUrl(request.getCoverUrl());
        book.setPublishYear(request.getPublishYear());
        book.setStatus(status);
        book.setRating(request.getRating());
        book.setComment(request.getComment());

        LibraryBook saved = libraryBookRepository.save(book);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", saved.getId(),
                "openLibraryId", saved.getOpenLibraryId(),
                "title", saved.getTitle(),
                "status", saved.getStatus().name()
        ));
    }

    @GetMapping
    public ResponseEntity<?> listBooks(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {

        if (userRepository.findById(userId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur introuvable."));
        }

        List<LibraryBook> books;
        if (status == null || status.isBlank()) {
            books = libraryBookRepository.findByUserId(userId);
        } else {
            try {
                ReadingStatus readingStatus = ReadingStatus.valueOf(status.trim());
                books = libraryBookRepository.findByUserIdAndStatus(userId, readingStatus);
            } catch (IllegalArgumentException exception) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Statut invalide. Utilisez A_LIRE, EN_COURS ou LU."));
            }
        }

        if ("title".equals(sort)) {
            books.sort(Comparator.comparing(LibraryBook::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)));
        } else {
            books.sort(Comparator.comparing(LibraryBook::getId).reversed());
        }

        List<Map<String, Object>> response = new ArrayList<>();
        for (LibraryBook book : books) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", book.getId());
            item.put("openLibraryId", book.getOpenLibraryId());
            item.put("title", book.getTitle());
            item.put("author", book.getAuthor());
            item.put("coverUrl", book.getCoverUrl());
            item.put("publishYear", book.getPublishYear());
            item.put("status", book.getStatus().name());
            item.put("rating", book.getRating());
            item.put("comment", book.getComment());
            response.add(item);
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {

        if (userRepository.findById(userId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur introuvable."));
        }

        LibraryBook book = libraryBookRepository.findByIdAndUserId(id, userId).orElse(null);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Livre introuvable."));
        }

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le statut est obligatoire."));
        }

        try {
            book.setStatus(ReadingStatus.valueOf(request.getStatus().trim()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Statut invalide. Utilisez A_LIRE, EN_COURS ou LU."));
        }

        if (request.getRating() != null) {
            book.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            book.setComment(request.getComment());
        }

        LibraryBook saved = libraryBookRepository.save(book);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", saved.getId());
        response.put("title", saved.getTitle());
        response.put("status", saved.getStatus().name());
        response.put("rating", saved.getRating());
        response.put("comment", saved.getComment());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        if (userRepository.findById(userId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur introuvable."));
        }

        LibraryBook book = libraryBookRepository.findByIdAndUserId(id, userId).orElse(null);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Livre introuvable."));
        }

        libraryBookRepository.delete(book);
        return ResponseEntity.noContent().build();
    }
}