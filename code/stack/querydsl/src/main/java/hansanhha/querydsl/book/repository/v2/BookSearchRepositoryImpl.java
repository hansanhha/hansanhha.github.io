package hansanhha.querydsl.book.repository.v2;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.book.entity.BookCategory;
import hansanhha.querydsl.book.entity.QBook;
import hansanhha.querydsl.book.repository.v2.expression.BookAuthorExpression;
import hansanhha.querydsl.book.repository.v2.expression.BookCategoryExpression;
import hansanhha.querydsl.book.repository.v2.expression.BookTitleExpression;
import hansanhha.querydsl.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BookSearchRepositoryImpl implements BookSearchRepository {

    private final JPAQueryFactory queryFactory;
    private final QBook book = QBook.book;

    @Override
    public Page<Book> findBooks(BookCategory category, String title, String author, boolean ignoreCase, Pageable pageable) {
        BooleanExpression expression = null;

        if (category != null) {
            expression = new BookCategoryExpression(category).buildEqualExpression(book);
        }

        if (StringUtils.hasText(title)) {
            expression = expression == null
                    ? new BookTitleExpression(title, ignoreCase).buildContainsExpression(book)
                    : expression.and(new BookTitleExpression(title, ignoreCase).buildContainsExpression(book));
        }

        if (StringUtils.hasText(author)) {
            expression = expression == null
                    ? new BookAuthorExpression(author, ignoreCase).buildContainsExpression(book)
                    : expression.and(new BookAuthorExpression(author, ignoreCase).buildContainsExpression(book));
        }

        List<Book> books = queryFactory.selectFrom(book)
                .where(expression)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        return new PageImpl<>(books, pageable, books.size());
    }

    @Override
    public Optional<Book> fetchBorrowerByIsbn(UUID isbn) {
        Book found = queryFactory
                .selectFrom(book)
                .leftJoin(book.borrower).fetchJoin()
                .where(book.isbn.eq(isbn))
                .fetchOne();

        return Optional.ofNullable(found);
    }
}
