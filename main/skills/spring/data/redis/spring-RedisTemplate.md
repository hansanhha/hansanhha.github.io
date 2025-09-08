---
layout: default
title:
---

[RedisTemplate](#redistemplate)

[데이터 타입에 따른 작업 수행 객체 반환 메서드](#데이터-타입에-따른-작업-수행-객체-반환-메서드)

[데이터 조작 메서드](#데이터-조작-메서드-string)

[lua 스크립트 실행 메서드](#lua-스크립트-실행-메서드)

[트랜잭션 메서드](#트랜잭션-메서드)

[메시지 발행 메서드](#메시지-발행-메서드)

## RedisTemplate

RedisTemplate는 레디스와 상호작용을 쉽게 할 수 있도록 도와주는 헬퍼 클래스로 스레드 세이프하여 멀티 스레드 환경에서 동기화 문제를 일으키지 않는다

## 데이터 타입에 따른 작업 수행 객체 반환 메서드

하나의 키는 하나의 데이터 타입을 가진 값을 그룹화한다

레디스는 다양한 데이터 타입(string, hash 등)을 지원하는데 해당 키의 데이터 타입에 맞는 명령어로만 작업을 수행할 수 있다

RedisTemplate 클래스는 데이터 타입 별로 레디스 작업을 수행하는 객체를 나누고 해당 객체에 접근할 수 있는 getter 메서드를 지원한다

따라서 레디스 데이터 작업을 진행하려면 이 게터 메서드를 통해 수행 객체를 획득하고 각 객체에서 제공하는 메서드를 사용하면 된다

### `opsFor<data type>()`

RedisTemplate의 메서드 이름은 레디스의 데이터 구조와 작업 방식에 따라 명확하게 지어져있다

레디스에서 지원하는 데이터 타입에 따라 작업을 수행할 수 있는 객체를 반환한다

| 데이터 타입           | 메서드                 | 반환 타입                 |
|------------------|---------------------|-----------------------|
| string           | opsForValue()       | ValueOperations       |
| hash             | opsForHash()        | HashOperations        |
| list             | opsForList()        | ListOperations        |
| set              | opsForSet()         | SetOperations         |
| sorted set(zset) | opsForZSet()        | ZSetOperations        |
| hyperloglog      | opsForHyperLogLog() | HyperLogLogOperations |
| cluster          | opsForCluster()     | ClusterOperations     |
| geo              | opsForGeo()         | GeoOperations         |
| stream           | opsForStream()      | StreamOperations      |

### `bound<data type>Ops()`

특정 키에 바인딩된 데이터 타입에 따라 작업을 수행할 수 있는 객체를 반환한다

특정 키에 대한 컨텍스트를 유지하여 동일한 키에 대해 여러 작업을 수행할 때, 키를 매번 명시하지 않아도 된다

| 데이터 타입            | 메서드                 | 반환 타입                 |
|-------------------|---------------------|-----------------------|
| string            | boundValueOps(key)  | BoundValueOperations  |
| hash              | boundHashOps(key)   | BoundHashOperations   |
| list              | boundListOps(key)   | BoundListOperations   |
| set               | boundSetOps(key)    | BoundSetOperations    |
| sorted set (zset) | boundZSetOps(key)   | BoundZSetOperations   |
| geo               | boundGeoOps(key)    | BoundGeoOperations    |
| stream            | boundStreamOps(key) | BoundStreamOperations |

## execute(RedisCallback)

### 파라미터

#### RedisCallback
- 레디스의 저수준 명령을 실행할 수 있는 콜백 인터페이스, 레디스 연결(RedisConnection(jedis, lettuce 등))에 직접 접근하여 레디스 명령을 실행하는 데 사용된다
- 여려 명령을 파이프라인 방식으로 효율적으로 처리할 수 있다 -> 네트워크 왕복 횟수를 줄일 수 있다
- 트랜잭션 처리 가능
- 실제 명령을 처리하는 역할은 RedisConnection이고 RedisCallback은 RedisConnection에게 명령을 콜백으로 위임하면서 파이프라인, 트랜잭션 같은 부가 기능을 제공한다

```java
// <T>: 레디스 명령 결과 반환 제네릭 타입
public interface RedisCallback<T> {
    
    // RedisConnection(jedis, lettuce 등) 객체를 사용하여 작업을 수행한 뒤 결과 반환
    @Nullable
    T doInRedis(RedisConnection connection) throws DataAccessException;
}
```

#### exposeConnection

RedisConnection 객체에 대한 프록시 적용 여부

#### pipeline

명령어를 파이프라인 방식으로 수행하는지에 대한 여부

### 코드

```java
@Nullable
public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {

    // RedisConnection(jedis, lettuce 등) 생성
    RedisConnectionFactory factory = getRequiredConnectionFactory();
    RedisConnection conn = RedisConnectionUtils.getConnection(factory, enableTransactionSupport);

    // 레디스 작업 수행
    try {

        // RedisConnection 전처리
        boolean existingConnection = TransactionSynchronizationManager.hasResource(factory);
        RedisConnection connToUse = preProcessConnection(conn, existingConnection);

        // 파이프라인 설정
        boolean pipelineStatus = connToUse.isPipelined();
        if (pipeline && !pipelineStatus) {
            connToUse.openPipeline();
        }

        // RedisConnection 프록시 설정
        RedisConnection connToExpose = (exposeConnection ? connToUse : createRedisConnectionProxy(connToUse));
        
        // 레디스 작업 수행 및 결과 반환
        T result = action.doInRedis(connToExpose);

        // 파이프라인 해제
        if (pipeline && !pipelineStatus) {
            connToUse.closePipeline();
        }

        // 결과 후처리
        return postProcessResult(result, connToUse, existingConnection);
        
    } finally {
        
        // RedisConnection 해제
        RedisConnectionUtils.releaseConnection(conn, factory);
    }
}
```

## 데이터 조작 메서드 (string)

opsForValue() 메서드는 단순 값(레디스는 내부적으로 모든 데이터를 string으로 처리한다)에 대한 작업을 수행하는 ValueOperations 객체를 반환한다

ValueOperations의 기본 구현체: DefaultValueOperations

### 삽입

#### `set(key, value)`

키에 대한 값 삽입

```java
@Override
public void set(K key, V value) {

    // 키와 값에 대한 직렬화
    byte[] rawKey = rawKey(key);
    byte[] rawValue = rawValue(value);

    // 레디스 SET 명령 수행
    execute(connection -> connection.set(rawKey, rawValue));
}
```

#### `set(key, value, offset)`

지정된 오프셋에서 시작하는 키 부분의 값을 덮어씌운다

```java
@Override
public void set(K key, V value, long offset) {

    // 키와 값에 대한 직렬화
    byte[] rawKey = rawKey(key);
    byte[] rawValue = rawValue(value);

    // 레디스 SETRANGE 명령 수행
    execute(connection -> {
        connection.setRange(rawKey, rawValue, offset);
        return null;
    });
}
```

#### `setBit(key, long, boolean)`

키에 저장된 값의 오프셋에 비트 설정

```java
@Override
public Boolean setBit(K key, long offset, boolean value) {

    // 키 직렬화
    byte[] rawKey = rawKey(key);
    
    // 레디스 SETBIT 명령 수행
    return execute(connection -> connection.setBit(rawKey, offset, value));
}
```

#### `setIfAbsent(key, value)`

키가 없는 경우 키와 값을 함께 설정

```java
@Override
public Boolean setIfAbsent(K key, V value) {

    // 키와 값 직렬화
    byte[] rawKey = rawKey(key);
    byte[] rawValue = rawValue(value);
    
    // 레디스 SET NX PX 명령 수행
    return execute(connection -> connection.set(rawKey, rawValue, Expiration.persistent(), SetOption.ifAbsent()));
}
```

### 조회(삭제 및 ttl 관련 포함)

#### `get(key)`

키에 대한 값 조회

```java
@Override
public V get(Object key) {
    
    // 레디스 GET 명령 수행
    return execute(valueCallbackFor(key, DefaultedRedisConnection::get));
}
```

#### `get(key, start, end)`

일정 범위에 속한 값 조회

```java
@Override
public String get(K key, long start, long end) {
    byte[] rawKey = rawKey(key);
    byte[] rawReturn = execute(connection -> connection.getRange(rawKey, start, end));

    // 레디스 GETRANGE 명령 수행
    return deserializeString(rawReturn);
}
```

#### `getAndSet(key, value)`

키에 대해 새로운 값으로 대체하고, 기존 값을 반환한다

```java
@Override
public V getAndSet(K key, V newValue) {

    byte[] rawValue = rawValue(newValue);
    
    // 레디스 GETSET 명령 수행
    return execute(valueCallbackFor(key, (connection, rawKey) -> connection.getSet(rawKey, rawValue)));
}
```

#### `getAndPersist(key)`

값을 조회하면서 키에 대한 ttl을 제거하고 영속화한다

```java
public V getAndPersist(K key) {
    
    // 레디스 GETEX 명령 수행
    return execute(valueCallbackFor(key, (connection, rawKey) -> connection.getEx(rawKey, Expiration.persistent())));
}
```

#### `getAndDelete(key)`

값을 조회하고 키를 삭제한다

```java
public V getAndDelete(K key) {
    
    // 레디스 GETDEL 명령 수행
    return execute(valueCallbackFor(key, DefaultedRedisConnection::getDel));
}
```

#### `getAndExpire(key, timeout)`

값을 조회하고 키에 대한 ttl을 설정한다

```java
public V getAndExpire(K key, long timeout, TimeUnit unit) {
    
    // 레디스 GETEX 명령 수행
    return execute(
            valueCallbackFor(key, (connection, rawKey) -> connection.getEx(rawKey, Expiration.from(timeout, unit))));
}
```

### 값 증가, 감소

#### increment(key)

키에 대한 값을 1만큼 증가시킨다 

```java
@Override
public Long increment(K key) {

    byte[] rawKey = rawKey(key);
    
    // 레디스 INCR 명령 수행
    return execute(connection -> connection.incr(rawKey));
}
```

#### decrement(key)

키에 대한 값을 1만큼 감소시킨다

```java
@Override
public Long decrement(K key) {

    byte[] rawKey = rawKey(key);
    
    // 레디스 DECR 명령 수행
    return execute(connection -> connection.decr(rawKey));
}
```

## lua 스크립트 실행 메서드

### 메서드

```java
public <T> T execute(RedisScript<T> script, List<K> keys, Object... args) {
    return scriptExecutor.execute(script, keys, args);
}
```

`RedisScript<T>`: 실행할 lua 스크립트를 캡슐화한 객체

`List<K>`: 스크립트에서 사용될 키 목록 (레디스 스크립트의 KEYS 배열)

`Object...`: 스크립트에서 사용될 추가적인 인자들  (레디스 스크립트의 ARGV 배열)

`<T>`: 스크립트 실행 결과 반환 타입

### 예시 코드 

특정 키를 삭제하고 새 값을 설정한다

```java
String luaScript =
        """
        redis.call('DEL', KEYS[1])
        redis.call('SET', KEYS[1], ARGV[1])
        return redis.call('GET', KEYS[1])
        """;

DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
redisScript.setScriptText(luaScript);
redisScript.setReturnType(String.class);

String result = redisTemplate.exeute(
        redisScript, Collections.singletonList("hansanhha"), "redis");

System.out.println(result) // redis
```

## 트랜잭션 메서드

### RedisTemplate 사용

RedisTemplate은 레디스 자체에서 제공하는 트랜잭션 관리 명령어를 수행하는 메서드를 제공한다

내부적으로 트랜잭션/배치 작업을 담당하는 RedisTxCommands 객체에게 수행을 위임한다 

#### `void multi()`

트랜잭션 시작 (레디스 MULTI 명령 수행)

명령은 대기열에 추가되며 exec 메서드를 통해 실행할 수 있다

또는 discard 메서드를 통해 롤백할 수도 있다

#### `List<Object> exec()` 

트랜잭션 시작 이후 대기열에 속한 모든 명령을 실행한다 (레디스 EXEC 명령 수행)

watch 메서드로 특정 키를 감시하고 있는데, 트랜잭션 도중 어느 키가 수정된다면 작업은 실패한다

exec 메서드는 실행된 각 메서드의 응답 결과를 반환한다

#### `void discard()`

트랜잭션 롤백

#### `void watch(byte[]... keys)`

트랜잭션 시작된 후 주어진 키를 감시한다 (레디스 WATCH 명령 수행)

다른 클라이언트에서 해당 키를 수정한 경우 트랜잭션을 롤백한다

#### `void unwatch()`

감시하고 있는 모든 키를 해제한다 (레디스 UNWATCH 명령 수행)

## 메시지 발행 메서드

```java
@Override
public Long convertAndSend(String channel, Object message) {

    // 채널, 메시지 직렬화
    byte[] rawChannel = rawString(channel);
    byte[] rawMessage = rawValue(message);

    // 레디스 PUBLISH 명령 수행
    return execute(connection -> connection.publish(rawChannel, rawMessage), true);
}
```