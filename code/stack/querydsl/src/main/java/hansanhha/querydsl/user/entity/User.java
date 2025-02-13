package hansanhha.querydsl.user.entity;

import hansanhha.querydsl.BaseEntity;
import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.loan.entity.WaitList;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private UUID userNumber;

    private String name;

    private Integer totalBorrowCount;

    private Integer totalOverdueCount;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "borrower")
    private Set<Book> currentBorrowBooks = new HashSet<>(0);

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user")
    private Set<WaitList> currentWaitList = new HashSet<>(0);

    public static User create(String name) {
        User user = new User();
        user.userNumber = UUID.randomUUID();
        user.name = name;
        user.totalBorrowCount = 0;
        user.totalOverdueCount = 0;
        return user;
    }

    public void borrowBook(Book book) {
        currentBorrowBooks.add(book);
        totalBorrowCount++;
        book.borrow(this);
    }

    public void overdueReturnBook(Book book) {
        this.totalOverdueCount++;
        book.returns();
    }

    public void normalReturnBook(Book book) {
        book.returns();
    }
}
