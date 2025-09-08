package hansanhha.querydsl.book.repository.v2.expression;

import com.querydsl.core.types.dsl.BooleanExpression;
import hansanhha.querydsl.book.entity.QBook;

public interface BookSearchExpression {

    default BooleanExpression buildEqualExpression(QBook book) {
        throw new UnsupportedOperationException();
    }

    default BooleanExpression buildContainsExpression(QBook book) {
        throw new UnsupportedOperationException();
    }

}
