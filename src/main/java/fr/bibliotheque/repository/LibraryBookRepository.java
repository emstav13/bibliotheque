package fr.bibliotheque.repository;

import fr.bibliotheque.model.LibraryBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryBookRepository extends JpaRepository<LibraryBook, Long> {

    boolean existsByUserIdAndOpenLibraryId(Long userId, String openLibraryId);
}