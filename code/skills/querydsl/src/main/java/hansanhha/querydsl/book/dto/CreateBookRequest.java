package hansanhha.querydsl.book.dto;

import hansanhha.querydsl.book.vo.BookSubCategory;

public record CreateBookRequest(
        String title,
        String author,
        BookSubCategory category) {
}
