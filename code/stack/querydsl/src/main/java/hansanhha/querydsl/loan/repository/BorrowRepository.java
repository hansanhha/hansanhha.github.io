package hansanhha.querydsl.loan.repository;

import hansanhha.querydsl.loan.entity.Borrow;
import org.springframework.data.repository.CrudRepository;

public interface BorrowRepository extends CrudRepository<Borrow, Long>, BorrowRepositoryCustom {
}
