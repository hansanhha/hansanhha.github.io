package hansanhha.distributed_lock;

import hansanhha.redisson.distributed_lock.basic.LettuceDistributedLock;
import hansanhha.redisson.distributed_lock.basic.RedissonDistributedLock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
public class DistributedLockBasicTest {

    @Autowired
    private LettuceDistributedLock lettuceDistributedLock;

    @Autowired
    private RedissonDistributedLock redissonDistributedLock;

    @Test
    @DisplayName("lettuce 클라이언트를 통한 10개 스레드 락 획득 시도")
    void lettuceTryLockWithMultiThread() throws InterruptedException {
        int threadCount = 10;

        String lockKey = "hansanhha";
        String lockValue = "lock";

        Runnable task = () -> {
            boolean acquire = lettuceDistributedLock.acquireLock(lockKey, lockValue);

            if (acquire) {
                try {
                    Thread.sleep(50);
                } catch (
                        InterruptedException ignored) {
                } finally {
                    lettuceDistributedLock.releaseLock(lockKey, lockValue);
                }
            }
        };

        List<Thread> threads = new LinkedList<>();

        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(task));
        }

        threads.forEach(Thread::start);

        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    @DisplayName("redisson 클라이언트를 통한 10개 스레드 락 획득 시도")
    void redissonTryLockWithMultiThread() throws InterruptedException {
        int threadCount = 10;

        String lockKey = "hansanhha";

        Runnable task = () -> redissonDistributedLock.lock(lockKey);

        List<Thread> threads = new LinkedList<>();

        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(task));
        }

        threads.forEach(Thread::start);

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
