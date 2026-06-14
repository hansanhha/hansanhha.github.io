---
layout: default
title:
---

#### index
- [Gauge](#gauge)
- [TimeGauge](#timegauge)
- [MultiGauge](#multigauge)


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

