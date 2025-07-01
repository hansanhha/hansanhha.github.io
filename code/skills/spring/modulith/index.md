---
layout: index
title:
---

## Fundamental Concepts

spring modulith : 스프링 부트 애플리케이션에서 프로젝트 구조를 논리적인 모듈로 구성할 수 있도록 도와주는 프레임워크

주요 기능
- 애플리케이션 구조 검증
- 모듈 의존성 문서화
- 개별/통합 모듈 테스트
- 런타임 모듈 상호작용 observing
- 모듈 간 느슨한 결합 구현(비동기, 이벤트 처리)

Application Modules 구성 타입
- provided interface : 노출할 API(다른 모듈에 노출할 스프링 빈, 모듈에 의해 발행되는 이벤트)
- internal implementation component : 모듈 내부에서만 사용하는 컴포넌트 
- required interface : 참조하는 API(의존성 주입받을 다른 모듈의 스프링 빈, 수신할 이벤트, 사용할 프로퍼티)

Application Module Structure
```
- src/main/java
    - shop (root package)
        - @SpringBootApplication class
        - order(module package)
            - package-info.java
            - OrderManagement.class
            - OrderInternal.class
            - events (package)
                - OrderEnvent.class
                - package-info.java
        - inventory(other module package)
            - ...
```

module 패키지는 api(provided interface) 패키지로 취급 : 외부에서 참조 가능

module 하위 패키지는 internal로 취급 : 외부에서 참조 불가능
- 스프링 modulith 런타임에 논리적으로 모듈 간 의존성을 분석하기 때문에 컴파일러를 통해 에러를 잡을 수 없음 
- 잘못된 접근(참조)은 애플리케이션 로드 시점에 에러 발생

## Application Module 의존성 명시

NamedInterface 참조 선언이나 자신의 모듈 의존성을 명확하게 표현하여 제한할 수 있음

package-info.java에 @ApplicationModule을 사용하여 접근할 의존성을 명시

필수는 아니며, 명시하지 않은 경우 api 패키지만 접근 가능

```java
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = "order"
)
package com.hansanhha.spring.shop.inventory;
```

위의 설정으로 inventory 모듈은 order 모듈만 접근 가능

### NamedInterface

특정 module 하위 패키지를 API로 제공할 때 사용

API로 제공할 하위 패키지의 packgae-info.java에 @NamedInterface 사용

```java
@org.springframework.modulith.NamedInterface("events")
package com.hansanhha.spring.shop.order.events;
```

api를 사용할 곳의 package-info.java에 공개한 API 명시(:: 구분자 사용)
```java
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = "order::events"
)
package com.hansanhha.spring.shop.inventory;
```

## Application Event

이벤트 발행-소비를 통해 모듈 간 상호작용을 분리

### 기존의 방식과 이벤트 기반 방식 비교

#### 기존 방식

```java
@Service
@RequiredArgsConstructor
public class OrderManagement {

  private final InventoryManagement inventory;

  @Transactional
  public void complete(Order order) {
      
      // business logic ...
      
      inventory.updateStockFor(order);
  }
}
```

기존의 방식은 기능적으로 관련된 다른 모듈의 스프링 빈을 주입받아 사용함

OrderManagement를 테스트할 때 의존성 주입이 필요하며, 테스트가 복잡해짐 

#### 이벤트 기반

```java
@Service
@RequiredArgsConstructor
public class OrderManagement {

    private final ApplicationEventPublisher events;
    private final OrderInternal dependency;

    @Transactional
    public void complete(Order order) {

        // State transition on the order aggregate go here

        events.publishEvent(new OrderCompleted(order.getId()));
    }
}
```

primary aggregate의 상태 변경 후 Spring ApplicationEventPublisher를 통해 이벤트 발행

이벤트 발행은 기본적으로 동기적으로 동작함

따라서 전체 트랜잭션 의미는 기존 방식과 동일하게 전체 실패 or 성공 처리됨 - 일관된 모델 유지 가능

다만 트랜잭션 범위가 넓어지며 전체 트랜잭션이 실패할 수 있는 잠재력이 있음(중요하지 않은 에러임에도 불구하고) 

#### 비동기 이벤트 처리

```java
@Component
class InventoryManagement {

  @Async
  @TransactionalEventListener
  void on(OrderCompleted event) { /* … */ }
}
```

비동기적으로 이벤트를 처리하면 Event 리스너의 실행과 이벤트 발행자의 original 트랜잭션을 분리

-> original 트랜잭션 확장을 피할 수 있음

