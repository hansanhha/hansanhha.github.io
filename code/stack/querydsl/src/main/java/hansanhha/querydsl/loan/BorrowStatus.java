package hansanhha.querydsl.loan;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BorrowStatus {

    BORROWED("대여 중"),
    OVERDUE("연체됨"),

    NORMAL_RETURN("정상 반납됨"),
    OVERDUE_RETURN("연체 반납됨");

    private final String displayName;

    public boolean isReturned() {
        return this.equals(NORMAL_RETURN) || this.equals(OVERDUE_RETURN);
    }
}
