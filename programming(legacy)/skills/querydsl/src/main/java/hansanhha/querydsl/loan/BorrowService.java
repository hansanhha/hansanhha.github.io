package hansanhha.querydsl.loan;

import hansanhha.querydsl.book.entity.Book;
import hansanhha.querydsl.book.repository.BookRepository;
import hansanhha.querydsl.loan.dto.BorrowHistoryResponse;
import hansanhha.querydsl.loan.dto.BorrowResponse;
import hansanhha.querydsl.loan.dto.ReturnBookResponse;
import hansanhha.querydsl.loan.entity.Borrow;
import hansanhha.querydsl.loan.repository.BorrowRepository;
import hansanhha.querydsl.user.entity.User;
import hansanhha.querydsl.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BorrowService {

    public static final int MAXIMUM_BORROWABLE_BOOK_LIMIT = 10;
    public static final int DEFAULT_BORROW_PERIOD = 2;

    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;

    public Page<BorrowHistoryResponse> getAllBorrowHistory(UUID userNumber, Pageable pageable) {
        Page<Borrow> borrows = borrowRepository.fetchByBorrowerNumber(userNumber, pageable);
        return borrows.map(BorrowHistoryResponse::from);
    }

    public BorrowResponse borrow(UUID userNumber, UUID isbn) {
        Book book = bookRepository.fetchBorrowerByIsbn(isbn).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.fetchBorrowBooksByUserNumber(userNumber).orElseThrow(EntityNotFoundException::new);

        book.validateStatus();

        if (user.getCurrentBorrowBooks().size() >= MAXIMUM_BORROWABLE_BOOK_LIMIT) {
            throw new IllegalStateException(String.format("최대 %d권까지만 대여할 수 있습니다", MAXIMUM_BORROWABLE_BOOK_LIMIT));
        }

        user.borrowBook(book);

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDate returnDateDeadlineDate = borrowDate.plusWeeks(DEFAULT_BORROW_PERIOD).toLocalDate();

        Borrow borrow = Borrow.builder()
                .book(book)
                .borrower(user)
                .status(BorrowStatus.BORROWED)
                .bookBorrowDate(borrowDate)
                .bookReturnDeadlineDate(returnDateDeadlineDate)
                .build();

        borrowRepository.save(borrow);

        return new BorrowResponse(userNumber, isbn, borrowDate, returnDateDeadlineDate);
    }

    public ReturnBookResponse returnBook(Long loanId) {
        Borrow borrow = borrowRepository.fetchById(loanId).orElseThrow(EntityNotFoundException::new);
        Book book = borrow.getBook();
        User borrower = borrow.getBorrower();

        borrow.validateStatusBeforeReturn();

        LocalDate bookReturnDeadlineDate = borrow.getBookReturnDeadlineDate();
        LocalDate now = LocalDate.now();

        if (now.isAfter(bookReturnDeadlineDate)) {
            Period overduePeriod = borrow.overdueReturn(now);
            borrower.overdueReturnBook(book);
            return new ReturnBookResponse(borrower.getUserNumber(), book.getIsbn(), now, true, overduePeriod);
        }

        borrow.normalReturn(now);
        borrower.normalReturnBook(book);
        return new ReturnBookResponse(borrower.getUserNumber(), book.getIsbn(), now, false, null);
    }

}
