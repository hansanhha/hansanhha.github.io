package hansanhha.querydsl.book.entity;

import hansanhha.querydsl.book.vo.BookMainCategory;
import hansanhha.querydsl.book.vo.BookMiddleCategory;
import hansanhha.querydsl.book.vo.BookSubCategory;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookCategory {

    @Enumerated(EnumType.STRING)
    private BookMainCategory mainCategory;

    @Enumerated(EnumType.STRING)
    private BookMiddleCategory middleCategory;

    @Enumerated(EnumType.STRING)
    private BookSubCategory subCategory;

    public static BookCategory from(BookMainCategory main) {
        return new BookCategory(main, null, null);
    }

    public static BookCategory from(BookMiddleCategory middle) {
        return new BookCategory(middle.getParentCategory(), middle, null);
    }

    public static BookCategory from(BookSubCategory sub) {
        BookMiddleCategory middle = sub.getParentCategory();
        BookMainCategory main = middle.getParentCategory();

        return new BookCategory(main, middle, sub);
    }
}
