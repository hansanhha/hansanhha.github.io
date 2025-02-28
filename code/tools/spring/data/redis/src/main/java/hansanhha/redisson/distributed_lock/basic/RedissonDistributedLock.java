package hansanhha.redisson.distributed_lock.basic;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static hansanhha.redisson.distributed_lock.basic.DistributedLockProperties.*;

@Component
@RequiredArgsConstructor
public class RedissonDistributedLock {

    private final RedissonClient redisClient;

    public boolean lock(String lockKey) {
        RLock lock = redisClient.getLock(lockKey);

        try {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + ": [락 획득 시도] 키: " + lock.getName());

            boolean success = lock.tryLock(TIMEOUT, LOCK_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

            System.out.println(threadName + ": [락 획득 " + (success ? "성공] " : "실패]"));

            if (success) {
                try {
                    // 비즈니스 로직 수행 (가정)
                    Thread.sleep(50);
                } finally {
                    System.out.println(threadName + ": [락 해제]");
                    lock.unlock();
                }
            }

            return success;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
