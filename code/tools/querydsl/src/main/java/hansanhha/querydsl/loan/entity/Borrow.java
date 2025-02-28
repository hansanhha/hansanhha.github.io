package hansanhha.querydsl.loan.entity;

import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.BaseEntity;
import hansanhha.querydsl.loan.BorrowStatus;
import hansanhha.querydsl.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Borrow extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id")
    private User borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false, updatable = false)
    private LocalDateTime bookBorrowDate;

    @Enumerated(EnumType.STRING)
    private BorrowStatus status;

    @Column(nullable = false)
    private LocalDate bookReturnDeadlineDate;

    private LocalDateTime extensionApplyDate;
    private Integer extensionBookReturnCount;

    private LocalDate bookReturnDate;
    private Boolean overdue;
    private Long overduePeriod;

    public Period overdueReturn(LocalDate returnDate) {
        Period overduePeriod = Period.between(bookReturnDeadlineDate, returnDate);

        this.overdue = true;
        this.overduePeriod = overduePeriod.get(ChronoUnit.DAYS);
        this.status = BorrowStatus.OVERDUE_RETURN;
        this.bookReturnDate = returnDate;

        return overduePeriod;
    }

    public void normalReturn(LocalDate returnDate) {
        this.bookReturnDate = returnDate;
        this.status = BorrowStatus.NORMAL_RETURN;
    }

    public void validateStatusBeforeReturn() {
        if (status.isReturned()) {
            throw new IllegalArgumentException("이미 반납된 도서입니다");
        }
    }
}
