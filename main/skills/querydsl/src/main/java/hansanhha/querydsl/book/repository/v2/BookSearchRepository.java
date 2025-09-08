package hansanhha.querydsl.book.repository.v2;

import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.book.entity.BookCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BookSearchRepository {

    Page<Book> findBooks(BookCategory category, String title, String author, boolean ignoreCase, Pageable pageable);

    Optional<Book> fetchBorrowerByIsbn(UUID isbn);
}
