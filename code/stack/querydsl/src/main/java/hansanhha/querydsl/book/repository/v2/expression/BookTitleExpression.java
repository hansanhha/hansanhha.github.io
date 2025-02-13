package hansanhha.querydsl.book.repository.v2.expression;

import com.querydsl.core.types.dsl.BooleanExpression;
import hansanhha.querydsl.book.entity.QBook;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookTitleExpression implements BookSearchExpression {

    private final String title;
    private final boolean ignoreCase;

    @Override
    public BooleanExpression buildContainsExpression(QBook book) {
        return ignoreCase
                ? book.title.containsIgnoreCase(title)
                : book.title.contains(title);
    }
}
