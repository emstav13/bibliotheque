package fr.bibliotheque.repository;

import fr.bibliotheque.model.LibraryBook;
import fr.bibliotheque.model.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryBookRepository extends JpaRepository<LibraryBook, Long> {

    boolean existsByUserIdAndOpenLibraryId(Long userId, String openLibraryId);

    List<LibraryBook> findByUserId(Long userId);

    List<LibraryBook> findByUserIdAndStatus(Long userId, ReadingStatus status);
}