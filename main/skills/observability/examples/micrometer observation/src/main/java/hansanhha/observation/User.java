package hansanhha.observation;

import java.time.LocalDateTime;

// 비즈니스 로직에 사용될 엔티티 객체
public record User(
        String userId,
        String role,
        LocalDateTime createdAt) {

}
