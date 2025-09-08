package hansanhha.querydsl.user.dto;

import hansanhha.querydsl.book.dto.BookResponse;
import hansanhha.querydsl.loan.dto.WaitListResponse;
import hansanhha.querydsl.user.entity.User;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record UserResponse(
        UUID userNumber,
        String name,
        int totalBorrowCount,
        int totalOverdueCount,
        List<BookResponse> borrowBooks,
        List<WaitListResponse> waitList) {


    public static UserResponse from(User user) {
        return new UserResponse(user.getUserNumber(),
                user.getName(),
                user.getTotalBorrowCount(),
                user.getTotalOverdueCount(),
                user.getCurrentBorrowBooks() == null ? null : BookResponse.from(user.getCurrentBorrowBooks()),
                user.getCurrentWaitList() == null ? null : WaitListResponse.from(user.getCurrentWaitList()));
    }
}
