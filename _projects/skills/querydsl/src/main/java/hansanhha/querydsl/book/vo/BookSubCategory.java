package hansanhha.querydsl.book.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static hansanhha.querydsl.book.vo.BookMiddleCategory.*;

@Getter
@RequiredArgsConstructor
public enum BookSubCategory {

    COMPUTER_SCIENCE(MIDDLE_ETC, "004", "컴퓨터과학"),
    PROGRAMMING_PROGRAM_DATA(MIDDLE_ETC, "005", "프로그래밍, 프로그램, 데이터"),

    KOREAN_ENCYCLOPEDIA(ENCYCLOPEDIA, "031", "한국어"),
    JAPANESE_ENCYCLOPEDIA(ENCYCLOPEDIA, "033", "일본어"),

    DEDUCTIVE_METHOD(LOGIC, "171", "연역법"),
    INDUCTION(LOGIC, "172", "귀납법"),

    BIBLE(CHRISTIANITY, "233", "성서(성경)"),

    ARMY(MILITARY_SCIENCE, "396", "육군"),
    NAVY(MILITARY_SCIENCE, "397", "해군"),
    AIR_FORCE(MILITARY_SCIENCE, "398", "공군"),

    KOREAN_NOVEL(KOREAN_LITERATURE, "813", "한국소설");

    private final BookMiddleCategory parentCategory;
    private final String code;
    private final String displayName;
}
