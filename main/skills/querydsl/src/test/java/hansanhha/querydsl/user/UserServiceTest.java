package hansanhha.querydsl.user;

import hansanhha.querydsl.user.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    UserService userService;

    private UUID userNumber;

    @Test
    @Order(Integer.MIN_VALUE)
    @Commit
    @DisplayName("회원가입")
    void join() {
        String username = "test user";
        userNumber = userService.join(username);

        assertThat(userNumber.getClass()).isEqualTo(UUID.class);
    }

    @Test
    @Order(Integer.MAX_VALUE)
    @DisplayName("유저 정보 조회")
    void getUserInfo() {
        UserResponse user = userService.getOne(userNumber);

        assertThat(user.userNumber()).isEqualTo(userNumber);

        System.out.println(user);
    }

}