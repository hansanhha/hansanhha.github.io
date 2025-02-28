package hansanhha.querydsl.loan.dto;

import hansanhha.querydsl.loan.entity.Borrow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

public record BorrowHistoryResponse(
        Long borrowId,
        UUID userNumber,
        UUID isbn,
        String bookTitle,
        String bookAuthor,
        boolean isUseExtensionReturnDate,
        boolean isOverdue,
        LocalDateTime borrowDate,
        LocalDate returnDateDeadline,
        LocalDate returnDate,
        Period overduePeriod) {

    public static BorrowHistoryResponse from(Borrow borrow) {
        return new BorrowHistoryResponse(
                borrow.getId(),
                borrow.getBorrower().getUserNumber(),
                borrow.getBook().getIsbn(),
                borrow.getBook().getTitle(),
                borrow.getBook().getAuthor(),
                borrow.getExtensionBookReturnCount() != null && borrow.getExtensionBookReturnCount()> 0,
                borrow.getOverdue() != null ? borrow.getOverdue() : false,
                borrow.getBookBorrowDate(),
                borrow.getBookReturnDeadlineDate(),
                borrow.getBookReturnDate() != null ? borrow.getBookReturnDate() : null,
                borrow.getOverduePeriod() != null ? Period.ofDays(borrow.getOverduePeriod().intValue()) : null);
    }
}
