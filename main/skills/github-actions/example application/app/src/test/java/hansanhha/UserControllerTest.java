package hansanhha;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void saveShouldReturnCreatedStatus() {
        assertThat(mvc.post().uri("/api/users")
                .content("{\"name\": \"hansanhha\"}"))
                .hasStatus(HttpStatus.CREATED.value());
    }
}