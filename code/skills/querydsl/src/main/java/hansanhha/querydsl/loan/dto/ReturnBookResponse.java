package hansanhha.querydsl.loan.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

public record ReturnBookResponse(
        UUID userNumber,
        UUID isbn,
        LocalDate returnDate,
        boolean isOverdue,
        Period overduePeriod) {
}
