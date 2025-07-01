package hansanhha.querydsl.book;

import hansanhha.querydsl.book.dto.BookResponse;
import hansanhha.querydsl.book.dto.CreateBookRequest;
import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.book.entity.BookCategory;
import hansanhha.querydsl.book.repository.BookRepository;
import hansanhha.querydsl.book.vo.BookStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@Transactional
@RequiredArgsConstructor
public class BookAdminService {

    private final BookRepository repository;

    public List<BookResponse> registerAll(List<CreateBookRequest> requests) {
        Iterable<Book> books = repository.saveAll(requests.stream().map(this::buildBook).toList());

        return StreamSupport.stream(books.spliterator(), false)
                .map(BookResponse::from)
                .toList();
    }

    public BookResponse register(CreateBookRequest request) {
        Book book = repository.save(buildBook(request));
        return BookResponse.from(book);
    }

    public BookResponse inactive(UUID isbn) {
        return repository.findByIsbn(isbn)
                .map(BookResponse::from)
                .orElseThrow(EntityNotFoundException::new);
    }

    private Book buildBook(CreateBookRequest request) {
        return Book.builder()
                .isbn(UUID.randomUUID())
                .title(request.title())
                .author(request.author())
                .category(BookCategory.from(request.category()))
                .status(BookStatus.BORROW_AVAILABLE)
                .build();
    }
}
