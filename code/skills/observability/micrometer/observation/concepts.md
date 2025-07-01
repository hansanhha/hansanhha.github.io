---
layout: default
title:
---

#### index
- [micrometer observation](#micrometer-observation)
- components
  - [Observation](#observation)
  - [Observation.Context](#observationcontext)
  - [Observation.Scope](#observationscope)
  - [ObservationHandler](#observationhandler)
  - [ObservationFilter](#observationfilter)
  - [ObservationConvention](#observationconvention)
  - [ObservationPredicate](#observationpredicate)
  - [ObservationRegistry](#observationregistry)
- [@Observed](#observed)
- [workflow](#workflow)
- [gradle configuration](#gradle-configuration)


## micrometer observation

micrometer observation은 1.10 버전부터 도입된 관측(observability) api로 **micrometer metrics**와 **micrometer tracing**을 통합하여 애플리케이션의 메트릭, 로그, 추적을 한 곳에서 관리할 수 있는 기능을 제공한다

기존 micrometer metrics 타입(Timer, Counter, LongTaskTimer 등)과 opentelemetry, zipkin 등과 같은 tracing 도구를 micrometer observation을 통해 통합할 수 있다 

또한 스프링 부트 3 버전부터 @Timed, @Counted 등의 어노테이션이 observation 기반으로 동작하도록 전환되었다

커스텀 ObservationHandler를 구현하여 로그, 메트릭, 트레이싱 외에 다른 데이터도 수집할 수 있다

### 주요 활용 사례
- http 요청/응답 시간 측정: @Observed를 사용하여 각 요청별 지연 시간을 자동으로 측정한다
- 데이터베이스 쿼리 모니터링: jdbc, r2dbc와 같은 데이터베이스 호출을 자동으로 관찰하여 쿼리 성능을 분석한다
- 외부 api 호출 성능 측정: RestTemplate, WebClient와의 통합을 통해 외부 api 호출 시간을 메트릭으로 수집한다
- 비즈니스 로직 성능 분석: 특정 메서드나 코드 블록을 감싸서 수동으로 관찰을 진행한다


## Observation

observation 작업이나 이벤트를 모니터링하고 감지하는 컴포넌트로 관찰 대상의 로직을 감싸서 observation 작업(span)의 시작/종료를 정의하거나 태그, 에러 등의 관리를 수행한다

관찰 중 발생한 데이터는 [Observation.Context](#observationcontext)를 통해 관리하고 자신의 생명주기 메서드가 호출되면 내부적으로 [ObservationHandler](#observationhandler)의 콜백 메서드를 호출한다

주요 역할
- 관찰 작업 시작, 종료 정의: `start()` `stop()`
- 작업 중 예외 발생 시 예외 처리 동작 정의: `error()`
- 관찰 중 이벤트 발생 시 이벤트 처리 동작 정의: `event()`
- 카디널리티 관리: `highCardinalityKeyValue()` `lowCardinalityKeyValue()`
- Scope 열기: `openScope()` - Scope는 더 이상 사용되지 않으면 `Observation.Scope#close()` 메서드를 통해 닫아줘야 한다
- 관찰 대상 작업 지정: `observe()`

high cardinality는 범위가 제한되지 않은 값을 의미한다 `/example/user1234` `/example/user2345` 등

low cardinality는 제한된 범위의 값을 의미한다 `/example/{userId}`, http 상태 코드 등 

### Observation lifecycle

Observation의 구현체는 start, stop, event, error 등의 메서드가 호출되면 내부적으로 ObservationHandler의 콜백 메서드를 호출한다

이 때 Observation.Context를 매개변수로 넘겨 관찰 데이터를 공유하거나 수정한다

```text
        Observation           Observation
        Context               Context
Created ----------> Started(Event, Error) ----------> Stopped
```

### Observation scope flow

```text
              Observation
              Context
Scope Started ----------> Scope Finished
```


## Observation.Context

관찰 중에 발생하는 메타데이터와 상태 정보를 담는 변경 가능한 객체로 Map과 메타데이터 필드를 통해 상태를 유지하며 수집, 처리, 전파 등의 작업에서 사용된다

관찰 정보: Map으로 관리

메타데이터: 메타데이터 필드로 관리 - name, contextualName, error, parentObservation, tags(lowCardinality, highCardinality)

Observation이 생성될 때 Context도 함께 생성되거나 별도로 Context 객체만 생성한 뒤 Observation에 연결할 수 있다

관찰을 진행하는 동안 Observation에 의해 호출되는 ObservationHandler들은 Context를 매개변수로 받아 관찰 데이터를 사용하며, Observation이 종료되면 Context 내 수집된 모든 데이터가 등록된 모니터링 시스템으로 전송된다


## Observation.Scope

Scope는 현재 활성화된(현재 스레드에서 사용 중인) Observation을 ThreadLocal에 설정하여 스레드 전역(로깅 시스템, 분산 추적 시스템, 메트릭 수집기 등)에서 Observation의 Context에 접근할 수 있도록 한다

또한 현재 스레드에서 Observation을 공유할 뿐만 아니라 micrometer의 context propagation을 통해 비동기 환경에서도 Observation을 안전하게 전파할 수 있다

`Observation.openScope` 메서드를 통해 새로운 Scope 객체를 생성할 수 있으며, 열린 Scope는 `Scope.close` 메서드를 통해 닫을 수 있다

만약 openScope() 메서드로 호출하지 않고 Context에 데이터를 추가하면 micrometer의 다른 컴포넌트(현재 스레드의 다른 코드)에서 Observation을 인식할 수 없어 이 데이터를 활용하지 못할 수가 있다


## ObservationHandler

ObservationHandler는 Observation의 생명주기(start, stop, error, event 등) 동안 발생하는 이벤트에 대해 observation 작업(메트릭 기록, 로그 출력, 추적 데이터 전파 등)을 처리하는 핸들러 인터페이스(콜백)이다

```java
public interface ObservationHandler<T extends Observation.Context> {

    default void onStart(T context) {}

    default void onError(T context) {}

    default void onEvent(Observation.Event event, T context) {}

    default void onScopeOpened(T context) {}

    default void onScopeClosed(T context) {}

    default void onScopeReset(T context) {}

    default void onStop(T context) {}

    boolean supportsContext(Observation.Context context);
}
```

micrometer observation에서 제공하는 주요 구현체는 다음과 같다


#### MeterObservationHandler

Meter 메트릭 타입에 대한 ObservationHandler임을 나타내는 마커 인터페이스

```java
public interface MeterObservationHandler<T extends Observation.Context> extends ObservationHandler<T> {

    @Override
    default boolean supportsContext(Observation.Context context) {
        return true;
    }

}
```

#### DefaultMeterObservationHandler

`Timer.Sample` `Counter`에 대한 메트릭을 기록하는 MeterObservation 구현체

기본적으로 LongTaskTimer.Sample 타입에 대한 메트릭 기록도 지원하지만 생성자를 통해 기능을 사용하지 않을 수 있다

LongTaskTimer는 `onStart` 메서드에서 생성되는데 이 시점에 접근할 수 있는 태그만 기록할 수 있기 때문에, Observation의 start 생명주기 이후 추가한 태그는 LongTaskTimer의 태그에 등록되지 않는다


```java
public class DefaultMeterObservationHandler implements MeterObservationHandler<Observation.Context> {

    private final MeterRegistry meterRegistry;

    private final boolean shouldCreateLongTaskTimer;

    // 기본적으로 LongTaskTimer를 사용한다
    public DefaultMeterObservationHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.shouldCreateLongTaskTimer = true;
    }

    // 생성자 파라미터에 LONG_TASK_TIMER를 넘겨주면 LongTaskTimer를 사용하지 않는다
    public DefaultMeterObservationHandler(MeterRegistry meterRegistry, IgnoredMeters... metersToIgnore) {
      this.meterRegistry = meterRegistry;
      this.shouldCreateLongTaskTimer = Arrays.stream(metersToIgnore)
              .noneMatch(ignored -> ignored == IgnoredMeters.LONG_TASK_TIMER);
    }

    // Observation.start 메서드에 의해 호출되는 콜백 메서드
    // shouldCreateLongTaskTimer 값에 따라 LongTaskTimer를 생성한 뒤 Context에 저장한다
    // Timer.Sample을 시작한 뒤 Context에 저장한다
    @Override
    public void onStart(Observation.Context context) {
      if (shouldCreateLongTaskTimer) {
        LongTaskTimer.Sample longTaskSample = LongTaskTimer.builder(context.getName() + ".active")
                .tags(createTags(context))
                .register(meterRegistry)
                .start();
        context.put(LongTaskTimer.Sample.class, longTaskSample);
      }
  
      Timer.Sample sample = Timer.start(meterRegistry);
      context.put(Timer.Sample.class, sample);
    }

    // Observation.stop 메서드에 의해 호출되는 콜백 메서드
    // Timer.Sample와 LongTaskTimer(생성된 경우)를 멈춘다
    @Override
    public void onStop(Observation.Context context) {
      List<Tag> tags = createTags(context);
      tags.add(Tag.of("error", getErrorValue(context)));
      Timer.Sample sample = context.getRequired(Timer.Sample.class);
      sample.stop(Timer.builder(context.getName()).tags(tags).register(this.meterRegistry));
  
      if (shouldCreateLongTaskTimer) {
        LongTaskTimer.Sample longTaskSample = context.getRequired(LongTaskTimer.Sample.class);
        longTaskSample.stop();
      }
    }

    // Observation.event 메서드에 의해 호출되는 콜백 메서드
    // Context와 Event 객체에 지정된 이름을 기반으로 Counter를 증가시킨다 
    @Override
    public void onEvent(Observation.Event event, Observation.Context context) {
      Counter.builder(context.getName() + "." + event.getName())
              .tags(createTags(context))
              .register(meterRegistry)
              .increment();
    }
    
}
```

#### ObservationTextPublisher

context 데이터를 String으로 변환하고 Consumer에게 발행하는 핸들러

각 필드의 기본 값은 다음과 같다
- consumer (발행 대상): 로깅 작업을 수행하는 micrometer의 InternalLoggerFactory(내부적으로 slf4j 또는 jdk logger 사용) 인스턴스를 사용한다
- supportsContextPredicate (발행 조건): true를 반환한다
- converter (데이터 변환): String.valueOf 메서드(Object.toString() 호출)를 converter로 사용한다

각 생명주기에 따른 이름을 event 이름으로 설정하고 Context 데이터를 변환한 뒤 consumer에게 전송한다

```java
public class ObservationTextPublisher implements ObservationHandler<Observation.Context> {

    private final Consumer<String> consumer;

    private final Predicate<Observation.Context> supportsContextPredicate;

    private final Function<Observation.Context, String> converter;

    public ObservationTextPublisher() {
        this(InternalLoggerFactory.getInstance(ObservationTextPublisher.class)::info, context -> true, String::valueOf);
    }

    public ObservationTextPublisher(Consumer<String> consumer, Predicate<Observation.Context> supportsContextPredicate,
            Function<Observation.Context, String> converter) {
        this.consumer = consumer;
        this.supportsContextPredicate = supportsContextPredicate;
        this.converter = converter;
    }

    // Observation.start 메서드에 의해 호출되는 콜백 메서드
    @Override
    public void onStart(Observation.Context context) {
        publish("START", context);
    }

    // Observation.event 메서드에 의해 호출되는 콜백 메서드
    @Override
    public void onEvent(Observation.Event event, Observation.Context context) {
        publishUnformatted(String.format("%5s - %s, %s", "EVENT", event, converter.apply(context)));
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return this.supportsContextPredicate.test(context);
    }

    // event 이름과 context 데이터를 포맷한 뒤 publishUnformatted에게 위임하는 메서드 
    private void publish(String event, Observation.Context context) {
        publishUnformatted(String.format("%5s - %s", event, converter.apply(context)));
    }

    // consumer에게 변환된 context 데이터를 전송하는 메서드
    private void publishUnformatted(String event) {
        this.consumer.accept(event);
    }

}
```

#### CompositeObservationHandler

다른 핸들러들을 래핑한 ObservationHandler

```java
interface CompositeObservationHandler extends ObservationHandler<Observation.Context> {

    // 등록된 handler 목록 반환 
    List<ObservationHandler<Observation.Context>> getHandlers();

}
```

#### FirstMatchingCompositeObservationHandler

보관 중인 핸들러 목록으로부터 가장 먼저 매칭된 ObservationHandler 구현체를 선택하는 핸들러

아래의 onStart 메서드를 포함하여 모든 메서드가 동일한 메커니즘을 사용한다 

```java
class FirstMatchingCompositeObservationHandler implements CompositeObservationHandler {
    
    private final List<ObservationHandler<Observation.Context>> handlers;
  
    // Observation.start 메서드에 의해 호출되는 콜백 메서드
    // getFirstApplicableHandler를 통해 가장 먼저 매칭되는 핸들러를 찾은 뒤 요청을 위임한다
    @Override
    public void onStart(Observation.Context context) {
      ObservationHandler<Observation.Context> handler = getFirstApplicableHandler(context);
      if (handler != null) {
        handler.onStart(context);
      }
    }
    
    @Nullable
    private ObservationHandler<Observation.Context> getFirstApplicableHandler(Observation.Context context) {
      for (ObservationHandler<Context> handler : this.handlers) {
        if (handler.supportsContext(context)) {
          return handler;
        }
      }
      return null;
    }

}
```

#### AllMatchingCompositeObservationHandler

보관 중인 핸들러 목록으로부터 매칭되는 모든 핸들러에게 작업을 위임하는 핸들러

아래의 onStart 메서드를 포함하여 모든 메서드가 동일한 메커니즘을 사용한다

```java
class AllMatchingCompositeObservationHandler implements CompositeObservationHandler {

    private final List<ObservationHandler<Observation.Context>> handlers;

    @Override
    public void onStart(Observation.Context context) {
        for (ObservationHandler<Context> handler : this.handlers) {
            if (handler.supportsContext(context)) {
                handler.onStart(context);
            }
        }
    }

}
```


## ObservationConvention

ObservationConvention은 Observation과 Context의 이름, 태그에 대한 기본 값을 정의할 수 있는 인터페이스로 반복적인 메타데이터 설정을 중앙화하여 코드 중복을 줄이고 일관된 KeyValue 네이밍 규칙을 적용한다

Observation의 각 생명주기에 반응하는 ObservationHandler와 달리 Observation이 시작될 때 초기 설정에 관해서만 동작한다

Observation에 ObservationConvention이 지정되면 Observation의 name, contextualName 대신 Convention 구현체에서 지정한 name과 contextualName이 사용된다 

micrometer에서 라이브러리 별/기능 별 ObservationConvention 구현체를 제공하니 잘 확인해보자 

```java
// KeyValuesConvention는 KeyValue 네이밍 컨벤션에 대한 마커 인터페이스 역할을 한다
public interface ObservationConvention<T extends Observation.Context> extends KeyValuesConvention {

    ObservationConvention<Observation.Context> EMPTY = context -> false;

    boolean supportsContext(Observation.Context context);

    default KeyValues getLowCardinalityKeyValues(T context) {
        return KeyValues.empty();
    }

    default KeyValues getHighCardinalityKeyValues(T context) {
        return KeyValues.empty();
    }

    @Nullable
    default String getName() {
        return null;
    }

    @Nullable
    default String getContextualName(T context) {
        return null;
    }

}
```


## ObservationFilter

ObservationFilter는 Observation이 맨 처음 시작된 후 메타데이터(이름, 태그 등)를 동적으로 변경하는 역할을 하는 인터페이스이다

주로 필터링 규칙을 적용하거나 특정 조건에 따라 메타데이터를 조작하는 작업을 수행한다

```java
public interface ObservationFilter {

    Observation.Context map(Observation.Context context);

}
```


## ObservationPredicate

ObservationPredicate는 Observation을 생성할 지, 계측 로직을 수행하지 않는 NoopObservation을 생성할 지 결정하는 인터페이스이다

BiPredicate 함수형 인터페이스를 상속하여 true를 반환할 시 Observation을 생성하고 false를 반환할 시 NoopObservation 인스턴스를 생성한다

첫 번째 파라미터인 String은 Observation의 name 값을 의미한다

```java
@FunctionalInterface
public interface BiPredicate<T, U> {
  boolean test(T t, U u);
}

public interface ObservationPredicate extends BiPredicate<String, Observation.Context> {

}
```


## ObservationRegistry

애플리케이션 내에 모든 Observation, ObservationHandler, ObservationFilter, ObservationPredicate 들을 등록하고 관리하는 레지스트리

전역적으로 접근 가능하며 스프링 부트에서는 의존성 주입을 받을 수 있다

주요 메서드
- create (static): SimpleObservationRegistry 인스턴스 생성
- observationConfig: handler, filter, predicate 등을 관리할 수 있는 config 객체 반환
- getCurrentObservation: 현재 Observation 반환
- getCurrentObservationScope: 현재 Observation.Scope 반환
- setCurrentObservationScope: Observation.Scope 설정


## @Observed

명시적인 계측 로직을 작성하는 대신 `@Observed` 어노테이션을 통해 aspectj의 AOP 기능을 사용할 수 있다

`@Observed` 어노테이션은 클래스에 선언하여 모든 메서드에 적용하거나 특정 메서드에 선언할 수 있다

```java
public class SimpledObservedService {

    @Observed(name = "test.call", contextualName = "test#call",
            lowCardinalityKeyValues = { "abc", "123", "test", "42" })
    public void call() {
        System.out.println("call");
    }
}
```

프록시 패턴을 사용하여 관찰 메서드를 대상으로 Observation을 생성하여 계측 작업을 수행한다

아래는 SimpledObservedService의 call 메서드에 적용된 @Observed 어노테이션의 동작을 테스트한다

```java
import io.micrometer.observation.ObservationTextPublisher;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.observation.tck.TestObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;

class SimpledObservedServiceTest {

    @Test
    void observedAnnotationTest() {
        
        // 테스트 레지스트리 생성
        TestObservationRegistry registry = TestObservationRegistry.create();
        registry.observationConfig().observationHandler(new ObservationTextPublisher());

        // 프록시 생성 및 aspect 추가
        AspectJProxyFactory pf = new AspectJProxyFactory(new SimpledObservedService());
        pf.addAspect(new ObservedAspect(registry));

        // 비즈니스 로직 호출
        SimpledObservedService service = pf.getProxy();
        service.call();

        // Observation 생성 검증
        assertThat(registry)
                .hasSingleObservationThat()
                .hasBeenStopped()
                .hasNameEqualTo("test.call")
                .hasContextualNameEqualTo("test#call")
                .hasLowCardinalityKeyValue("abc", "123")
                .hasLowCardinalityKeyValue("test", "42")
                .hasLowCardinalityKeyValue("class", SimpledObservedService.class.getName())
                .hasLowCardinalityKeyValue("method", "call").doesNotHaveError();
    }
}
```


## workflow

```text
┌─────────────────────┐ ┌───────┐ ┌───────────────────────┐ ┌──────────────────────┐
│ObservationRegistry│ │Context│ │ObservationConvention│ │ObservationPredicate│
└┬────────────────────┘ └┬──────┘ └┬──────────────────────┘ └┬─────────────────────┘
┌▽───────────────────────▽─────────▽────────────────────────▽┐
│Observation                                             │
└┬────────────────────────────────────────────────────────────┘
┌▽──────┐
│Handler│
└┬──────┘
┌▽─────────────────┐
│ObservationFilter│
└──────────────────┘
```


## gradle configuration

```kotlin
implementation(platform("io.micrometer:micrometer-bom:${micrometer-version}"))
implementation("io.micrometer:micrometer-observation")
```