---
layout: default
title:
---

#### index
- [micrometer](#micrometer)
- [three important characteristics of monitoring system](#three-important-characteristics-of-monitoring-system)


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

