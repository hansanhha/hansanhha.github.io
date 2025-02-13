package hansanhha.querydsl.book.repository.v1.expression;

import com.querydsl.core.types.dsl.BooleanExpression;
import hansanhha.querydsl.book.entity.BookCategory;
import hansanhha.querydsl.book.entity.QBook;
import hansanhha.querydsl.book.vo.BookMainCategory;
import hansanhha.querydsl.book.vo.BookMiddleCategory;
import hansanhha.querydsl.book.vo.BookSubCategory;

import static hansanhha.querydsl.book.repository.v1.expression.BookCategoryExpressions.BookCategoryBooleanExpressions.*;

public class BookCategoryExpressions {

    public static BooleanExpression buildEqualExpression(QBook book, BookCategory category) {
        BooleanExpression condition = null;

        BookMainCategory mainCategory = category.getMainCategory();
        BookMiddleCategory middleCategory = category.getMiddleCategory();
        BookSubCategory subCategory = category.getSubCategory();

        if (mainCategory != null) {
            condition = mainCategoryEq(book, mainCategory);
        }

        if (middleCategory != null) {
            if (mainCategory != null && !mainCategory.contains(middleCategory)) {
                throw new IllegalArgumentException("주요 카테고리와 중간 카테고리가 잘못 지정되었습니다");
            }

            condition = (condition == null)
                    ? middleCategoryEq(book, middleCategory)
                    : condition.and(middleCategoryEq(book, middleCategory));
        }

        if (subCategory != null) {
            if (middleCategory != null && !middleCategory.contains(subCategory)) {
                throw new IllegalArgumentException("중간 카테고리와 서브 카테고리가 잘못 지정되었습니다");
            }

            condition = (condition == null)
                    ? subCategoryEq(book,subCategory)
                    : condition.and(subCategoryEq(book,subCategory));
        }

        return condition;
    }


    static class BookCategoryBooleanExpressions {

        static BooleanExpression mainCategoryEq(QBook book, BookMainCategory mainCategory) {
            return book.category.mainCategory.eq(mainCategory);
        }

        static BooleanExpression middleCategoryEq(QBook book, BookMiddleCategory middleCategory) {
            return book.category.middleCategory.eq(middleCategory);
        }

        static BooleanExpression subCategoryEq(QBook book, BookSubCategory subCategory) {
            return book.category.subCategory.eq(subCategory);
        }
    }

}
