package fr.bibliotheque.controller;

import fr.bibliotheque.dto.AddLibraryBookRequest;
import fr.bibliotheque.model.LibraryBook;
import fr.bibliotheque.model.ReadingStatus;
import fr.bibliotheque.model.User;
import fr.bibliotheque.repository.LibraryBookRepository;
import fr.bibliotheque.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}