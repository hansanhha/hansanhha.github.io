package hansanhha.querydsl.loan.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BorrowResponse(
        UUID userNumber,
        UUID isbn,
        LocalDateTime borrowDate,
        LocalDate returnDateDeadline) {
}
