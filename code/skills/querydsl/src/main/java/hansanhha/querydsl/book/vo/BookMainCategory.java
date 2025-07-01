package hansanhha.querydsl.book.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookMainCategory {

    ETC("000", "총류"),
    PHILOSOPHY("100", "철학"),
    RELIGION("200", "종교"),
    SOCIAL_SCIENCE("300", "사회과학"),
    NATURAL_SCIENCE("400", "자연과학"),
    TECHNOLOGY_SCIENCE("500", "기술과학"),
    ART("600", "예술"),
    LANGUAGE("700", "언어"),
    LITERATURE("800", "문학"),
    HISTORY("900", "역사");

    private final String code;
    private final String displayName;

    public boolean contains(BookMiddleCategory middleCategory) {
        return this.equals(middleCategory.getParentCategory());
    }
}