다만 이벤트가 손실되거나, 리스너가 호출되기도 전에 시스템이 실패하는 경우 전체 기능이 완전하게 작동하지 않을 수 있음

### ApplicationModuleListener

```java
@Async
@Transactional(propagation = Propagation.REQUIRES_NEW)
@TransactionalEventListener
@Documented
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationModuleListener {

    @AliasFor(annotation = Transactional.class, attribute = "readOnly")
    boolean readOnlyTransaction() default false;

    @AliasFor(annotation = EventListener.class, attribute = "id")
    String id() default "";
}
```

스프링 모듈리스에서 제공하는 합성 어노테이션(shortcut 용)으로, @Async, @Transactional, @TransactionalEventListener를 포함함

자체적인 트랜잭션 안에서 비동기적으로 트랜잭션 이벤트 처리를 수행

### 이벤트 기반 동작 정리

1. 이벤트 정의 
2. primary aggregate 상태 변경 후 스프링 ApplicationEventPublisher으로 이벤트 발행
3. 다른 모듈에서 @ApplicationModuleListener를 통해 이벤트 수신 및 로직 실행

### EventPublicationRegistry

이벤트 발행을 처리하는 역할

이벤트가 발행되면 두 가지 동작을 수행함
1. 해당 이벤트의 리스너 검색
2. 각 리스너를 이벤트 발행 로그에 기록(각 요소는 original 트랜잭션의 일부로 취급)

리스너의 실행이 성공하면 해당 리스너는 로그 항목을 완료된 것으로 표시하는 aspect로 감싸짐

실패할 경우 로그 항목을 가만히 두고, 애플리케이션의 필요에 따라 다시 시도할 수 있음(기본적으로 성공하지 못한 모든 이벤트 발행들은 애플리케이션 시작 시 다시 제출됨)

### EventPublicationRegistry 관련 요소

#### org.springframework.modulith:spring-modulith-events-api

완료된 이벤트, 완료되지 않은 이벤트에 대해 다양한 처리를 위한 API를 제공하는 의존성 

주요 추상화
- CompletedEventPublications : 완료된 이벤트 접근, 삭제, 지정된 시간보다 오래된 이벤트 삭제
- IncompleteEventPublications : 완료되지 않은 이벤트 다시 제출, 지정된 시간보다 오래된 이벤트 다시 제출

#### EventPublicationRepository

실제로 이벤트 발행 로그를 작성하는 SPI(Service Provider Interface)

JPA, JDBC, MongoDB 등 다양한 저장소에 대한 구현체 및 스타터 제공(spring-modulith-starter-jpa 등)

#### EventSerializer

각 이벤트 로그 항목들은 직렬화된 original 이벤트가 포함되어 있음

EventSerializer를 통해 이벤트 인스턴스를 DataSource 포맷에 맞게 직렬화함

스프링 부트 autoconfiguration을 통해 기본적으로 JacksonEventSerializer(ObjectMapper 사용)가 등록됨

#### 이벤트 발행 Date 커스텀

Event Publication Registry는 기본적으로 Clock.systemUTC()를 통해 이벤트 발행 시간을 기록함

Clock 타입의 빈을 등록하여 시간 형식을 커스텀 가능 

```java
@Configuration
class MyConfiguration {

  @Bean
  Clock myCustomClock() {
      // ...
  }
}
```

### Application Event 동작 관련 개념

aggregate-driven

spring data application event publication mechanism

primary aggregate

## Testing

특정 모듈만, 모듈 조합만을 대상으로 하는 통합 테스트 환경을 구축할 수 있음(bootstrapping)

JUnit 테스트 클래스에 @ApplicationModuleTest 선언

@SpringBootTest의 동작과 유사하나, 테스트 클래스가 위치한 모듈만 bootstrap하도록 제한함

```java
@Target({ElementType.TYPE})
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@TypeExcludeFilters({ModuleTypeExcludeFilter.class})
@ImportAutoConfiguration({ModuleTestAutoConfiguration.class})
@ExtendWith({PublishedEventsParameterResolver.class, ScenarioParameterResolver.class})
@TestInstance(Lifecycle.PER_CLASS)
@TestConstructor(
    autowireMode = AutowireMode.ALL
)
public @interface ApplicationModuleTest {
    @AliasFor("mode")
    BootstrapMode value() default ApplicationModuleTest.BootstrapMode.STANDALONE;

    @AliasFor("value")
    BootstrapMode mode() default ApplicationModuleTest.BootstrapMode.STANDALONE;

    boolean verifyAutomatically() default true;

    String[] extraIncludes() default {};

    @AliasFor(
        annotation = SpringBootTest.class
    )
    SpringBootTest.WebEnvironment webEnvironment() default WebEnvironment.MOCK;

    public static enum BootstrapMode {
        STANDALONE(DependencyDepth.NONE),
        DIRECT_DEPENDENCIES(DependencyDepth.IMMEDIATE),
        ALL_DEPENDENCIES(DependencyDepth.ALL);

        private final DependencyDepth depth;

        private BootstrapMode(DependencyDepth depth) {
            this.depth = depth;
        }

        public DependencyDepth getDepth() {
            return this.depth;
        }
    }
}
```

