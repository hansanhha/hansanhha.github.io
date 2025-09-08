package hansanhha.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JpaTransactionTest {

    @Autowired
    private JpaTransaction jpaTransaction;

    @Test
    void 트랜잭션_커밋() {
        SimpleEntity simpleEntity = new SimpleEntity(1L, "simpleEntity");
        jpaTransaction.commit(simpleEntity);
    }

    @Test
    void 엔티티_조회() {
        SimpleEntity simpleEntity = new SimpleEntity(1L, "simpleEntity");
        jpaTransaction.commit(simpleEntity);
        SimpleEntity findEntity = jpaTransaction.findByJPQL("SELECT s FROM SimpleEntity s WHERE s.id = :id", 1L);
    }
}