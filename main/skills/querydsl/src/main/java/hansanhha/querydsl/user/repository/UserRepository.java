package hansanhha.querydsl.user.repository;

import hansanhha.querydsl.user.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByUserNumber(UUID userNumber);
}
