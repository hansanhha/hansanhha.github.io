package hansanhha.querydsl.loan.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hansanhha.querydsl.loan.entity.Borrow;
import hansanhha.querydsl.loan.entity.QBorrow;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BorrowRepositoryCustomImpl implements BorrowRepositoryCustom {

    private final JPAQueryFactory query;
    private final QBorrow borrow = QBorrow.borrow;

    @Override
    public Optional<Borrow> fetchById(Long id) {
        Borrow found = query
                .selectFrom(borrow)
                .leftJoin(borrow.book).fetchJoin()
                .leftJoin(borrow.borrower).fetchJoin()
                .where(borrow.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(found);
    }

    @Override
    public Page<Borrow> fetchByBorrowerNumber(UUID userNumber, Pageable pageable) {
        List<Borrow> borrows = query
                .selectFrom(borrow)
                .leftJoin(borrow.book).fetchJoin()
                .leftJoin(borrow.borrower).fetchJoin()
                .where(borrow.borrower.userNumber.eq(userNumber))
                .orderBy(borrow.bookBorrowDate.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        return new PageImpl<>(borrows, pageable, borrows.size());
    }
}
