package hansanhha.querydsl.loan.repository;

import hansanhha.querydsl.loan.entity.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BorrowRepositoryCustom {

    Optional<Borrow> fetchById(Long id);

    Page<Borrow> fetchByBorrowerNumber(UUID userNumber, Pageable pageable);
}
