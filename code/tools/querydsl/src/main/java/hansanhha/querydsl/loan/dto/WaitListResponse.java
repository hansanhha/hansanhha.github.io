package hansanhha.querydsl.loan.dto;

import hansanhha.querydsl.loan.entity.WaitList;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record WaitListResponse(
        UUID userNumber,
        UUID isbn,
        int waitOrder,
        LocalDateTime appliedAt) {

    public static WaitListResponse from(WaitList waitList) {
        return new WaitListResponse(
                waitList.getBook().getIsbn(),
                waitList.getUser().getUserNumber(),
                waitList.getWaitOrder(),
                waitList.getCreateAt());
    }

    public static List<WaitListResponse> from(Collection<WaitList> waitList) {
        return waitList.stream().map(WaitListResponse::from).toList();
    }
}
