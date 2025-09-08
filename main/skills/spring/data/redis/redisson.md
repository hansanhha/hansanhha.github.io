[redisson](#reddison)

[redisson vs lettuce](#redisson-vs-lettuce)


## reddison

redis 및 valkey(레디스 라이센스 변경 전 fork한 새 오픈소스 프로젝트)를 지원하는 네티 기반 비동기/락프리 클라이언트

[reddison vs spring data redis](https://redisson.org/articles/feature-comparison-redisson-vs-spring-data-redis.html)

### 주요 기능

[기능 목록 상세](https://github.com/redisson/redisson?tab=readme-ov-file#features)

스레드 세이프

auto reconnection

비동기 커넥션풀

api 
- reactive streams api
- rxjava3 api 
- asynchronous api
- cache api(spring cache, hibernate cache 등)


## redisson vs lettuce

### lettuce 

스프링 데이터 레디스는 lettuce를 스프링 애플리케이션의 기본 레디스 클라이언트로 설정한다

레디스는 단일 노드를 기준으로 락을 관리하므로 다중 노드 환경에서는 신뢰성을 보장하지 못하므로 레디스의 redlock 알고리즘을 사용해야 한다

lettuce는 레디스 명령을 사용하여 락을 구현하는 방법을 사용자에게 위임한다

또한 레디스는 락 대기 큐를 관리하지 않기 때문에 락을 획득하지 못한 클라이언트는 직접 재시도해야 한다

-> 분산 락 구현을 위해 모두 사용자가 담당하고 lettuce에서 별도의 기능을 제공해주지 않는 단점이 있다 

#### 예시 코드

```java
public boolean acquireLock(String lockKey, String lockValue) {
    long start = System.currentTimeMillis();

    // 락 획득 시도 유효 시간 내에 락을 획득할 때 까지 반복적으로 레디스에게 락 획득을 시도한다 (스핀 락)
    while (System.currentTimeMillis() - start < TIMEOUT) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue);

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
```

### reddison

분산 락을 구현할 때 reddison을 사용하면 lettuce에 비해 몇 가지 장점을 얻을 수 있다

#### 내장된 고수준 api

reddison은 기본적으로 분산 락, 세마포어, 공정 락 등 다양한 동시성 도구를 제공한다

분산 락을 구현할 때 별도의 코드를 직접 작성하지 않아도 RLock 객체를 통해 쉽게 락을 사용할 수 있다

lettuce를 사용하면 락 구현을 위해 직접 redis 명령어와 스핀 락을 구현해야 하는 반면 reddison은 이런 작업을 추상화한다

#### 자동 락 갱신 (lease time extension)

redisson은 watchdog을 통해 락이 만료되지 않도록 자동 갱신 기능을 제공한다

클라이언트가 작업을 완료하기 전에 락이 만료될 걱정이 줄어든다

lettuce를 사용하면 이를 직접 구현해야 하며 갱신 기능을 만들지 않으면 락이 예상치 못하게 종료될 수 있다

#### 다양한 데이터 구조 지원

redisson은 분산 맵, 분산 리스트 등 분산 컬렉션 및 lettuce에서 제공하지 않는 여러 데이터 구조를 지원한다

#### redlock 알고리즘 지원

redisson은 redlock 알고리즘을 내장하며 다중 노드 환경에서의 분산 락 구현을 지원한다

lettuce는 redlock 알고리즘을 직접 구현해야 한다

#### 마냥 다 좋은 것만은 아니다

lettuce에 비해 더 많은 기능을 포함하고 있어 상대적으로 의존성 크기가 크며 약간의 오버헤드가 발생할 수 있다

레디스 cluster, replicated 모드를 사용하려면 apache 2.0 대신 reddison pro 라이선스(유료)가 필요하다 


## redisson 분산 락 구현

