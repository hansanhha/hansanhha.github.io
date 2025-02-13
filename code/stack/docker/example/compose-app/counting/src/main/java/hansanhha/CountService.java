package hansanhha;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CountService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_KEY_PREFIX = "counter:";

    public CountService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Long increment(String id) {
        return redisTemplate.opsForValue().increment(REDIS_KEY_PREFIX + id);
    }

    public Long decrement(String id) {
        return redisTemplate.opsForValue().decrement(REDIS_KEY_PREFIX + id);
    }

    public Long getValue(String id) {
        String value = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + id);
        return value != null ? Long.parseLong(value) : 0L;
    }

    public void delete(String id) {
        redisTemplate.delete(REDIS_KEY_PREFIX + id);
    }
}
