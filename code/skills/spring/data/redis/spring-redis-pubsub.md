---
layout: default
title:
---

[설정](#설정)

[메시지 발행](#메시지-발행)

[메시지 수신](#메시지-수신)

## 설정

### 의존성 설정

```kotlin
implementation("org.springframework.boot:spring-boot-starter-data-redis")
```

### 스프링 부트 자동 구성
- LettuceConnectionFactory
- RedisTemplate
- [필요시 커스텀 빈 또는 커스터마이저 등록](./spring%20data%20redis.md#레디스를-사용하기-위한-필수커스텀-설정)

### RedisMessageListenerContainer 빈 등록 (필수 설정)

```java
@Configuration
public class RedisMessageConfig {
    
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       MessageListenerAdapter messageListener) {
        RedisMessageListenerContainer messageListenerContainer = new RedisMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(connectionFactory);
        return messageListenerContainer;
    }
    
}
```

## 메시지 발행



## 메시지 수신

```java
@Component
public class RedisMessageSubscriber implements MessageListener {
    
    
} 
```