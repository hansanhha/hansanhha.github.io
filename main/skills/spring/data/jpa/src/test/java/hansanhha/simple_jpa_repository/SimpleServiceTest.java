package hansanhha.simple_jpa_repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SimpleServiceTest {

    @Autowired
    private SimpleUserService simpleUserService;

    @BeforeEach
    void init() {
        simpleUserService.create("the", "weenkd", "010-1234-5678");
    }

    @Test
    void findUserTest() {
        // given
        long findId = 1L;

        // when
        var simpleUser = simpleUserService.find(findId);

        // then
        assertThat(simpleUser.getId()).isEqualTo(findId);
    }

    @Test
    void createTest() {
        // given
        var firstName = "taylor";
        var lastName = "swift";
        var phoneNumber = "010-1234-1234";

        // when
        var created = simpleUserService.create(firstName, lastName, phoneNumber);

        // then
        assertThat(created.getFirstName()).isEqualTo(firstName);
        assertThat(created.getLastName()).isEqualTo(lastName);
        assertThat(created.getPhoneNumber()).isEqualTo(phoneNumber);
    }
}