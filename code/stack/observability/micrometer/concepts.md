#### index
- [micrometer](#micrometer)
- [three important characteristics of monitoring system](#three-important-characteristics-of-monitoring-system)
- [meters](#meters)
- [registry](#registry)
- [meter filters](#meter-filters)
- [rate aggregation](#rate-aggregation)


## micrometer

마이크로미터는 jvm 기반 애플리케이션의 metrics 계측 라이브러리다 

자바 로깅 api를 추상화한 slf4j와 비슷하게 다양한 모니터링 시스템에 대한 특정 계측 클라이언트에 종속되지 않는 facade를 제공하여 특정 벤더에 락인되지 않게 한다 

마이크로미터에 의해 기록된 데이터는 애플리케이션의 현재/최근 작동 상태를 관찰(observe)하고 경고(alert)하고 이에 대응(react)하는 데 사용된다

마이크로미터 1.10 부터 observation api와 plugin(micrometer tracing 등)이 제공된다


## three important characteristics of monitoring system

### dimensionality (차원성)

dimensionality는 메트릭 데이터를 다양한 차원(dimensions)으로 나누어 분석하고 관찰할 수 있는 능력을 말한다

메트릭 데이터를 단순히 수치만 기록하는 것이 아닌 **라벨 또는 태그**를 통해 세부적으로 구분하여 저장한다

e.g) http 요청 수를 기록할 때 단순히 요청 수만 기록하는 대신 status_code, http_method, endpoint 등의 차원으로 나누어 기록한다

```text
http_requests_total{status_code="200", http_method="GET", endpoint="/api/products"}
http_requests_total{status_code="500", http_method="POST", endpoint="/api/orders"}
```

태그나 라벨을 통해 다양한 조건으로 필터링하여 세밀한 분석이 가능하지만 너무 많은 차원을 사용하면 라벨 폭발(label cardinality explosion)이 발생하여 저장소 및 쿼리 성능에 영향을 미칠 수 있다

모니터링 시스템 종류에 따라 dimentional하지 않고 **hierarchical**하게 데이터를 관리할 수 있다

데이터를 계층적인 트리 구조로 저장하며 각 노드가 디렉토리 경로처럼 고정된 경로를 가진다

```text
servers.prod.us-east-1.web01.cpu.load
servers.prod.us-east-1.web01.cpu.idle
servers.prod.us-east-1.web02.cpu.load
```

dimensional 모니터링 시스템: prometheus, influxdb, new relic 등

hierarchical 모니터링 시스템: graphite, jmx 등

### rate aggregation (비율 집계)

rate aggregation은 시간의 경과에 따른 메트릭 데이터의 변화율을 집계하여 분석하는 기법이다

초당 http 요청 수 같이 단순한 누적 수치보다 시간 단위당 변화량을 계산한다

트렌드(요청 수의 증가 또는 감소 추세)를 분석할 수 있고, 갑작스러운 트래픽 폭주나 감소(서버 장애)를 실시간으로 감지할 수 있다

counter 타입 메트릭에 주로 사용되고 값이 초기화되는 경우를 고려해야 한다

### publishing (발행)

publishing은 모니터링 시스템에 메트릭 데이터를 전송하는 과정을 의미한다

애플리케이션 또는 시스템에서 수집한 메트릭을 prometheus, influxdb, graphite 등과 같은 모니터링 시스템에 발행한다

발행 방식은 push와 pull 방식으로 구분된다

push
- 애플리케이션이 메트릭 데이터를 모니터링 시스템에 직접 전달하는 방식
- 클라이언트가 발행 주기를 제어할 수 있어서 즉시성이 높다
- 네트워크 장애 시 유실 위험이 있다
- graphite, influx, jmx, new relic 등

pull
- 모니터링 시스템이 주기적으로 애플리케이션에서 데이터를 가져오는 방식
- 보안 및 장애 처리에 상대적으로 유리하다
- 애플리케이션에서 metrics 엔드포인트를 노출해야 하므로 별도의 보안 설정이 필요하다
- prometheus, all statsd flavors


## meters

`Meter` 인터페이스는 애플리케이션 메트릭을 수집하는 역할을 한다

마이크로미터는 primitives, Timer, Counter, Gauge, DistributionSummary, LongTaskTimer, FunctionCounter, TimeGauge 등의 Meter 구현체를 지원한다

각 meter 인스턴스는 고유한 이름과 차원(dimension, tag - Tag 인터페이스)을 가진다

차원을 사용하면 특정 명명된 메트릭 항목을 분할하여 데이터를 분석(drill down)하고 문제 원인을 추론할 수 있다 (반대로 이름을 선택한 경우 다른 dimension을 통해 분석할 수 있다)

### naming meters

메트릭의 이름은 각 모니터링 시스템에서 권장하는 형식이 다르기 때문에 시스템 간 호환되지 않을 가능성이 있다

이러한 오류를 줄이고자 마이크로미터에서 Meter 이름 컨벤션을 맞추고 대상 모니터링 시스템에서 권장하는 컨벤션으로 변환하고, 모니터링에서 허용하지 않는 특수문자를 제거한다

마이크로미터의 컨벤션: Meter의 이름을 단어를 `.`로 구분하고 모두 소문자로 사용

예를 들어서 `registry.timer("http.server.requests");`와 같이 설정하면 모니터링 시스템에 따라 다음과 같이 변환된다

```text
prometheus - http_server_requests_duration_seconds

atlas - httpServerRequests

graphite - http.server.requests

influxdb - http_server_requests
```

기본 네이밍 컨벤션을 오버라이딩하고 싶은 경우 NamingConention을 구현하고 아래와 같이 해당 레지스트리에 설정하면 된다

```java
registry.config().namingConvention(myCustomNamingConvention);
```

### common tags

[common tags](https://docs.micrometer.io/micrometer/reference/concepts/naming.html#_common_tags)


## registry

MeterRegistry는 Meter들을 등록하고 수집된 데이터를 모니터링 시스템에 발행(publishing)하는 역할을 한다

모든 Meter는 MeterRegistry에 등록되어야 데이터가 수집되고 전송될 수 있다

마이크로미터가 지원하는 모니터링 시스템(prometheus, influxdb 등)들은 각각 MeterRegistry 구현체(PrometheusMeterRegistry)를 제공한다

또한 마이크로미터에서는 각 meter의 최신 값을 메모리에만 보관하고 데이터를 외부로 내보내지 않는 SimpleMeterRegistry를 제공하는데, 모니터링 시스템을 가지지 않은 경우 간단히 사용하기에 유용하다

스프링 애플리케이션에서는 SimpleMeterRegistry를 의존성 주입받을 수 있다

### composite registries

한 개 이상의 모니터링 시스템에 메트릭을 동시에 발행해야 하는 경우 CompositeMeterRegistry를 사용할 수 있다

```java
CompositeMeterRegistry composite = new CompositeMeterRegistry();
Counter compositeCounter = composite.counter("counter");

// 복합 registry가 구성되기 전까지 값이 증가하지 않는다
compositeCounter.increment();

SimpleMeterRegistry simple = new SimpleMeterRegistry();

// registry 추가
composite.add(simple);

// 값 증가
compositeCounter.increment();
```

### global registry

마이크로미터는 `Metrics.globalRegistry`라고 불리는 정적 레지스트리와 정적 meter 빌더 메서드를 제공한다

global registry도 composite registry처럼 한 개 이상의 registry를 추가할 수 있다

```java
class MyComponent {
    
    // meter 추가
    Counter featureCounter = Metrics.counter("feature", "region", "test");

    void feature() {
        featureCounter.increment();
    }

    void feature2(String type) {
        Metrics.counter("feature.2", "type", type).increment();
    }
}

class MyApplication {
    void start() {
        // 정적 레지스트리에 모니터링 시스템 추가
        Metrics.addRegistry(new SimpleMeterRegistry());
    }
}
```








## meter filters

MeterFilter는 MeterRegistry에 Meter가 등록되기 전, 발행되기 전에 메트릭을 필터링하거나 수정하는 인터페이스를 말한다

주로 다음 용도로 사용된다
- meter 등록 승인 또는 거부
- meter id 변환 (이름 변경, 태그 추가 또는 삭제, 설명 수정 등)
- 분포 메트릭 설정 (Timer, DistributionSummary)

[자세한 내용 참고](https://docs.micrometer.io/micrometer/reference/concepts/meter-filters.html)




## rate aggregation

rate aggregation (비율 집계)은 메트릭 데이터를 시간당 비율로 집계하는 것을 말하며 초당 요청 수, qps(queries for second), tps(transactions per second), 에러율 등을 계산할 때 사용한다

단순히 절대 값을 보고하는 것보다 시간에 따른 변화율을 파악하기에 유리하다

다만 메트릭 유형에 따라 비율로 나타내기 적절하지 않은 경우도 있다 - gauge(현재 상태 값), long task time(작업 중인 상태) 

마이크로미터는 사용 중인 모니터링 시스템이 메트릭이 발행되기 전에 클라이언트 단에서 비율 집계를 원하는지, 아니면 서버에서 일부분 쿼리로 남길 바라는지 인식하고 모니터링 시스템이 기대하는 스타일에 따라 메트릭을 축적한다

즉, 마이크로미터에서 비율 집계는 모니터링 시스템이 데이터를 수집하고 처리하는 방식에 따라 server-side와 client-side 방식으로 나뉘고 마이크로미터는 모니터링 시스템의 특성에 따라 자동으로 선택한다 

### server-side rate aggregation

server-side 방식은 마이크로미터가 절대값(absolute value)을 일정 간격(interval) 모니터링 시스템에 보내고, 모니터링 시스템은 이를 기반으로 비율을 계산한다

e.g) Counter는 서비스가 시작된 이후의 누적 값을 보고하며, 서비스가 재시작되면 0으로 초기화된다

모니터링 시스템이 비율을 동적으로 계산할 수 있어 유연한 쿼리와 분석이 가능하지만 쿼리 시점에 계산하기 때문에 성능 저하를 불러 일으킬 수 있다

도구: prometheus

### client-side rate aggregation

client-side 방식은 마이크로미터가 비율을 사전에 계산하여 모니터링 시스템에 전송한다

모니터링 시스템이 rate aggregation 기능을 가지지 않았거나 사전에 계산된 비율을 기대하는 경우 사용된다 - datadog

동작 원리
- 마이크로미터는 일정한 간격(step interval) 동안 데이터를 누적한다
- step interval이 끝나면 현재 데이터는 이전 상태(previous state)로 이동하고 새 데이터를 수집한다
- 이전 상태의 데이터가 모니터링 시스템에 보고되며 이 데이터는 해당 interval 동안의 비율을 의미한다
- e.g) 10초 동안 20개의 요청이 발생했다면, 초당 2개의 요청이 있었다는 비율을 보낸다

```text
count = 10 seconds * (20 requests / 10 seconds) = 20 requests
totalTime = 10 seconds * (20 * 100 ms / 10 seconds) = 2 seconds 
```

모니터링 시스템이 비율을 계산할 필요가 없어 데이터 조회 성능이 우수하지만 미리 계산된 비율이기 때문에 세밀한 분석이 어렵다

도구: datadog











