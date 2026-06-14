package hansanhha.querydsl.book.repository.v1.expression;

import com.querydsl.core.types.dsl.BooleanExpression;
import hansanhha.querydsl.book.entity.QBook;
import org.springframework.util.StringUtils;

public class BookAuthorExpressions {

    public static BooleanExpression buildContainsExpression(QBook book, String author, boolean ignoreCase) {
        if (!StringUtils.hasText(author)) {
            return null;
        }

        return ignoreCase
                ? book.author.containsIgnoreCase(author)
                : book.author.contains(author);
    }
}
