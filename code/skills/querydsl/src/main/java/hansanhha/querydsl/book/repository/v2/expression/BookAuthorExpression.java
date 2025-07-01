package hansanhha.querydsl.book.repository.v2.expression;

import com.querydsl.core.types.dsl.BooleanExpression;
import hansanhha.querydsl.book.entity.QBook;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookAuthorExpression implements BookSearchExpression {

    private final String author;
    private final boolean ignoreCase;

    @Override
    public BooleanExpression buildContainsExpression(QBook book) {
        return ignoreCase
                ? book.author.containsIgnoreCase(author)
                : book.author.contains(author);
    }
}
