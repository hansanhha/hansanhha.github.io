package hansanhha.querydsl.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hansanhha.querydsl.book.entity.QBook;
import hansanhha.querydsl.loan.entity.QWaitList;
import hansanhha.querydsl.user.entity.QUser;
import hansanhha.querydsl.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    @Override
    public Optional<User> fetchByUserNumber(UUID userNumber) {

        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .leftJoin(user.currentBorrowBooks).fetchJoin()
                        .leftJoin(user.currentWaitList).fetchJoin()
                        .where(user.userNumber.eq(userNumber))
                        .fetchFirst());
    }

    @Override
    public Optional<User> fetchBorrowBooksByUserNumber(UUID userNumber) {
        User found = queryFactory.selectFrom(user)
                .leftJoin(user.currentBorrowBooks).fetchJoin()
                .where(user.userNumber.eq(userNumber))
                .fetchOne();

        return Optional.ofNullable(found);
    }
}
