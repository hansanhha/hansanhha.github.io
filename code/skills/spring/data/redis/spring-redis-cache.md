---
layout: default
title:
---

[spring caching 추상화](#spring-caching-추상화)

[스프링 (부트) 설정과 RedisCacheManager 자동 구성](#스프링-부트-설정과-rediscachemanager-자동-구성)

[RedisCacheConfiguration (spring)](#rediscacheconfiguration-spring)

[스프링 부트에서 레디스 캐시 설정하기](#스프링-부트에서-레디스-캐시-설정하기)

[어노테이션 기반 레디스 캐시 사용](#어노테이션-기반-레디스-캐시-사용)

## spring caching 추상화

스프링은 core 기능 중 @Cacheable, @CachePut, @CacheEvict 같은 어노테이션을 통해 캐시를 관리할 수 있는 추상화를 제공한다

이 추상화는 특정 캐시 제공자(redis, caffeine, hazelcast) 등에 종속되지 않는다

### 캐시 어노테이션 동작

@Cacheable: 메서드 결과를 캐시 저장소에 저장하거나, 실제 메서드를 호출하기 전 캐시에서 데이터를 조회한다

@CachePut: 메서드 실행 후 반환값을 캐시에 저장한다

@CacheEvict: 특정 캐시 데이터를 제거한다

@Caching: 여러 캐시 작업을 조합한다

### 주요 컴포넌트

#### Cache

공통 캐시 작업을 정의한 인터페이스

캐시 어노테이션 기반 동작에 대한 SPI이자 애플리케이션에서 직접적으로 사용할 수 있는 API이다

각 캐시 제공자는 캐시 작업을 수행하는 구현체가 존재한다 (RedisCache 등)

#### CacheManager

스프링에서 캐시를 관리하는 SPI 인터페이스

각 캐시 제공자는 캐시 관리 기능을 담당하는 구현체를 제공한다 (RedisCacheManager 등)

#### CacheResolver

메서드 호출을 인터셉트하여 어떤 캐시 인스턴스를 사용할지 결정하는 인터페이스

스프링 캐시는 어노테이션 기반으로 동작하기 때문에 호출되는 메서드를 인터셉트하고 캐시 기능을 수행할 특정 캐시 제공자의 Cache(RedisCache 등) 인스턴스를 결정한다


## 스프링 (부트) 설정과 RedisCacheManager 자동 구성

레디스 캐시 매니저 자동 구성 흐름: 스프링 캐시 설정 -> 스프링 부트 캐시 자동 구성 -> 레디스 캐시 매니저 빈 등록

### 스프링의 @EnableCaching

스프링의 캐싱 추상화 기능을 사용하려면 @EnableCaching 어노테이션을 통해 캐싱 기능을 명시적으로 활성화해야 한다

스프링은 프록시 기반 어노테이션을 통해 캐시 관리를 수행할 수 있도록 스프링의 infrastructure bean을 등록하는 ProxyCachingConfiguration 빈을 활성화한다

ProxyCachingConfiguration 구성 클래스는 다음의 빈들을 등록한다

#### BeanFactoryCacheOperationSourceAdvisor

역할: AOP advisor 등록
- 캐싱 관련 스프링 aop의 advisor 역할을 수행하는 클래스로 캐싱 관련 메서드 호출을 가로챌 시점을 정의한다
- @Cacheable, @CacheEvict, @CachePut, @Caching 어노테이션이 붙은 메서드를 감지하여 메서드 호출 시점에 가로채서 캐싱 관련 advice를 적용한다

주요 기능: advice와 pointcut 등록 및 프록시 생성
- CacheInterceptor를 advice로 사용하고 pointcut은 CacheOperationSource를 기반으로 동작하는 메서드로 지정한다
- 캐싱 대상 메서드를 동적으로 감싸는 프록시를 생성한다

#### CacheOperationSource

역할: 캐싱 메타데이터 제공
- 캐싱 동작을 정의하는 메타데이터를 추출한다
- 메서드 또는 타입 수준에서 적용된 캐싱 관련 어노테이션을 분석하고 이를 기반으로 CacheOperation을 생성한다

주요 기능: 캐싱 로직 정의
- 캐시 동작(cacheNames, cacheManager, key, condition 등)을 포함한 캐시 설정(CacheOperation)을 반환한다

기본 구현체: AnnotationCacheOperationSource

#### CacheInterceptor

역할: 캐시 동작 수행
- aop 기반으로 캐싱 관련 메서드를 가로채고 캐싱 로직을 실행한다
- CacheOperationSource를 사용하여 메서드 실행 시 필요한 캐싱 설정을 가져온다
- CacheManager를 사용하여 캐시 어노테이션(@Cacheable, @CachePut 등) 별 작업을 수행한다

주요 기능
- [캐시 어노테이션 동작](#캐시-어노테이션-동작)
- 어노테이션에서 지정된 condition, unless 등의 조건을 평가하여 캐싱 여부를 결정한다

#### 스프링 캐싱 동작 흐름

BeanFactoryCacheOperationSourceAdvisor가 AOP를 통해 캐싱 대상 메서드를 감지한다
- pointcut: CacheOperationSource에서 제공하는 메타데이터
- advice: CacheInterceptor

CacheOperationSource는 메서드에 설정된 캐싱 메타데이터를 기반으로 캐싱 동작을 정의한다

CacheInterceptor는 메서드 호출을 가로채서 CacheManager를 통해 캐싱 관련 작업을 수행한다 (캐시 저장 조건 평가도 실시한다)

### 스프링 부트 autoconfiguration

스프링 부트는 스프링 캐싱 기능을 사용할 경우 캐시 제공자에 대한 자동 구성을 지원해주는 CacheAutoConfiguration을 제공한다

#### CacheAutoConfiguration 활성화 조건
- 스프링의 @EnableCaching 선언 (정확히는 CacheAspectSupport(CacheInterceptor) 빈 등록 여부)
- cacheResolver 이름으로 CacheManager 빈이 등록되지 않은 경우

#### CacheAutoConfiguration 동작

활성화된 CacheAutoConfiguration은 CacheConfigurationImportSelector 클래스를 import한다

또한 CacheManagerCustomizers 타입의 빈이 없는 경우 CacheManagerCustomizers 빈을 등록한다

CacheConfigurationImportSelector 클래스는 스프링에서 제공하는 각 캐싱 제공자에 대해 스프링 부트 차원에서 제공하는 자동 구성 클래스들을 import한다

RedisCacheConfiguration, CaffeineCacheConfiguration, HazelcastCacheConfiguration 등

import된 각 캐싱 제공자에 대한 구성 클래스마다 조건을 평가하여 캐시 매니저 구현체(RedisCacheManager 등)를 스프링 빈으로 등록하는데 이 때 빈의 이름을 **"cacheManager"**로 지정한다

간단 요약
- 스프링 @EnableCaching 활성화
- 스프링 부트 캐시 자동 구성 활성화 
- 캐싱 제공자의 캐시 매니저 구현체 스프링 빈 등록 진행 (@Conditional 평가 충족 시)

### RedisCacheConfiguration

RedisCacheConfiguration은 스프링 부트의 캐시 자동 구성에 의해 import되며 조건에 충족하는 경우 활성화된다

#### RedisCacheConfiguration 활성화 조건
- redis 의존성이 있는 경우
- RedisConnectionFactory 빈이 등록된 경우(레디스 자동 구성 또는 사용자 직접 등록)
- CacheManager 빈이 등록되지 않은 경우

#### RedisCacheManager 빈 등록

사용 정보 
- CacheProperties: Cache 프로퍼티 설정
- RedisCacheConfiguration: 스프링에서 제공하는 RedisCache 커스텀 클래스
- RedisConnectionFactory
- CacheManagerCustomizer
- RedisCacheManagerBuilderCustomizer

RedisCacheManager 빌드
- CacheProperties와 RedisCacheConfiguration를 기반으로 RedisCacheManager의 기본 캐시 동작 및 기타 정보(직렬화, ttl, 네임스페이스 등)을 설정한다
- CacheProperties에 설정된 프로퍼티 값 설정
- Customizer 적용
- **"cacheManager"** 이름으로 RedisCacheManager 타입 스프링 빈 등록


## RedisCacheConfiguration (spring)

레디스 캐시의 기본 동작 및 세부 설정을 지원하는 불변 클래스 (스프링 제공)
- 레디스 캐시 기본 동작 정의
- 키/값의 직렬화
- ttl
- prefix 등

스프링 부트에서 제공하는 레디스 캐시 매니저 구성 클래스의 이름과 동일하다

### 레디스 캐시 동작 설정

레디스에 데이터를 어떻게 읽고 저장할지 정의

```java
// 캐시에 null 값을 허용하지 않는다 (기본 설정은 null 값을 허용한다)
RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues();
```

### 직렬화 설정

캐시 키와 값에 대한 직렬화 방식 설정

캐시 키 직렬화 기본값: StringRedisSerializer (직렬화된 문자열 형태)

```java
RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
```

캐시 값 직렬화 기본값: JdkSerializationRedisSerializer (자바 직렬화 사용)

```java
// GenericJackson2JsonRedisSerializer 지정
RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
```

### ttl 설정

캐시에 저장된 데이터 만료 시간 설정 

```java
// 캐시 데이터 10분 유지
RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10)); 
```

### 네임스페이스 설정

캐시 키 충돌 방지를 위한 네임스페이스 설정

```java
// 캐시 키에 prefix 설정
RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("myApp:");
```

### 기본 설정

기본 설정을 가진 RedisCacheConfiguration을 생성한다
- ttl 만료 없음
- null 값 허용
- 캐시 작업 만료 시간 활성화 X
- 기본 prefix 설정 (어노테이션에 설정한 cacheNames + ":"로 설정한다)
- 키 직렬화: StringRedisSerializer.UTF_8
- 값 직렬화: JdkSerializationRedisSerializer
- 컨버팅: DefaultFormattingConversionService

```java
RedisCacheConfiguration.defaultCacheConfig()
```


## 스프링 부트에서 레디스 캐시 설정하기

스프링 부트는 레디스 클라이언트(LettuceConnectionFactory), RedisTemplate, RedisCacheManager를 자동 구성한다

따라서 스프링 부트에서 제공하는 기본 동작으로만 레디스 캐시를 사용할 것이라면 사용자는 @EnableCaching만 선언하면 된다

또는 두 가지 요소를 이용하여 레디스 캐시의 기본 동작이나 설정 값을 바꿀 수 있다
- 레디스 캐시 관련 프로퍼티 (spring.cache.redis)
- RedisCacheConfiguration 빈 설정

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 60000
      cache-null-values: false
      prefix: myApp
```

```java
@Configuration
@EnableCaching
public class RedisCacheConfig {

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















## 어노테이션 기반 레디스 캐시 사용

### @Cacheable

#### 동작 방식

캐시를 먼저 조회하고 데이터가 있으면 메서드를 실행하지 않고 저장된 데이터를 반환한다

캐이세 데이터가 없으면 메서드를 실행하고 반환값을 캐시에 저장한다

#### 속성

cacheNames(value)
- 메서드 호출의 결과 값이 저장되는 캐시 이름 (여러 캐시 이름을 지정할 수 있다)
- 스프링 캐시 추상화에서 캐시를 논리적으로 구분하기 위한 네임스페이스/접두사처럼 동작한다
- 레디스에 저장될 키는 cacheNames와 사용자가 정의한 키가 합쳐져서 생성된 완전한 키로 저장된다
- 명시하지 않으면 기본적으로 클래스와 메서드 이름을 기반으로 캐시 이름이 생성된다

key
- 캐시 항목의 키 설정
- 기본 값: #root.methodName + #root.args로 생성된 키
- spel을 사용하여 커스텀 키를 정의할 수 있다

keyGenerator
- 커스텀 키 생성기 지정
- key 속성과 배타적이다

cacheManager
- 애플리케이션에서 여러 캐시 매니저를 등록한 경우 특정 캐시를 선택하기 위해 사용하는 속성
- 명시하지 않으면 스프링 컨텍스트에 등록된 cacheManager 빈을 사용한다
- cacheResolver 속성과 배타적이다

cacheResolver
- 커스텀 cacheResolver 빈을 지정하기 위한 속성
- 명시하지 않으면 스프링이 제공하는 기본 CacheResolver를 사용한다

condition
- 캐싱 여부를 결정하는 조건식
- spel을 사용하며 true일 때만 캐시가 적용된다
- 메서드 실행 전 평가된다

unless
- 캐싱 결과를 저장하지 않을 조건을 지정
- spel을 사용하며 조건이 true일 때 캐시 저장이 방지된다
- 메서드 실행 후 결과값을 기반으로 평가된다

sync
- 캐시 조회와 메서드 실행을 동기하여 캐스 미스 시 동시에 여러 스레드가 메서드를 실행하는 것을 방지한다
- 기본값: false

#### 코드

```java
@Service
public class ProductService {

    // 메서드 실행 전 캐시 조회
    // 캐시 이름: products, 캐시 키: id 값
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        
        // 데이터가 없는 경우 메서드를 실행하고 반환값을 캐시에 저장한다
        return productRepository.findById(id).orElseThrow();
    }
}
```

### @CachePut

#### 동작 방식

메서드를 실행하고 결과를 캐시에 저장한다

항상 데이터를 갱신한다

#### 속성

[@Cacheable 속성](#속성)의 sync 속성을 제외하고 모두 동일하다

```java
@Service
public class ProductService {

    @CachePut(value = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }
}
```

### @CacheEvict

#### 동작 방식

캐시에서 데이터를 제거한다

#### 속성

[@Cacheable 속성](#속성)의 sync 속성을 제외하고 모두 동일하다

추가적으로 allEntries와 beforeInvocation 속성을 제공한다

allEntries
- 캐시 이름(cacheName)에 해당하는 모든 데이터를 삭제한다
- 캐시의 크기가 크거나 키의 수가 많을 경우 성능에 영향을 줄 수 있다
- 기본값: false
- true일 시 key 속성을 별도로 지정하지 않는다

beforeInvocation
- 메서드 호출 전 캐시 데이터를 삭제할 지에 대한 여부
- 기본값: false -> 메서드 호출이 정상적으로 수행됐을 때 데이터를 제거한다
- true로 하면 메서드의 결과에 관계없이(예외 발생 포함) 데이터를 제거한다

```java
@Service
public class ProductService {

    @CacheEvict(value = "products", key = "#id")
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}
```

### @Caching

#### 활용

@Caching 어노테이션은 내부적으로 @Cacheable[] @CachePut[] @CacheEvict[] 속성을 가지고 있다

한 메서드에서 복합적인 캐시 동작을 정의할 때 @Caching을 활용할 수 있다

```java
@Service
public class UserService {

    @Caching(
        cacheable = {
            @Cacheable(cacheNames = "users", key = "#id", unless = "#result == null")
        },
        put = {
            @CachePut(cacheNames = "usersByName", key = "#result.name", unless = "#result == null")
        }
    )
    public User getUserById(Long id) {
        // DB에서 사용자 정보를 가져오는 로직
        return userRepository.get(id);
    }

    @Caching(
        evict = {
            @CacheEvict(cacheNames = "users", key = "#id"),
            @CacheEvict(cacheNames = "usersByName", allEntries = true)
        }
    )
    public void deleteUser(Long id) {
        userRepository.remove(id);
    }

    public void saveUser(User user) {
        userRepository.put(user.getId(), user);
    }
}
```