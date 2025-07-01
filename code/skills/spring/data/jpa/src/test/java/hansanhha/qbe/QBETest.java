package hansanhha.qbe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class QBETest {

    @Autowired
    private hansanhha.qbe.QBEUserRepository QBEUserRepository;

    @BeforeEach
    void init() {
        QBEUserRepository.save(new QBEUser("the", "weeknd"));
    }

    @Test
    void qbe() {
        // given
        QBEUser QBEUserProbe = new QBEUser();
        QBEUserProbe.setFirstname("the");
        QBEUserProbe.setLastname("weeknd");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("first_name", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("last_name", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<QBEUser> example = Example.of(QBEUserProbe, matcher);

        // when
        List<QBEUser> QBEUsers = QBEUserRepository.findAll(example);

        // then
        assertThat(QBEUsers.size()).isEqualTo(1);
        assertThat(QBEUsers.getFirst().getFirstname()).isEqualTo(QBEUserProbe.getFirstname());
        assertThat(QBEUsers.getFirst().getLastname()).isEqualTo(QBEUserProbe.getLastname());
    }
}
