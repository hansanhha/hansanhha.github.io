---
layout: default
title:
---

[parallel test](#parallel-test)

[configurations](#configurations)

[examples (@Execution)](#examples-execution)


## parallel test

junit 5는 기본적으로 순차 실행(sequential execution) 방식으로 동작하지만 병렬 실행 설정을 활성화하면 여러 개의 테스트를 동시에 실행할 수 있다

테스트 병렬 실행 시 다음과 같은 점을 염두에 둬야한다
- 공유 자원 동기화 문제: synchronized, ReentrantLock 또는 ThreadLocal 사용
- 테스트 순서 보장(필요 시): @Order 또는 순차 실행 강제 `@Execution(SAME_THREAD)`
- db, 파일 i/o 충돌: 테스트마다 별도 트랜잭션 사용


## configurations

`src/test/resources/junit-platform.properties` 파일 생성 후 병렬 실행 설정을 활성화시킨다

```properties
# 병렬 실행 활성화
junit.jupiter.execution.parallel.enabled=true

# 모든 테스트 메서드를 병렬 실행
junit.jupiter.execution.parallel.mode.default=concurrent

# 모든 클래스의 테스트를 병렬 실행
junit.jupiter.execution.parallel.mode.classes.default=concurrent
```


## examples (@Execution)

병렬 활성화 프로퍼티 설정과 별개로 @Execution 어노테이션을 통해 각 테스트마다 실행 방식을 다르게 설정할 수도 있다

```java
@Test
// 병렬 실행 모드 지정
@Execution(ExecutionMode.CONCURRENT)
void test1() throws InterruptedException {
    System.out.println(Thread.currentThread().getName() + " - 실행: test1");
}

@Test
// 순차 실행 모드 지정
@Execution(ExecutionMode.SAME_THREAD)
void sequentialTest() throws InterruptedException {
    System.out.println(Thread.currentThread().getName() + " - 순차 실행됨");
}

@Test
void test2() throws InterruptedException {
    System.out.println(Thread.currentThread().getName() + " - 실행: test2");
}

@Test
void test3() throws InterruptedException {
    System.out.println(Thread.currentThread().getName() + " - 실행: test3");
}
```

