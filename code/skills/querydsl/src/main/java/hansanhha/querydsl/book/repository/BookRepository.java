package hansanhha.querydsl.book.repository;

import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.book.repository.v1.BookMetadataRepository;
import hansanhha.querydsl.book.repository.v2.BookSearchRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends CrudRepository<Book, Long>, BookMetadataRepository, BookSearchRepository {

    Optional<Book> findByIsbn(UUID isbn);

    Optional<Book> findBookByIsbn(UUID isbn);
}
