package hansanhha.querydsl.loan;

import hansanhha.querydsl.book.BookAdminService;
import hansanhha.querydsl.book.BookUserService;
import hansanhha.querydsl.book.dto.BookResponse;
import hansanhha.querydsl.book.util.BookRequestFactory;
import hansanhha.querydsl.book.vo.BookStatus;
import hansanhha.querydsl.loan.dto.BorrowHistoryResponse;
import hansanhha.querydsl.loan.dto.BorrowResponse;
import hansanhha.querydsl.loan.dto.ReturnBookResponse;
import hansanhha.querydsl.user.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static hansanhha.querydsl.loan.BorrowService.MAXIMUM_BORROWABLE_BOOK_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BorrowServiceTest {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookUserService bookUserService;

    @Autowired
    private BookAdminService adminService;

    @Autowired
    private UserService userService;

    private UUID user1_userNumber;
    private UUID user2_userNumber;

    @BeforeAll
    void init() {
        adminService.registerAll(BookRequestFactory.getAll());
        user1_userNumber = userService.join("test user1");
        user2_userNumber = userService.join("test user2");
    }

    @Test
    @DisplayName("도서 조회 후 대여")
    void borrow() {
        Page<BookResponse> findBooks = bookUserService.getBooksByAuthor("한강", Pageable.ofSize(20));
        UUID isbn = findBooks.getContent().getFirst().isbn();

        BorrowResponse checkout = borrowService.borrow(user1_userNumber, isbn);

        BookResponse book = bookUserService.getBookByIsbn(isbn);

        assertThat(checkout).isNotNull();
        assertThat(book).isNotNull();
        assertThat(checkout.isbn()).isEqualTo(book.isbn());
        assertThat(book.status()).isEqualTo(BookStatus.BORROWED.getDisplayName());
    }

    @Test
    @DisplayName("최대 대여 권수를 초과하여 도서 대여 실패")
    void failedBorrowExceedMaximumLimit() {
        Page<BookResponse> findBooks = bookUserService.getBooksByAuthor("한강", Pageable.ofSize(20));
        List<BookResponse> books = findBooks.getContent();

        IntStream.range(0, MAXIMUM_BORROWABLE_BOOK_LIMIT)
                .forEach(i -> borrowService.borrow(user1_userNumber, books.get(i).isbn()));

        assertThatThrownBy(() -> borrowService.borrow(user1_userNumber, books.getLast().isbn()))
                .hasMessage(String.format("최대 %d권까지만 대여할 수 있습니다", MAXIMUM_BORROWABLE_BOOK_LIMIT))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("대여 중인 도서 대여 실패")
    void failedBorrowAlreadyBorrowed() {
        Page<BookResponse> findBooks = bookUserService.getBooksByAuthor("한강", Pageable.ofSize(20));
        UUID isbn = findBooks.getContent().getFirst().isbn();

        borrowService.borrow(user1_userNumber, isbn);

        assertThatThrownBy(() -> borrowService.borrow(user2_userNumber, isbn))
                .hasMessage("이미 대여 중인 도서입니다")
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("도서 반납")
    void returnBook() {
        Page<BookResponse> findBooks = bookUserService.getBooksByAuthor("한강", Pageable.ofSize(20));

        IntStream.range(0, MAXIMUM_BORROWABLE_BOOK_LIMIT)
                .forEach(i -> borrowService.borrow(user1_userNumber, findBooks.getContent().get(i).isbn()));

        Page<BorrowHistoryResponse> borrowHistories = borrowService.getAllBorrowHistory(user1_userNumber, Pageable.ofSize(20));

        System.out.println("======= 대여 도서 정보 =======");
        borrowHistories.forEach(System.out::println);

        BorrowHistoryResponse firstBorrowHistory = borrowHistories.getContent().getFirst();
        ReturnBookResponse returnBookResponse = borrowService.returnBook(firstBorrowHistory.borrowId());

        System.out.println("======= 반납 정보 =======");
        System.out.println(returnBookResponse);

        assertThat(returnBookResponse).isNotNull();
        assertThat(firstBorrowHistory.isbn()).isEqualTo(returnBookResponse.isbn());
        assertThat(returnBookResponse.isOverdue()).isFalse();
    }

}