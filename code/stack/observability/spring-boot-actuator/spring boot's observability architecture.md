[observability 3 pillars](#observability-3-pillars)

[spring boot observability](#spring-boot-observability)

[observability workflow](#observability-workflow)



## observability 3 pillars

### traces

애플리케이션의 요청 흐름을 분석하여 분산된 시스템 간의 호출 관계를 파악한다

구조
- trace: 하나의 전체 요청 흐름
- span: trace를 구성하는 개별 작업 단위
- context propagation: 요청이 다른 서비스로 전달될 때 trace/span 정보를 포함하여 연속성 유지

도구
- micrometer tracing (spring boot 3.x ~)
- brave/zipkin
- opentelmetry

### logging

애플리케이션의 상태 및 이벤트에 대해 텍스트를 기반으로 기록하여 애플리케이션의 이벤트 흐름 또는 오류를 추적하거나 디버깅을 위한 정보를 제공한다

구조
- logger: 로그를 기록하는 주체
- appender: 로그를 저장할 위치 (콘솔, 파일, 원격 서버 등)
- layout: 로그 형식 지정 (json 등)

도구
- logback (default)
- loki + grafana

### metrics

애플리케이션 및 시스템 성능에 대한 수치화된 데이터를 말한다

구조
- gauge: 현재 상태의 수치 (메모리 사용량 등)
- counter: 이벤트 발생 횟수 (http 요청 수 등)
- timer: 이벤트 지속 시간 (api 응답 시간 등)
- summary: 통계적 요약 (응답 시간 평균, 최대값 등)

도구 
- micrometer metrics
- prometheus
- grafana


## spring boot observability

스프링 부트는 micrometer를 중심으로 observability의 traces와 metrics 컴포넌트를 통합한다

```text
        ┌────────────────────────────────────────┐
        │         spring boot app             │
        │                                     │
        │   ┌────────────────────────────────┐   │
        │   │      actuator endpoints     |   |
        │   └────────────────────────────────┘   │
        │     │           │           │        │
        │     │           │           │        │
        │ ┌───▼────┐  ┌───▼───┐  ┌────▼────┐   │
        │ │ traces │  │ logs  │  │ metrics │   │ 
        │ └─────────┘  └────────┘  └─────────┘   │
        └───────────┬───────────────┬─────────────┘
                   │              │
                   ▼             ▼
             ┌─────────────┐  ┌─────────────┐
             │   zipkin   │  │ prometheus │
             └─────────────┘  └─────────────┘
                   │               │
                   ▼              ▼
              ┌────────────────────────────┐
              │         grafana          │
              │ (visualization & alerts) │
              └────────────────────────────┘
```

actuator endpoints
- 스프링 부트 액추에이터가 제공하는 health, metrics, loggers, httptrace 등을 통해 observability 데이터를 노출한다
- micrometre와 통합하여 metrics를 관리하고 traces와 logs도 수집한다

micrometer
- metrics 및 traces 집계 도구로 prometheus와 같은 모니터링 시스템과 연결한다
- @Timed, @Counted 등의 어노테이션으로 커스텀 메트릭을 정의하고 수집할 수 있다

prometheus
- micormeter와 연동하여 메트릭 수집 및 알림 설정에 사용된다

grafana
- prometheus 및 loki에서 수집된 데이터를 시각화 및 대시보드 형태로 보여준다

zipkin, opentelemetry
- 각 서비스의 traces와 span을 수집하여 요청의 흐름을 파악한다
- grafana와 연동하여 trace 데이터를 시각화할 수 있다


## observability workflow

#### 1. collection

micrometer는 스프링 부트에서 merics와 traces 데이터를 수집한 뒤 스프링 부트 액추에이터의 엔드포인트(`/actuator/metrics` `/actuator/loggers` `/actuator/httptrace` 등)를 통해 데이터를 외부 모니터링 시스템(prometheus 등)에 전송한다

prometheus는 pull 방식을 사용하여 스프링 부트 액추에이터(`/actuator/prometheus`)에서 메트릭 데이터를 가져오는데, 이를 위해 Promethues MeterRegistry가 micrometer에 적용된다

#### 2. storage

prometheus는 metrics를 시계열 데이터베이스에 저장한다

zipkin은 trace 데이터를 저장하고, loki는 로그 데이터를 저장한다

#### 3. analysis & visualization

grafana는 저장된 데이터를 기반으로 대시보드를 생성하고 시각화한다

#### 4. alerting & incident response

grafana 및 prometheus alertmanager를 통해 sla/slo 위반 시 알림을 전송한다




micrometer observation

datasource micrometer project (Observability for JDBC can be configured using a separate project)

ObservationRegistry
- ObservationPredicate
- GlobalObservationConvention
- ObservationFilter
- ObservationHandler
- ObservationRegistryCustomizer

common tags

preventing observation