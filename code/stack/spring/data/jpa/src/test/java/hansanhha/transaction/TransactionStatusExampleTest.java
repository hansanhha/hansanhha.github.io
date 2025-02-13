package hansanhha.transaction;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TransactionStatusExampleTest {

    @Autowired
    private TransactionStatusExample transactionStatusExample;

    @Test
    void TransactionStatus_RollbackOnly_설정() {
        ExampleEntity exampleEntity = transactionStatusExample.createExampleEntity(5);

        int initCount = exampleEntity.getCount();

        int count = transactionStatusExample.increaseCountSetRollbackOnly(exampleEntity);

        System.out.println(initCount);
        System.out.println(count);
        assertThat(initCount).isEqualTo(count);
    }

    @Test
    void TransactionStatus_RollbackOnly_설정_X() {
        ExampleEntity exampleEntity = transactionStatusExample.createExampleEntity(5);

        int initCount = exampleEntity.getCount();

        int count = transactionStatusExample.increaseCount(exampleEntity);

        System.out.println(initCount);
        System.out.println(count);
        assertThat(initCount).isNotEqualTo(count);

    }
}