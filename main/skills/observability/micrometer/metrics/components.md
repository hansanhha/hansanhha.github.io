---
layout: default
title:
---

#### index
counters
- [Counter](#counter)
- [@Counted](#counted)
- [FunctionCounter](#functioncounter)

gauges
- [Gauge](#gauge)
- [TimeGauge](#timegauge)
- [MultiGauge](#multigauge)

timers
- [Timer](#timer)
- [@Timed](#timed)
- [@MeterTag](#metertag)
- [FunctionTimer](#functiontimer)
- [pause detection](#pause-detection)

distribution summaries
- [Distribution Summary](#distribution-summary)
- [scaling and histograms](#scaling-and-histograms)

long task timers

histogram 

percentiles


## Counter

counter는 단순한 카운트 값을 수집하는 메트릭으로, 마이크로미터에서 제공하는 Counter 인터페이스을 사용하면 고정된 양(양수)만큼 값을 증가시킬 수 있다

`Counter` 인터페이스는 주로 이벤트 발생 횟수를 추적하는 데 사용하며 increment 메서드로 증가만 할 수 있는 특징을 가지며 누적 통계를 필요로 할 때 사용한다

```java
public class OrderCounter {

    private final Counter orderCounter;

    /*
         shallowOrder.created.count 라는 이름으로 카운터 정의 (마이크로미터 네이밍 컨벤션: 소문자, '.'으로 단어 구별)
         type=online 태그 설정
     */
    public OrderCounter(MeterRegistry registry) {
        orderCounter = Counter.builder("shallowOrder.created.count")
                .description("total number of orders created")
                .tags("type", "online")
                .register(registry);
    }

    public void createOrder() {
        System.out.println("created shallowOrder");

        orderCounter.increment();
    }
}
```


## @Counted

`@Counted` 어노테이션은 aspectj aop 기반으로 메서드 호출 횟수를 자동으로 카운팅한다

참고로 프록시 패턴을 사용하기 때문에 프록시 객체 내부에서 자기 자신을 호출하면 카운팅되지 않는다

```java
public class PaymentCounter {

    /*
         aspectj aop를 기반으로 동작하는 @Counted 어노테이션 사용
         메서드 호출 횟수를 자동으로 카운팅하며, aop 방식으로 동작하기 때문에 프록시 객체 내부에서 호출하면 카운팅되지 않는다
     */
    @Counted(value = "payment.success.count", description = "number of successful payments")
    public void processPayment() {
        System.out.println("payment proceed successfully");
    }
}
```

### @MeterTag

@Counted나 @Timed 등과 함께 사용하는 어노테이션으로 메서드 호출 시점에 파라미터 값을 메트릭 태그로 활용한다

```java
public class PaymentCounter {
    /*
        @MeterTag 어노테이션은 어노테이션에 지정한 키와 파라미터의 값의 쌍이 메트릭 태그에 추가한다
    */
    @Counted(value = "payment.failure.count", description = "number of failure payments")
    public void failPayment(@MeterTag(key = "cause") String failureCause) {
        System.out.println("payment failed (cause: " + failureCause + ")");
    }
}
```


## FunctionCounter

`FunctionCounter`는 현재 상태를 기반으로 동적으로 값을 계산하는 인터페이스로 값을 누적하지 않고 현재 상태를 나타낸다

현재 메모리 사용량이나 작업 큐의 크기처럼 변동하는 값을 카운팅할 때 사용한다

```java
public class TaskQueue {

    private final AtomicInteger queueSize = new AtomicInteger(0);

    // FunctionCounter는 값을 누적하지 않고 현재 상태를 기반으로 동적으로 값을 계산한다
    public TaskQueue(MeterRegistry registry) {
        FunctionCounter.builder("task.queue.size", queueSize, AtomicInteger::get)
                .description("current size of the task queue")
                .register(registry);
    }

    public void addTask() {
        queueSize.incrementAndGet();
        System.out.println("task added to the queue");
    }

    public void completeTask() {
        queueSize.decrementAndGet();
        System.out.println("task completed");
    }

}
```


## Gauge

Guage는 변동하는 값을 실시간으로 측정하는 메트릭 타입으로 현재 상태를 나타내는 값을 모니터링할 때 사용한다

최대 상한선(upper bound)이 정해져 있는 값을 모니터링하기 유용하다 - e.g) 현재 실행 중 상태인 스레드 수, 큐에 남은 작업 수, 메모리 사용량 등

특징
- 누적 X, 즉시 측정: 메모리 사용량을 모니터링할 때 Guage는 현재 시점의 사용량만 기록한다
- 콜백 함수 사용: 값을 측정할 때 콜백을 사용하여 현재 상태를 가져온다
- 음수 허용: Counter와 달리 음수 값을 허용한다
- lazy evaluation: 실제로 모니터링 시스템이 값을 조회할 때 값을 평가한다

micrometer의 모든 형태의 Gauge는 관찰하는 객체에 대해 약한 참고(weak reference)를 유지하기에 가비지 컬렉션을 방해하지 않는다

MeterRegistry는 number, function, collection, map 타입에 대한 Gauge를 생성하는 메서드를 포함한다

```java
public class BasicGauge {

    private final MeterRegistry registry;

    public BasicGauge(MeterRegistry registry) {
        this.registry = registry;
    }

    // Number 타입에 대한 Gauge 생성
    public void numberGauge() {
        AtomicInteger state = new AtomicInteger(0);
        registry.gauge("numberGauge", state);

        state.decrementAndGet();
        state.decrementAndGet();
        state.decrementAndGet();
    }

    // Collection 타입에 대한 Gauge 생성
    public void listGauge() {
        List<String> state = new ArrayList<>();
        registry.gauge("listGauge", Tags.empty(), state, List::size);

        state.add("hello");
        state.add("list gauge");
    }

    // Map 타입에 대한 Gauge 생성
    public void mapGauge() {
        HashMap<String, Integer> state = new HashMap<>();

        registry.gaugeMapSize("mapGauge", Tags.empty(), state);

        state.put("hello", 1);
        state.put("map gauge", 2);
    }

    // Gauge builder 이용
    public void gaugeBuilder() {
        ArrayList<Integer> state = new ArrayList<>();
        ToDoubleFunction<ArrayList<Integer>> callback = List::size;

        Gauge gauge = Gauge
                .builder("gauge", state, callback)
                .description("gauge description")
                .tag("type", "test gauge")
                .register(registry);

        for (int i = 0; i < 10; i++) {
            state.add(1);
        }
    }
}
```


## TimeGauge

TimeGauge는 시간(duration) 값을 실시간으로 모니터링할 때 사용하는 Gauge의 특수한 형태로 시간 단위(TimeUnit)를 지정하여 시간 값을 추적한다

모니터링 시스템(prometheus, datadog 등)에서 시간 단위 그래프를 시각화할 수 있다

```java
public class SpecialGauge {

    private final MeterRegistry registry;

    public SpecialGauge(MeterRegistry registry) {
        this.registry = registry;
    }

    public void timeGauge() {
        AtomicInteger msTimeGauge = new AtomicInteger(4000);

        // 특정 시간 단위(TimeUnit)을 지정하여 TimeGauge 생성
        TimeGauge timeGauge = TimeGauge
                .builder("my.time.gauge", msTimeGauge, TimeUnit.MILLISECONDS, AtomicInteger::get)
                .register(registry);
    }
}
```


## MultiGauge

MultiGauge는 여러 개의 Gauge의 값을 한 번에 등록하고 업데이트할 수 있는 Gauge의 확장 형태이다

각 Gauge에 태그를 붙여 각각의 값을 구분하고 여러 Gauge를 한 번의 호출로 등록 및 갱신한다

```java
public class SpecialGauge {

    private final MeterRegistry registry;

    public SpecialGauge(MeterRegistry registry) {
        this.registry = registry;
    }

    public void multiGauge() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        // MultiGauge 생성
        MultiGauge multiGauge = MultiGauge.builder("server.memory.usage")
                .description("memory usage by category")
                .register(registry);

        /*
            MultiGauge에 개별 Gauge 등록
         */
        ArrayList<MultiGauge.Row<?>> rows = new ArrayList<>();

        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        rows.add(MultiGauge.Row.of(Tags.of("type", "heap"), heapMemoryUsage.getUsed()));

        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        rows.add(MultiGauge.Row.of(Tags.of("type", "non_heap"), nonHeapMemoryUsage.getUsed()));

        multiGauge.register(rows, true);
    }
}
```


## Timer

Timer는 애플리케이션의 작업이나 요청에 걸리는 시간을 측정하고 그 작업의 수행 빈도(호출 횟수)와 함께 분포 정보를 수집하는 메트릭 타입이다

평균, 최소, 최대, 백분위(percentile) 값 등을 파악할 수 있어 성능 분석과 병목 지점을 확인하는 데 유용하다

#### 시간 측정

Timer는 작업(요청)이 시작되어 끝날 때까지의 경과 시간을 측정한다

모니터링 시스템 별 또는 도출하고자 하는 값의 종류에 따라 필요한 시간 단위가 다를 수 있기 때문에 micrometer는 특정 시간 단위를 제안하지 않는다

따라서 시간 측정 시 필요에 따른 시간 단위를 지정해야 한다

#### 호출 횟수(count)와 총 소요 시간(total time)

Timer는 작업이 몇 번 실행되었는지와 모든 작업의 총 소요 시간을 동시에 기록한다

이 두 값으로 평균 응답 시간(total time % count)을 계산할 수 있다

#### 분포 정보

Timer는 추가적으로 히스토그램이나 백분위(percentile)같은 통계치를 기록할 수 있다

이를 통해 작업의 지연 분포를 시각화하고 작업이 얼마나 빠르게 처리되는지, 지연이 발생하는지 파악할 수 있다

### registry.timer, Timer.builder

아래와 같이 MeterRegistry의 timer 메서드 또는 Timer.builder를 통해 Timer 인스턴스를 생성할 수 있다

```java
registry.timer("timer.sample", "type", "account")
```

```java
timer = Timer.builder("timer.record")
                .description("time taken for a example task")
                .register(registry);
```

### timer.record(), Timer.Sample

Timer 인스턴스를 활용해서 값을 측정하는 방법은 record 인스턴스 메서드 활용하는 방법과 Timer.Sample을 이용하는 방법이 있다

record 메서드는 `Runnable` 또는 `Supplier<T>`를 파라미터로 받으며, record 메서드의 호출 횟수와 코드를 실행하는 데 걸린 시간, 가장 오래 걸린 작업의 시간을 측정한다

```java
public class RecordTimer {

    private final Timer timer;

    public RecordTimer(MeterRegistry registry) {
        timer = Timer.builder("timer.record")
                .description("time taken for a example task")
                .register(registry);
    }

    // timer.record(Runnable) 사용
    public void executeRunnableTask() {
        timer.record(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    // timer.record(Supplier<T>) 사용
    public String executeSupplierTask() {
        return timer.record(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return "completed";
        });
    }

}
```

Timer.Sample을 이용하면 작업의 시작과 종료 시점을 명시적으로 제어하여 값을 측정할 수 있다

```java
public class SampleTimer {

    private final MeterRegistry registry;
    private final Timer timer;
    private Timer.Sample sample;

    // Timer.Sample을 사용하면 작업의 시작과 종료 시점을 명시적으로 제어하여 측정할 수 있다
    public SampleTimer(MeterRegistry registry) {
        this.registry = registry;

        timer = Timer.builder("timer.sample")
                .description("time taken for a example task")
                .register(registry);
    }

    // sample의 시작 시간은 registry의 Clock을 기반으로 기록한다
    public void startTimer() {
        sample = Timer.start(registry);
    }

    public void doSomething() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopTimer() {
        sample.stop(timer);
    }
}
```


## @Timed

@Timed 어노테이션은 메서드의 실행 시간을 자동으로 측정하여 Timer 메트릭을 기록하는 데 사용된다

메서드 실행 전후로 자동으로 타이머를 시작하고 정지하여 실행 시간을 측정한다

기록된 데이터는 MeterRegistry에 누적되어 외부 모니터링 시스템에게 전달될 수 있으며 메서드 호출 빈도, 총 실행 시간, 평균 실행 시간, 백분위 등 다양한 성능 지표를 모니터링할 수 있다

```java
public class TimedAOP {

    @Timed(value = "timer.timed", description = "time taken for a example task")
    public void executeTask() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

longTask 속성을 활성화하면 Timer 대신 Long Task Timer로 처리한다

```java
public class TimedAOP {

    @Timed(value = "timer.timed.long", longTask = true, description = "long running task execution time")
    public void executeLongTask() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### @MeterTag

`@Counted` 어노테이션과 마찬가지로 `@Timed` 어노테이션을 사용할 때 메서드 파라미터에 `@MeterTag`를 적용하여 파라미터 값을 태그에 추가할 수 있다

```java
public class TimedAOP {

    @Timed(value = "timer.timed.metertag")
    public void executeTask(@MeterTag(key = "name") String name) {
    }
}
```


## FunctionTimer

FunctionTimer (function-tracking timer)는 애플리케이션 내에서 발생한 이벤트를 기록하기 위해 매번 record()를 호출하는 대신, 이미 누적된 값(총 실행 횟수와 총 실행 시간)을 직접 제공하는 객체의 상태를 기반으로 타이머 값을 계산한다

값을 제공하는 객체는 아래의 두 개의 메서드를 활용하여 평균 실행 시간 및 초당 이벤트 발생률을 계산한다

상태 객체의 필수 구성 메서드
- count function: 객체에서 현재까지의 이벤트 총 개수를 반환하는 함수
- total time function: 객체에서 이벤트들의 총 소요 시간을 반환하는 함수

용도
- 이미 내부적으로 통계를 집계하고 있는 객체(외부 라이브러리의 통계, 비동기 작업 집계기 등)의 상태를 메트릭으로 노출하고 싶을 때
- 애플리케이션 코드에서 직접 이벤트를 기록하지 않고 현재 상태를 기반으로 타이머 값을 제공할 때

특징
- 자동 계산: 내부적으로 제공된 count와 total time 함수를 기반으로 평균 실행 시간과 초당 처리율을 계산한다
- 데이터 소스: 직접 이벤트를 기록하지 않고 이미 집계된 값을 함수 호출을 통해 얻는다
- 낮은 오버헤드: 이벤트마다 record() 호출 없이 단순히 현재 상태를 조회하기 때문에 애플리케이션 코드에 부담을 주지않는다

다만 FunctionTimer는 참조한 객체의 상태를 기반으로 값을 계산하므로 해당 객체가 GC 대상이 되지 않도록 주의해야 한다

```java
public class FunctionTimers {

    private final OperationStats stats;

    /*
        FunctionTimer는 빌더를 이용하여 생성할 수 있다
        첫 번째 인자: 메트릭 이름
        두 번째 인자: 통계 값 제공 객체
        세 번째 인자: count function
        네 번째 인자: total time function
        다섯 번째 인자: 시간 단위
     */
    public FunctionTimers(MeterRegistry registry) {
        stats = new OperationStats();

        FunctionTimer.builder("timer.function", stats, OperationStats::getCount, OperationStats::getTotalTime, TimeUnit.NANOSECONDS)
                .description("a function-tracking timer that measures operation durations")
                .register(registry);
    }

    public void executeTask() {
        stats.record(10);
    }

    public void executeOther() {
        stats.record(100);
    }

    // 상태 객체는 내부적으로 작업 횟수(count)와 작업에 걸린 총 시간(totalTimeInNanos)을 업데이트한다
    private static class OperationStats {
        private long count;
        private long totalTimeInNanos;

        public void record(long durationInNanos) {
            count++;
            totalTimeInNanos += durationInNanos;
        }

        public long getCount() {
            return count;
        }

        public double getTotalTime() {
            return totalTimeInNanos;
        }
    }
}
```


## pause detection

마이크로미터의 Timer는 애플리케이션의 응답 시간이나 작업 수행 시간 등 지연(latency)을 측정하는 데 사용되는데, 실제 시스템에서는 jvm이나 운영체제의 일시적인 정지(gc, 스케줄러 지연 등)로 인해 실제 응답 시간이 더 길어졌음에도 불구하고 측정된 지연 값이 낮게 나타날 수 있다

이를 coordinated omission 문제라고 하는데, 마이크로미터는 이러한 문제를 보정하기 위해 pause detection 기능을 제공한다

이 기능은 LaytencyUtils 패키지를 사용하여 시스템 정지로 인해 발생하는 추가 지연을 보정하여 백분위, slo(서비스 수준 목표) 계산 등 분포 통계에 반영된다

마이크로미터에서 제공하는 두 개의 pause detector 구현체 중 필요에 따라 선택하여 기능을 사용할 수 있다

### clock-drift based detector

마이크로미터 1.0.10/1.1.4/1.2.0 이전 버전에 기본적으로 사용되는 구현체로 시스템의 시계(clock)와 실제 경과 시간 사이의 차이(drift)를 감지하여 시스템 정지로 인한 지연을 보장한다

또한 다음 두 가지 옵션을 설정할 수 있다
- sleepInterval: 주기적으로 sleep하며 시스템 상태를 점검하는 간격
- pauseThreshold: 이 간격 이상으로 멈춘 것으로 간주하는 임계치

공식 문서에서는 두 값 모두 100ms 정도로 설정하면 긴 pause 이벤트를 감지하면서도 cpu 사용량은 최소화할 수 있다고 한다

### no-op detector

마이크로미터 1.0.10/1.14/1.2.0 이후 버전에서 기본적으로 사용되는 구현체로 pause detection 기능을 적용하지 않는다

### configuration

MeterRegistry의 config에서 아래와 같이 pause detector 구현체를 지정할 수 있다

```java
// clock-drift detector 사용
registry.config().pauseDetector(new ClockDriftPauseDetector(sleepInterval, pauseThreshold));

// no-op detector 사용
registry.config().pauseDetector(new NoPauseDetector());
```


## Distribution Summary

Distribution Summary는 여러 개의 이벤트에서 발생한 값들의 분포(distribution)를 측정하고 집계하는 데 사용되는 메트릭 타입이다

주로 작업의 크기, 금액, 바이트 수 등과 같이 단순한 횟수나 시간과는 다른 "양"을 기록할 때 유용하다

#### 실시간 누적 측정

이벤트가 발생할 때마다 실시간으로 해당 이벤트의 "값"을 누적한다

e.g) http 요청의 응답 바이트 수, 거래 금액, 파일 전송 크기 등

#### 카운트 및 총합

이벤트의 총 개수(count)와 모든 이벤트의 합계(total amount)를 집계한다

#### 평균값과 분포 통계

count와 total amount값을 통해 평균값을 계산할 수 있으며 백분위(percentile)나 히스토그램같은 추가 통계를 구성할 수 있다


### DistributionSummary.builder

DistributionSummary 인스턴스는 아래와 같이 빌더를 이용하여 생성할 수 있다

```java
public class ResponseSizeTracker {

    private final DistributionSummary responseSizeSummary;

    public ResponseSizeTracker(MeterRegistry registry) {
        responseSizeSummary = DistributionSummary.builder("distribution.summary")
                .description("distribution of http response sizes")
                .baseUnit("bytes")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    public void recordResponseSize(long byteSize) {
        responseSizeSummary.record(byteSize);
    }
}
```


## scaling and histograms

Distribution Summary는 측정한 값들의 분포를 기록하기 위해 여러 히스토그램 버킷을 사용한다

기본적으로 마이크로미터는 미리 선택된 백분위 히스토그램 버킷을 1부터 `Long.MAX_VALUE` 까지의 정수 값들로 구성한다

만약 측정 대상의 값 범위가 작다면 기본 버킷 범위는 너무 넓어서 의미 있는 분포를 얻기 어렵다

scale 메서드를 통해 미리 버킷의 범위를 조정하여 이러한 문제를 해결할 수 있다

```java
public class ScalingBucketCardinality {

    private final DistributionSummary weightSummary;

    /*
        기본 버킷 범위: 1 ~ Long.MAX_VALUE 
        scale 메서드를 통해 기본 버킷 범위를 조정할 수 있다
        serviceLevelObjectives 메서드로 slo 경계를 설정하여 그 경계에 도달하면 비율을 모니터링할 수 있다
     */
    public ScalingBucketCardinality(MeterRegistry registry) {
        weightSummary = DistributionSummary.builder("distribution.summary.scale")
                .scale(100)
                .baseUnit("kg")
                .serviceLevelObjectives(70, 80, 90)
                .register(registry);
    }

    public void recordWeight(int weight) {
        weightSummary.record(weight);
    }

}
```







