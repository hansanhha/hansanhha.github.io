package hansanhha.querydsl.book.dto;

import hansanhha.querydsl.book.entity.Book;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record BookResponse(
        UUID isbn,
        String title,
        String author,
        String status,
        String categoryCode,
        String categoryName) {

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getStatus().getDisplayName(),
                book.getCategory().getSubCategory().getCode(),
                book.getCategory().getSubCategory().getDisplayName());
    }

    public static List<BookResponse> from(Collection<Book> books) {
        return books.stream().map(BookResponse::from).toList();
    }
}
