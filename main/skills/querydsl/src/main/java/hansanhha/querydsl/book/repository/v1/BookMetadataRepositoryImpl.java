package hansanhha.querydsl.book.repository.v1;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.book.entity.BookCategory;
import hansanhha.querydsl.book.entity.QBook;
import hansanhha.querydsl.book.repository.v1.expression.BookAuthorExpressions;
import hansanhha.querydsl.book.repository.v1.expression.BookCategoryExpressions;
import hansanhha.querydsl.book.repository.v1.expression.BookTitleExpressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookMetadataRepositoryImpl implements BookMetadataRepository {

    private final JPAQueryFactory queryFactory;
    private final QBook book = QBook.book;

    @Override
    public Page<Book> findAllByCategory(BookCategory category, Pageable pageable) {
        BooleanExpression categoryExpression = BookCategoryExpressions.buildEqualExpression(book, category);
        return findBooksByExpressions(categoryExpression, pageable);
    }

    @Override
    public Page<Book> findAllByTitleContainingIgnoreCase(String title, Pageable pageable) {
        BooleanExpression titleExpression = BookTitleExpressions.buildContainsExpression(book, title, true);
        return findBooksByExpressions(titleExpression, pageable);
    }

    @Override
    public Page<Book> findAllByAuthorContainingIgnoreCase(String author, Pageable pageable) {
        BooleanExpression authorExpression = BookAuthorExpressions.buildContainsExpression(book, author, true);
        return findBooksByExpressions(authorExpression, pageable);
    }

    private Page<Book> findBooksByExpressions(BooleanExpression expression, Pageable pageable) {
        List<Book> books = queryFactory
                .selectFrom(book)
                .where(expression)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        return new PageImpl<>(books, pageable, books.size());
    }
}
