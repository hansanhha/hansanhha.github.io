package hansanhha.querydsl.user;

import hansanhha.querydsl.user.dto.UserResponse;
import hansanhha.querydsl.user.entity.User;
import hansanhha.querydsl.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getOne(UUID userNumber) {
        return userRepository.fetchByUserNumber(userNumber)
                .map(UserResponse::from)
                .orElseThrow(EntityNotFoundException::new);
    }

    public UUID join(String name) {
        User user = User.create(name);
        userRepository.save(user);

        return user.getUserNumber();
    }

    public void delete(UUID userNumber) {
        userRepository.fetchByUserNumber(userNumber)
                .ifPresentOrElse(userRepository::delete, () -> {
                            throw new EntityNotFoundException();}
                );
    }
}
