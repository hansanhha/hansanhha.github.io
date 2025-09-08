package hansanhha.querydsl.book.repository.v1;

import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.book.entity.BookCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookMetadataRepository {

    Page<Book> findAllByCategory(BookCategory category, Pageable pageable);

    Page<Book> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findAllByAuthorContainingIgnoreCase(String author, Pageable pageable);
}
