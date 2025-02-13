package hansanhha.specification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpecUserServiceTest {

    @Autowired
    private SpecUserService specUserService;

    @Test
    void specificationTest() {
        // given
        specUserService.create("han", "sanhha", 10);
        specUserService.create("han", "sanhha", 20);
        specUserService.create("han", "sanhha", 30);
        specUserService.create("han", "sanhha", 40);
        specUserService.create("han", "sanhha", 50);
        specUserService.create("han", "sanhha", 60);

        // when
        List<SpecUser> found = specUserService.findUsers("sanhha", 30);

        // then
        assertThat(found.size()).isEqualTo(3);
    }

    @Test
    void specificationTest2() {
        // given
        specUserService.create("han", "sanhha", 10);
        specUserService.create("han", "sanhha", 20);
        specUserService.create("han", "sanhha", 30);
        specUserService.create("han", "sanhha", 40);
        specUserService.create("han", "sanhha", 50);
        specUserService.create("han", "sanhha", 60);

        // when
        List<SpecUser> found = specUserService.findUsers("", 30);

        // then
        assertThat(found.size()).isEqualTo(0);
    }
}