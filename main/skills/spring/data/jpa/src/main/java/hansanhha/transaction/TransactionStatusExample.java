package hansanhha.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

@Service
@RequiredArgsConstructor
public class TransactionStatusExample {

    private final PlatformTransactionManager transactionManager;
    private final ExampleEntityRepository exampleEntityRepository;

    public ExampleEntity createExampleEntity(int count) {
        ExampleEntity exampleEntity = new ExampleEntity(0L, count);
        return exampleEntityRepository.save(exampleEntity);
    }

    public int increaseCountSetRollbackOnly(ExampleEntity exampleEntity) {
        TransactionDefinition defaultTransactionDefinition = TransactionDefinition.withDefaults();
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);

        try {
            exampleEntity.increaseCount();

            // TransactionStatus를 통해 롤백 전용 설정
            // exampleEntity.increaseCount() 호출로 변경된 exampleEntity의 상태가 데이터베이스에 반영되지 않음
            status.setRollbackOnly();

            exampleEntityRepository.save(exampleEntity);

            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }

        return exampleEntityRepository.findById(exampleEntity.getId()).get().getCount();
    }

    public int increaseCount(ExampleEntity exampleEntity) {
        TransactionDefinition defaultTransactionDefinition = TransactionDefinition.withDefaults();
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);

        // TransactionStatus를 통해 롤백 전용 설정 X
        try {
            // exampleEntity.increaseCount() 호출로 변경된 exampleEntity의 상태가 데이터베이스에 반영됨
            exampleEntity.increaseCount();

            exampleEntityRepository.save(exampleEntity);

            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }

        return exampleEntityRepository.findById(exampleEntity.getId()).get().getCount();
    }

}