BootStrap 모드 종류
- STANDARD (default) : 테스트 클래스가 위치한 모듈으로 제한
- DIRECT_DEPENDENCIES : 테스트 클래스가 위치한 모듈과 직접적으로 의존하는 모듈로 제한
- ALL_DEPENDENCIES : 테스트 클래스가 위치한 모듈과 모든 의존하는 모듈로 제한

### Scenario

통합 테스트에서 동시성 처리(비동기, 트랜잭션 이벤트 처리)가 미묘한 오류를 발생시킬 수 있음

또한 다음 요소들을 필요로 함
- 이벤트가 제대로 발행됐는지, 리스너들이 수신했는지 확인해야 하는 TransactionOperations, ApplicationEventProcessor
- 동시성 처리를 위한 Awaitility
- 테스트 실행 결과에 대한 기댓값을 위한 AssertJ assertions

Scenario는 모듈 통합 테스트 정의를 쉽게 하기 위한 추상화임

@ApplicationModuleTest 클래스의 테스트 메서드 파라미터로 Scenario를 주입받아서 사용할 수 있음
```java
@ApplicationModuleTest
class SomeApplicationModuleTest {

  @Test
  public void someModuleIntegrationTest(Scenario scenario) {
    // ...
  }
}
```

테스트 정의 구조
1. 시스템에 대한 stimulus 정의(이벤트 발행, 모듈에 노출된 스프링 컴포넌트 호출)
  - stimulus : 시스템이나 모듈에 대한 특정한 입력이나 작용(시스템이나 모듈이 반응하도록 유도하는 호출을 가리킴)   
2. 실행 디테일 커스텀(timeout 등) - optional
3. 결과 기댓값 정의(조건에 일치하는 이벤트 발행, 노출된 컴포넌트를 호출하여 감지할 수 있는 모듈의 상태 변경 등)
4. 수신된 이벤트나 변경된 상태에 대한 추가 확인 - optional

### 사용법

- stimulus 정의(Scenario 시작 부분)

```java
// 이벤트 발행 시작
scenario.publish(new MyApplicationEvent(…)).…
        
// 빈 호출 시작
scenario.stimulate(() -> someBean.someMethod(…)).…
```

- 발행될 이벤트 또는 상태 변경 대기, 매칭 조건 정의
```java
….andWaitForEventOfType(SomeOtherEvent.class)
 .matching(event -> …) // Use some predicate here
 .…
```

- scenario 실행 최종 operation
```java
// scenario 실행
….toArrive(…)
    
// scenario 실행 후 수신된 이벤트 assertion 정의
….toArriveAndVerify(event -> …)
```

- 전체 Scenario 정의 코드
```java
scenario.publish(new MyApplicationEvent(…))
        .andWaitForEventOfType(SomeOtherEvent.class)
        .matching(event -> …)
        .toArriveAndVerify(event -> …);
```

### Scenario 실행 커스텀

- 개별 scenario 실행 커스텀
```java
scenario.publish(new MyApplicationEvent(…))
  .customize(it -> it.atMost(Duration.ofSeconds(2)))
  .andWaitForEventOfType(SomeOtherEvent.class)
  .matching(event -> …)
  .toArriveAndVerify(event -> …);
```

- 글로벌 scenario 실행 커스텀
```java
@ExtendWith(MyCustomizer.class)
class MyTests {

  @Test
  void myTestCase(Scenario scenario) {
    // scenario will be pre-customized with logic defined in MyCustomizer
  }

  static class MyCustomizer implements ScenarioCustomizer {

    @Override
    Function<ConditionFactory, ConditionFactory> getDefaultCustomizer(Method method, ApplicationContext context) {
      return it -> …;
    }
  }
}
```

## 참고

[spring modulith docs](https://docs.spring.io/spring-modulith/reference/index.html)


