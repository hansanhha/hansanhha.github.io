package hansanhha;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisAutoConfigurationTest {

    @Autowired
    private BeanFactory beanFactory;

    @Test
    @DisplayName("스프링 부트 자동 구성으로 인해 LettuceConnectionFactory, RedisTemplate, RedisCacheManager 빈이 자동으로 등록된다")
    void redisAutoConfigWithRedisCache() {
        LettuceConnectionFactory lettuceConnectionFactory = null;
        RedisTemplate<Object, Object> redisTemplate = null;
        RedisCacheManager redisCacheManager = null;

        try {
            lettuceConnectionFactory = beanFactory.getBean("redisConnectionFactory", LettuceConnectionFactory.class);
            redisTemplate = beanFactory.getBean("redisTemplate", RedisTemplate.class);
            redisCacheManager = beanFactory.getBean("cacheManager", RedisCacheManager.class);
        } catch (BeansException e) {}

        assertThat(lettuceConnectionFactory).isNotNull();
        assertThat(redisTemplate).isNotNull();
        assertThat(redisCacheManager).isNotNull();

    }
}
