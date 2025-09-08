---
layout: default
title:
---

[spring data redis](#spring-data-redis)

[구성 요소](#구성-요소)

[스프링 설정과 스프링 부트 자동 구성](#스프링-설정과-스프링-부트-자동-구성)

[레디스를 사용하기 위한 필수/커스텀 설정](#레디스를-사용하기-위한-필수커스텀-설정)

[RedisTemplate](#redistemplate-1)

[redis cache store](#redis-cache-store)

[pub/sub](#pubsub)

## spring data redis

redis와 spring 애플리케이션 간의 통합을 쉽게 해주는 스프링 데이터 프로젝트

redis 저장소 상호작용

reids 데이터 타입 지원

redis 캐싱 기능

redis pub/sub 메시징 기능 등


## 구성 요소

#### RedisTemplate

레디스와 상호작용하기 위한 스프링에서 제공하는 템플릿 클래스

JdbcTemplate처럼 레디스와 관련해서 공통적으로 수행해야 할 로직을 구현하고 동적으로 변경될 부분들을 템플릿 메서드로 처리한다

레디스의 모든 데이터 타입을 지원한다

레디스 명령을 코드에서 수행할 수 있다

#### ReactiveRedisTemplate

비동기 방식으로 레디스와 통합하기 위한 템플릿

WebFlux와 같은 리액티브 프로그래밍 환경에서 사용한다

#### CacheManager

Spring Cache를 추상화하여 레디스를 캐시 스토어로 활용할 수 있게 해주는 인터페이스

레디스 기반의 캐싱 구현을 단순화한다

#### RedisConnectionFactory

#### RedisMessageListenerContainer

레디스 pub/sub 메시지를 수신하기 위한 구성 요소

메시징 애플리케이션 구현 시 사용한다


## 스프링 설정과 스프링 부트 자동 구성

스프링 설정
- @EnableCaching (스프링 캐시 사용 시)

스프링 부트 자동 구성
- RedisAutoConfiguration: RedisTemplate, LettuceConnectionConfiguration
- RedisCacheConfiguration (스프링 캐시 사용 시): RedisCacheManager


## 레디스를 사용하기 위한 필수/커스텀 설정

### 의존성 설정

스프링 부트 스타터를 사용하면 스프링 데이터와 레디스 클라이언트 의존성을 간단하게 명시할 수 있다

스타터는 레디스의 jedis와 lettuce 클라이언트를 가져온다

```kotlin
implementation("org.springframework.boot:spring-boot-starter-data-redis")
```

### 레디스 서버 설정

#### 레디스 서버 프로퍼티 설정

레디스와 연결 및 기본 동작 설정을 담당한다

RedisTemplate, RedisConnectionFactory 등 전반적인 레디스 관련 작업에서 사용한다

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      username: test
      password: test
      connect-timeout: 2000
```

#### 자바 설정

```java
@Configuration
public class RedisConfig {

    // 스프링 부트가 LettuceConnectionFactory 타입으로 자동 구성한다
    // 별도의 설정이 필요한 경우 RedisConnectionFactory 빈을 등록하거나
    // LettuceClientConfigurationBuilderCustomizer, LettuceClientOptionsBuilderCustomizer 빈 등록
    @Bean
    public RedisConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    // 스프링 부트가 RedisTemplate<Object, Object>와 StringRedisTemplate을 자동 구성한다
    // 다만 Jackson2JsonRedisSerializer를 사용하려면 스프링 빈으로 등록해야 된다
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 레디스 클라이언트 설정
        template.setConnectionFactory(lettuceConnectionFactory());

        // 키, 값 직렬화 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

}
```

### 레디스 캐시 설정

#### 레디스 캐시 프로퍼티 설정

스프링 캐시를 추상화하여 레디스를 캐시 저장소로 사용할 때 필요한 설정을 담당한다

RedisCacheManager 등에서 캐시 저장소로 레디스를 사용할 때 추가적인 설정을 할 때 사용한다

```yaml
spring:
  cache:
    type: redis # 캐시 제공자 지정
    redis: 
      time-to-live: 60000      # 캐시 데이터의 ttl (밀리초)
      cache-null-values: false # 캐시 값 null 허용 여부
      prefix: myApp            # 캐시 키 prefix
```

#### 레디스 캐시 자바 설정

```java
@Configuration
// 스프링 캐시 사용 선언
@EnableCaching
public RedisCacheConfig {

    // 스프링 부트가 RedisCacheManager 타입으로 자동 구성한다
    // 별도의 설정이 필요한 경우 빈으로 등록하거나 레디스 캐시 프로퍼티 파일 설정과 RedisCacheConfiguration을 빈으로 등록한다
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // 캐시 설정
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // 키, 값 직렬화 설정
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues() // null 값 캐시 방지
                .entryTtl(Duration.ofMinutes(10)); // 캐시 만료 시간 설정

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory) // 클라이언트 설정
                .cacheDefaults(redisCacheConfiguration) // 캐시 설정
                .build();
    }

    // RedisCacheConfiguration을 빈으로 등록하면 스프링 부트가 자동 구성하는 RedisCacheManager에 설정을 반영한다
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)) // 캐시 만료 시간 5분
                 // 키 직렬화 설정
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                 // 값 직렬화 설정
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues() // Null 값 캐싱 비활성화
                .prefixCacheNameWith("myApp:"); // 프리픽스 설정
    }
}
```


## RedisTemplate

[RedisTemplate](./spring RedisTemplate)


## redis cache store

[spring redis cache](./spring%20redis%20cache.md)


## pub/sub