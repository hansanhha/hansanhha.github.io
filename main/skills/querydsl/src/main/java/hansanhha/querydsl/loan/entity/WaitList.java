package hansanhha.querydsl.loan.entity;

import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.BaseEntity;
import hansanhha.querydsl.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class WaitList extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private Integer waitOrder;

    private Boolean isCancel;
}
