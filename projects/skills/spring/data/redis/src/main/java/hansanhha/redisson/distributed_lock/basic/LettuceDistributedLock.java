package hansanhha.redisson.distributed_lock.basic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static hansanhha.redisson.distributed_lock.basic.DistributedLockProperties.RETRY_DELAY;
import static hansanhha.redisson.distributed_lock.basic.DistributedLockProperties.TIMEOUT;

@Component
@RequiredArgsConstructor
public class LettuceDistributedLock {

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    public boolean acquireLock(String lockKey, String lockValue) {
        long start = System.currentTimeMillis();

        System.out.println("[락 획득 시도] 키: " + lockKey + " 값: " + lockValue);

        // 락 획득 시도 유효 시간 내에 락을 획득할 때 까지 반복적으로 레디스에게 락 획득을 시도한다 (스핀 락)
        while (System.currentTimeMillis() - start < TIMEOUT) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue);

            System.out.println(Thread.currentThread().getName() + ": [락 획득 " + (Boolean.TRUE.equals(success) ? "성공]" : " 실패]"));

            if (Boolean.TRUE.equals(success)) {
                return true;
            }

            // 일정 시간 대기
            try {
                Thread.sleep(RETRY_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException();
            }
        }

        return false;
    }

    public void releaseLock(String lockKey, String lockValue) {
        System.out.println(Thread.currentThread().getName() + ": [락 해제] 키:" + lockKey + " 값: " + lockValue );

        String luaScript = """
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                else
                    return 0
                end
                """;

        DefaultRedisScript<Integer> script = new DefaultRedisScript<>(luaScript, Integer.class);
        redisTemplate.execute(script, Collections.singletonList(lockKey), lockValue);
    }
}
