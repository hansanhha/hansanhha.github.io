package hansanhha.querydsl.book.entity;

import hansanhha.querydsl.BaseEntity;
import hansanhha.querydsl.book.vo.BookStatus;
import hansanhha.querydsl.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Book extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private UUID isbn;

    private String title;

    private String author;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Embedded
    private BookCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id")
    private User borrower;

    public void borrow(User user) {
        borrower = user;
        status = BookStatus.BORROWED;
    }

    public void returns() {
        borrower = null;
        status = BookStatus.BORROW_AVAILABLE;
    }

    public void validateStatus() {
        if (getBorrower() != null && status.isBorrowing()) {
            throw new IllegalStateException("이미 대여 중인 도서입니다");
        }
        else if (!status.equals(BookStatus.BORROW_AVAILABLE)) {
            throw new IllegalStateException("대여할 수 없는 도서입니다");
        }
    }
}
