---
layout: default
title:
---

#### index
- [annotations](#annotations)
- [Tracer](#tracer)
- [TraceContext](#tracecontext)
- [CurrentTraceContext](#currenttracecontext)
- [Span](#span)
- [Handler](#handler)
- [Baggage](#baggage)


## annotations

### @NewSpan

진행 중인 trace가 있다면 기존 span의 자식 span을 만들고, 없다면 새로운 span을 만드는 어노테이션

public 메서드에만 선언할 수 있으며 메서드 파라미터에 [@SpanTag](#spantag) 어노테이션을 적용해서 파라미터 값을 span의 태그로 추가할 수 있다 

```java
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface NewSpan {

    String name() default "";
    String value() default "";
}
```

### @ContinueSpan

새로운 span을 생성하지 않고 기존 span을 이어가는 어노테이션

```java
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface ContinueSpan {
    String log() default "";
}
```


### @SpanTag

메서드 파라미터를 span 태그로 추가하는 어노테이션

```java
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.PARAMETER)
public @interface SpanTag {
    String value() default "";
    String key() default "";
    String expression() default "";
    Class<? extends ValueResolver> resolver() default NoOpValueResolver.class;
}
```


## Tracer

trace 또는 span을 생성, 시작, 활성화하거나 관리(builder, customizer) 객체를 반환하고 현재 활성화된 span을 조회하는 api  

```java
public interface Tracer extends BaggageManager {
    
    // Tracer NOOP ... 
    
    
    /* ----------  trace 및 span 생성 ---------- */
    
    // scope에 있는 현재 span을 기반으로 자식 span 생성
    // span이 없는 경우 새로운 trace를 생성한다
    Span nextSpan();

    // 주어진 parent span에 대한 자식 span 생성
    // parent가 null인 경우 새로운 trace를 생성한다
    Span nextSpan(@Nullable Span parent);

    // 주어진 span을 활성 span(current span)으로 설정한다
    // SpanInScope 객체를 반환하는데, 이는 현재 span의 scope를 나타낸다
    // SpanInScope는 자동으로 scope를 닫기 위해 AutoCloseable을 구현하고 있으므로 try-with-resources 패턴으로 사용하면 메모리 누수를 방지할 수 있다
    Tracer.SpanInScope withSpan(@Nullable Span span);

    // 활성화된 span이 있으면 (currentSpan 메서드에서 null을 반환하지 않으면) 자식 span을 생성 및 시작하고
    // 없으면 새 trace를 생성한 뒤 span을 생성 및 시작한다
    // 반환 값인 ScopedSpan은 end 메서드를 호출하기 전까지 활성화된 상태를 유지한다
    ScopedSpan startScopeSpan(String name);

    
    /* ----------  활성화된 current span 확인  ---------- */
    
    // scope에 있는 활성화된 current span 반환
    // 없는 경우 null 반환
    @Nullable
    Span currentSpan();
    
    
    /* ----------  span 및 trace 관리 객체 반환 ---------- */
    
    Span.Builder spanBuilder();

    TraceContext.Builder traceContextBuilder();

    CurrentTraceContext currentTraceContext();

    @Nullable
    SpanCustomizer currentSpanCustomizer();

    
    /* ----------- span의 scope 지정 (autocloseable) ----------- */
    
    interface SpanInScope extends Closeable {
        
        // scope가 닫힐 때 리소스 정리(MDC 정리 등)
        @Override
        void close();

    }
}
```


## TraceContext

trace와 span 데이터를 보관하는 기능을 추상화한 인터페이스

```java
public interface TraceContext {

    // TraceContext NOOP ...
    // Builder ...
    
    // span의 trace id 반환
    String traceId();
    
    // 부모 span id 반환 (없는 경우 null 반환)
    // span은 트리 구조를 가질 수 있다
    @Nullable
    String parentId();
    
    // 현재 span id 반환
    String spanId();
    
    // 해당 데이터가 수집될 지 여부를 반환한다
    // true: 수집됨, false: 수집 안됨, null: 아직 결정 안됨
    // false인 경우 로컬에서 span을 생성하지만 exporter를 통해 외부로 전달되지 않는다
    Boolean sampled();
    
}
```


## CurrentTraceContext

주어진 span을 scope의 활성 span으로 만드는 api

```java
public interface CurrentTraceContext {

    
    /* --------- 활성화된 context 조회 --------- */
    
    // 현재 trace context를 반환하거나 없는 경우 null 반환
    @Nullable
    TraceContext context();

    
    /* --------- span 활성화 --------- */
    
    // 주어진 context를 scope에 활성 span으로 설정한다 (반환 값이 닫히기 전까지 활성화됨)
    // null을 주면 scope를 비워둔다
    // CurrentTraceContext.Scope는 autocloseable을 구현하고 있으므로 try-catch-resources 구문 사용 권장 
    CurrentTraceContext.Scope newScope(@Nullable TraceContext context);

    // 주어진 context가 이미 scope에 있다면 noop scope 인스턴스를 반환한다
    // 나머진 newScope와 동일하게 동작한다
    CurrentTraceContext.Scope maybeScope(@Nullable TraceContext context);

    
    /* --------- trace에 포함될 작업을 지정하는 api (비동기 작업 포함) --------- */
    
    <C> Callable<C> wrap(Callable<C> task);
    Runnable wrap(Runnable task);
    Executor wrap(Executor delegate);
    ExecutorService wrap(ExecutorService delegate);

    
    /* ----------- span의 scope 지정 (autocloseable) ----------- */
    
    interface Scope extends Closeable {
        
        Scope NOOP = () -> {};

        // scope가 닫힐 때 리소스 정리(MDC 정리 등)
        @Override
        void close();

    }
}
```


## Span

해당 서비스에서 수행하는 작업 단위에 대해 추상화를 한 객체로, 유연성과 확장성을 위해 역할 별로 책임 분리를 해놓았다

### Span

span은 하나의 작업 단위를 나타내며 시작과 종료를 할 수 있으며 시간 정보(timing information)과 이벤트 및 태그를 포함한다

동적으로 이름과 이벤트 및 태그를 커스터마이징할 수 있는 SpanCustomizer 인터페이스를 확장한다

```java
public interface Span extends io.micrometer.tracing.SpanCustomizer {
    
    // Span NOOP ...
    // Builder ...
    
    // 기록되지 않고 아무것도 보고하지 않는 span이지만 외부 요청에 포함되는 경우 true 반환
    boolean isNoop();
    
    // 이 span 인스턴스에 대한 context 반환
    TraceContext context();
    
    // span 시작
    Span start();
    
    // span 이름 지정
    Span name(String name);
    
    // span 이벤트 지정
    Span event(String value);
    Span event(String value, long time, TimeUnit timeUnit);
    
    // span 태그 지정
    Span tag(String key, String value);

    // span 에러 기록
    Span error(Throwable throwable);
    
    // span 종료, noop이 아니라면 span을 멈추고 기록한다
    void end();
    void end(long time, TimeUnit timeUnit);
    
    // span을 종료하지만 기록하지 않는다
    void abandon();

    // 원격 정보 설정
    Span remoteServiceName(String remoteServiceName);
    Span remoteIpAndPort(String ip, int port);
    
    // span 타입 설정
    // span 간의 부모-자식 관계 외에도 추가 관계를 지정하는 데 사용될 수 있다
    // opentelemetry 참고
    enum Kind {
        
        // rpc 또는 기타 원격 요청을 서버에서 처리하는 span
        SERVER,
        
        // rpc 또는 기타 원격 요청으로 감싼 클라이언트의 span
        CLIENT,
        
        // broker에 메시지를 송신하는 producer span
        // producer와 consumer span은 직접적으로 중요한 관계를 가지지 않는다 
        PRODUCER,
        
        // broker로부터 메시지를 수신하는 consumer span
        // producer와 consumer span은 직접적으로 중요한 관계를 가지지 않는다
        CONSUMER
    }
}
```

### SpanCustomizer

scope에 감싸진 현재 span에 대한 커스터마이징 기능을 제공하는 api

```java
public interface SpanCustomizer {
    
    // SpanCustomizer NOOP ...

    // span 이름 커스터마이징
    SpanCustomizer name(String name);

    // span 태그 커스터마이징
    SpanCustomizer tag(String key, String value);

    // span 이벤트 커스터마이징
    SpanCustomizer event(String value);
}
```

### SpanNamer

span 이름을 자동으로 생성하는 api

```java
public interface SpanNamer {

    // 생성할 span 이름과 관련된 객체와 기본 값을 매개변수로 받는다 
    String name(Object object, String defaultValue);
}
```

### ScopedSpan

span.start 메서드를 호출한 후, end 메서드를 호출하기 전까지 현재 span을 나타내는 api

```java
public interface ScopedSpan {
    
    // ScopedSpan NOOP ...

    // 기록되지 않고 아무것도 보고하지 않는 span이지만 외부 요청에 포함되는 경우 true 반환
    boolean isNoop();

    // 이 span 인스턴스에 대한 context 반환
    TraceContext context();

    // span 이름 지정
    Span name(String name);

    // span 태그 지정
    ScopedSpan tag(String key, String value);

    // span 이벤트 지정
    ScopedSpan event(String value);

    // span 에러 지정
    ScopedSpan error(Throwable throwable);

    // span 종료, noop이 아니라면 span을 멈추고 기록한다
    void end();
}
```

### ThreadLocalSpan

스레드 로컬에 저장된 span을 나타내는 클래스

```java
public class ThreadLocalSpan {

    private final ThreadLocal<ArrayDeque<SpanAndScope>> currentSpanInScopeStack = new ThreadLocal<>();
    
    private final Tracer tracer;

    public ThreadLocalSpan(Tracer tracer) {
        this.tracer = tracer;
    }

    // 스레드 로컬에 span, scope 추가
    public void set(Span span) {
        Tracer.SpanInScope spanInScope = this.tracer.withSpan(span);
        SpanAndScope newSpanAndScope = new SpanAndScope(span, spanInScope);
        getCurrentSpanInScopeStack().addFirst(newSpanAndScope);
    }

    // 현재 span 및 scope 반환
    public SpanAndScope get() {
        return getCurrentSpanInScopeStack().peekFirst();
    }

    // 스레드 로컬에 있는 현재 span 삭제
    // 아무것도 없으면 null을 반환하고, span의 scope가 열려있는 경우 닫아준다
    public SpanAndScope remove() {
        SpanAndScope spanAndScope = getCurrentSpanInScopeStack().pollFirst();
        if (spanAndScope == null) {
            return null;
        }
        if (spanAndScope.getScope() != null) {
            spanAndScope.getScope().close();
        }
        return spanAndScope;
    }
}
```

### SpanAndScope

span과 Tracer.SpanInScope를 포함한 클래스

```java
public class SpanAndScope implements Closeable {

    private final Span span;

    private final Tracer.SpanInScope scope;

    public SpanAndScope(Span span, @Nullable Tracer.SpanInScope scope) {
        this.span = span;
        this.scope = scope;
    }
    
    // getter

    // scope를 닫고 span을 종료한다
    @Override
    public void close() {
        if (this.scope != null) {
            this.scope.close();
        }
        this.span.end();
    }
}
```


## Handler

micrometer tracing에서 micrometer observation의 ObservationHandler의 기능을 사용할 수 있도록 확장 인터페이스와 그 구현체를 제공한다

인터페이스: TracingObservationHandler

구현체: DefaultTracingObservationHandler, TracingAwareMeterObservationHandler 등


## Baggage

Baggage는 분산 추적(distributed tracing)에서 요청 간에 전달되는 key-value 형태의 메타데이터로 [TraceContext](#tracecontext)와 함께 전파되어 서비스 간의 추가적인 데이터를 공유할 수 있게 해준다

사용자 정보 전달(userId, tenantId 등), 요청 우선순위(priority, debug 플래그), A/B 테스트 그룹 정보, 트랜잭션 데이터 전달 등의 용도로 사용될 수 있다

tracing 구현체에 따라 Baggage를 scope로 감싸야 하거나 불변 객체로 만든다 (opentelemetry)

```java
public interface Baggage extends BaggageView {
    
    // Baggage NOOP ...

    // baggage value 설정 - makeCurrent(String) 사용 권장
    @Deprecated
    Baggage set(String value);

    // 주어진 traceContext에 대한 baggage value 설정 - makeCurrent(TraceContext, String) 사용 권장 
    @Deprecated
    Baggage set(TraceContext traceContext, String value);
    
    // scope의 활성 baggage로 설정하고 BaggageInScope 인스턴스를 반환한다
    BaggageInScope makeCurrent();

    // scope의 활성 baggage로 설정하고 값을 설정
    default BaggageInScope makeCurrent(String value) {
        return set(value).makeCurrent();
    }

    // 주어진 traceContext에 대한 baggage를 활성화하고 값을 설정
    default BaggageInScope makeCurrent(TraceContext traceContext, String value) {
        return set(traceContext, value).makeCurrent();
    }
}
```

### BaggageView

Baggage에 대한 정보를 제공하는 인터페이스

```java
public interface BaggageView {
    
    // BaggageView NOOP ...

    // baggage 이름 반환
    String name();
    
    // baggage 값 반환
    @Nullable
    String get();
    
    // 주어진 TraceContext에 대한 baggage 값 반환 
    @Nullable
    String get(TraceContext traceContext);
}
```

### BaggageInScope

일부 tracer는 baggage를 감싸는 scope를 필요로 한다

활성화된 baggage의 작업이 종료되면 scope를 자동으로 닫을 수 있게 BaggageInScope 추상화를 제공한다  

```java
public interface BaggageInScope extends BaggageView, Closeable {
    
    // BaggageInScope NOOP ...

    @Override
    void close();
}
```


### BaggageManager

Baggage를 관리하는 객체로 scope에 있는 baggage를 되찾거나 새로 추가하는 기능을 제공한다


```java
public interface BaggageManager {
    
    // BaggageManager NOOP ...
    
    // 모든 baggage 엔트리 반환
    Map<String, String> getAllBaggage();

    // 주어진 이름을 가진 baggage 조회 
    @Nullable
    Baggage getBaggage(String name);

    // 주어진 traceContext와 name에 대한 baggage 조회
    @Nullable
    Baggage getBaggage(TraceContext traceContext, String name);

    // 주어진 이름에 대한 새로운 baggage 생성 또는 기존 baggage 반환
    // createBaggageInScope(String, String) 사용 권장
    @Deprecated
    Baggage createBaggage(String name);

    // 주어진 이름과 값에 대한 새로운 baggage 생성 또는 기존 baggage 반환
    // createBaggageInScope(String, String) 사용 권장
    @Deprecated
    Baggage createBaggage(String name, String value);

    // 주어진 이름과 값을 가진 새로운 baggage를 생성하고 활성화시킨다
    default BaggageInScope createBaggageInScope(String name, String value) {
        return createBaggage(name).makeCurrent(value);
    }

    // 주어진 traceContext, 이름과 값을 가진 새로운 baggage를 생성하고 활성화시킨다
    default BaggageInScope createBaggageInScope(TraceContext traceContext, String name, String value) {
        return createBaggage(name).makeCurrent(traceContext, value);
    }

    // baggage 필드의 모든 이름 반환
    default List<String> getBaggageFields() {
        return Collections.emptyList();
    }
}
```



