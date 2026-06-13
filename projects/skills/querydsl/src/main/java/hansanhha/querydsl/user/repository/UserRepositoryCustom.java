package hansanhha.querydsl.user.repository;

import hansanhha.querydsl.user.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryCustom {
    Optional<User> fetchByUserNumber(UUID userNumber);

    Optional<User> fetchBorrowBooksByUserNumber(UUID userNumber);
}
