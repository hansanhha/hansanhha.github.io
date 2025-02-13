package hansanhha.querydsl.book;

import hansanhha.querydsl.book.dto.BookResponse;
import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.book.entity.BookCategory;
import hansanhha.querydsl.book.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BookUserService {

    private final BookRepository bookRepository;

    public Page<BookResponse> getBooksByCategory(BookCategory category, Pageable pageable) {
        Page<Book> books = bookRepository.findAllByCategory(category, pageable);

        return books.map(BookResponse::from);
    }

    public Page<BookResponse> getBooksByTitle(String title, Pageable pageable) {
        Page<Book> books = bookRepository.findAllByTitleContainingIgnoreCase(title, pageable);

        return books.map(BookResponse::from);
    }

    public Page<BookResponse> getBooksByAuthor(String author, Pageable pageable) {
        Page<Book> books = bookRepository.findAllByAuthorContainingIgnoreCase(author, pageable);

        return books.map(BookResponse::from);
    }

    public Page<BookResponse> getBooksV2(BookCategory category, String title, String author, boolean ignoreCase, Pageable pageable) {
        Page<Book> books = bookRepository.findBooks(category, title, author, ignoreCase, pageable);
        return books.map(BookResponse::from);
    }

    public BookResponse getBookByIsbn(UUID isbn) {
        return bookRepository.findBookByIsbn(isbn)
                .map(BookResponse::from)
                .orElseThrow(EntityNotFoundException::new);
    }
}
