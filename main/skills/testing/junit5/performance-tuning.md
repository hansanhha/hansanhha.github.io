---
layout: default
title:
---

[performance tuning](#performance-tuning)

[1. parallel execution](#1-parallel-execution)

[2. test instance lifecycle configuration](#2-test-instance-lifecycle-configuration)

[3. gradle test task tuning](#3-gradle-test-task-tuning)


## performance tuning

junit 5에서 테스트 실행 속도를 최적화하면 ci/cd 환경에서의 테스트 수행 시간을 줄여 개발 생산성을 높일 수 있다


## 1. parallel execution

junit 5는 기본적으로 테스트를 한 개씩 순차 실행하지만 [병렬 실행을 활성화하거나 특정 테스트만 병렬 실행](./parallel%20test.md)하면 여러 테스트를 동시에 실행할 수 있다

다만 병렬 실행에 따른 여러 문제(공유 자원 동기화 문제 등)가 발생할 가능성이 있기 때문에 이에 대한 대처를 적절히 해야 한다


## 2. test instance lifecycle configuration

junit 5는 각 테스트 메서드 실행할 때마다 새로운 테스트 인스턴스를 생성하는 것이 기본 동작이다

테스트 클래스에 하나의 인스턴스만 생성하도록 `@TestInstance(Lifecycle.PER_CLASS)` 어노테이션을 적용하여 성능을 개선할 수 있다

다만 테스트 케이스 별로 독립된 환경에서 테스트를 실행하지 않기 때문에 공유 필드에 대한 동기화 문제가 발생할 수 있다


## 3. gradle test task tuning

gradle의 test task를 병렬 실행하도록 설정하거나 테스트가 성공적으로 실행된 경우 테스트 결과를 캐싱하여 성능을 개선할 수 있다

```kotlin
tasks.withType<Test> {
    
    useJUnitPlatform()

    // cpu 코어 개수의 절반 만큼 병렬 실행
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2

    // 테스트가 성공적으로 실행된 경우 테스트 결과 캐싱
    outputs.cacheIf { true }
}

```




