package hansanhha.token.jwt.repository;

import hansanhha.token.Token;
import hansanhha.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JwtRefreshTokenRepository extends JpaRepository<Token, Long> {

    List<Token> findAllByUser(User user);

    Optional<Token> findByTokenValue(String tokenValue);

    Optional<Token> findByAccessId(String accessId);
}
