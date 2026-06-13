package hansanhha.querydsl.book.repository.v1.expression;

import com.querydsl.core.types.dsl.BooleanExpression;
import hansanhha.querydsl.book.entity.QBook;
import org.springframework.util.StringUtils;

public class BookTitleExpressions {

    public static BooleanExpression buildContainsExpression(QBook book, String title, boolean ignoreCase) {
        if (!StringUtils.hasText(title)) {
            return null;
        }

        return ignoreCase
                ? book.title.containsIgnoreCase(title)
                : book.title.contains(title);
    }
}
