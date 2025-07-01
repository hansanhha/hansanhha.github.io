---
layout: default
title:
---

#### index
- [micrometer tracing](#micrometer-tracing)
- [tracing terminology](#tracing-terminology)
- [workflow](#workflow)
- [dependencies](#dependencies)


## micrometer tracing

micrometer tracing은 micrometer observability의 일부로 분산 시스템의 요청 흐름을 시각화하여 성능 모니터링과 장애 진단을 위해서 사용된다

micrometer metrics와 마찬가지로 특정 tracing 라이브러리에 락인되지 않도록 jvm 애플리케이션에 대한 간단한 facade를 제공하며 주로 brave(zipkin)나 opentelemetry와 통합된다

스프링 클라우드 팀은 spring cloud sleuth 라는 tracing 라이브러리를 개발하여 사용하다가 스프링 클라우드 프로젝트와 관심사 분리를 위해 spring-agnostic한 micrometer tracing 프로젝트를 만들게 되었다

micrometer 1.10 버전부터 ObservationHandler tracing extension을 제공하여 Observation이 사용될 때마다 span을 생성, 시작, 중지, 리포팅할 수 있는 기능을 제공한다


## tracing terminology

micrometer tracing은 [구글의 dapper 아키텍처](https://research.google/pubs/dapper-a-large-scale-distributed-systems-tracing-infrastructure/)에서 소개된 개념과 용어를 차용한다

### span

작업의 기본 단위로 애플리케이션에서 특정 작업이나 이벤트를 나타낸다 (api, database query, rpc 등)

각 애플리케이션/시스템에서 발생하는 작업마다 새로운 span을 생성하거나 상위 span에 연결한다

span의 구성 요소는 다음과 같다
- span id: 스팬 고유 식별자
- operation name: 스팬 이름 (db.query, http.request 등)
- timestamped events (annotations): 특정 시점의 이벤트 기록
- tags (key-value annotations): 스팬의 부가 정보
- parent span id: 상위 span id (스팬은 트리 구조를 만든다)
- process id: ip 주소, 호스트 정보 등

클라이언트가 서버에 api 요청을 보낼 떄 클라이언트 측 span, 서버 측 span이 각각 생성된다 (서버-서버 간)

span은 시작 시점과 종료 시점을 기록하여 이후에 처리 시간(latency)을 계산할 수 있도록 한다

### trace

여러 span이 모여 만든 전체 요청 흐름을 나타내며 트리 구조로 표현된다

하나의 trace는 단일 요청이 분산 시스템 내에서 거치는 모든 작업(span)을 포함한다

e.g) 사용자의 상품 주문 요청

1. api gateway가 요청을 받아 span을 시작한다
2. shallowOrder service에서 새로운 span을 생성한다
3. payment service와 cart service에서 각각 span을 생성한다
4. 전체 요청 흐름이 하나의 trace id로 연결된다

### annotation/event

특정 시점(span)에 발생한 이벤트를 기록한다

e.g) cs(client send), sr(server receive), ss(server send), cr(client receive)와 같은 이벤트 타임 스탬프

### tracer

span의 생성, 시작, 종료 및 외부 시스템에게 보고를 담당하는 라이브러리

micrometer tracing은 micrometer metrics에서 MeterRegistry를 추상화하고 PrometheusMeterRegistry를 사용하는 것처럼 Tracer를 통해 tracing 기능을 추상화한다

그리고 tracer 구현체에서 추상화된 span을 새로 생성하고 현재 컨텍스트에 연결한다음, 구현체 포맷으로 변환하거나 종료한다

micrometer 메트릭 데이터를 특정 registry 구현체의 포맷으로 변환하듯이 micrometer의 span/trace 데이터도 해당 tracer 구현체의 포맷으로 변환된다

변환된 데이터는 reporter 또는 exporter를 통해 외부 tracing 시스템에 전달된다

tracer 구현체
- BraveTracer: brave 기반 구현체 (zipkin, grafana, tempo, jaeger 등)
- OtelTracer: opentelemetry 기반 구현체 (grafana, tempo, jaeger, prometheus, aws x-ray 등)

### tracing context

멀티 스레드 환경 또는 마이크로서비스 등의 분산 시스템 간의 통신에서 trace 정보를 유지하기 위해 tracing context를 사용한다

tracing context는 trace id, span id 및 관련 메타 데이터를 포함하며 컨텍스트 전파를 통해 시스템/애플리케이션 간 추적 정보를 유지한다

### log correlation

log correlation(로그 상관관계)은 trace id와 span id를 애플리케이션 로그에 포함시켜 로그와 추적 데이터를 연결하는 방법을 말한다

장애가 발생했을 때 특정 trace id로 관련된 모든 서비스의 로그를 한 눈에 알아보기 쉽다

또한 이후 로그 분석 도구에서 필터링 및 검색하기에 용이하다

### latency analysis tools

수집된 trace 데이터를 시각화하고 전체 trace 흐름을 보여주는 도구

분산 시스템의 지연(latency)을 분석하고 병목 현상을 파악하거나 요청이 어느 서비스에서 지연되는지, 응답 시간이 긴 부분을 시각화한다

zipkin: 경량화된 분산 추적 시스템

jaeger: uber에서 개발한 고성능 분석 추적 도구

opentelemetry collector: 다양한 추적 데이터를 수집하고 내보내는 표준 도구


## workflow

### 1. using facade api (micrometer tracing/observation api)

애플리케이션 코드에서 @Observed, @NewSpan, Tracer, Baggage 등을 이용하여 span을 생성하고 trace context를 관리한다

### 2. bridging api and implementation

micrometer observation/tracing은 추상화된 api를 제공하고 실제 구현체는 opentelemetry, zikpin 등에서 제공한다

이 둘을 연결하기 위해 micrometer tracing은 openzipkin brave, opentelemetry brdige를 제공한다 (micrometer-tracing-bridge-brave or micrometer-tracing-bridge-otel)

bridge 모듈이 micrometer span을 해당 brdige의 span으로 변환시킨다 (Span <-> BraveSpan, Span <-> OtelSpan)

### 3. tracer operation

Tracer 구현체(BraveTracer, OtelTracer)가 span 생성, 수집 및 관리, trace context를 포함한 메타데이터 자동 전파 등 추적과 관련된 실질적인 로직을 수행한다

### 4. export collected data

exporter/reporter를 사용하여 수집된 데이터를 외부 tracing 시스템에 내보낸다

opentelemetry exporteter를 사용하는 경우 http 및 rgpc를 지원하는 otlp(opentelemetry protocol)를 이용할 수 있다

OtlpGrpcLogRecordExporter, OtlpHttpLogRecordExporter 등

### 5. external tracing system

prometheus, zipkin, jaeger, grafana tempo 등의 추적 시스템은 exporter/reporter를 통해 전달받은 데이터를 저장하고 시각화한다

또한 분산 추적 데이터를 분석하고 검색할 수 있는 기능을 제공한다


## dependencies

micrometer tracing에서 제공하는 bom을 사용하여 버전 관리를 편하게 할 수 있다

micrometer-tracing-bom 모듈은 `context-propagation` `observation` `tracing` 을 포함한다

```kotlin
dependencies {
    implementation(platform("io.micrometer:micrometer-tracing-bom"))
    implementation("io.micrometer:micrometer-tracing")

//    implementation("io.micrometer:micrometer-tracing-bridge-brave")
//    implementation("io.micrometer:micrometer-tracing-bridge-otel")
}
```

#### micrometer tracing

tracing instrumentation facade api를 제공하는 micrometer의 라이브러리

직접적으로 tracer 라이브러리(brave, opentelemtry)를 사용하지 않고 micrometer tracing api를 통해 추적 데이터를 생성할 수 있다

생성된 추적 데이터를 외부 tracing 시스템으로 내보내기 전 bridge를 통해 특정 tracer 라이브러리에서 사용하는 포맷으로 변환해야 한다

```kotlin
implementation("io.micrometer:micrometer-tracing")
```

#### micrometer bridge (tracer)

micrometer tracing api를 실제 tracer 구현체(brave, opentelemtry)에 연결(bridge)하는 역할을 한다

추상화된 span/trace 데이터를 해당 구현체에서 사용하는 포맷으로 변환한다

변환된 span/trace 데이터는 exporter/reporter를 통해 외부 tracing 시스템으로 전송될 수 있다

```kotlin
implementation("io.micrometer:micrometer-tracing-bridge-brave")
implementation("io.micrometer:micrometer-tracing-bridge-otel")
```

brdige-otel 모듈은 `io.opentelemetry:opentelemetry-*` api, sdk 등의 모듈과 `io.opentelemetry.semconv` `io.opentelemetry.instrumentation` 모듈을 포함한다 

#### exporter/reporter

수집된 span/trace 데이터를 외부 tracing 시스템으로 전송하는 역할을 한다

exporter의 종류에 따라 데이터를 지정된 프로토콜로 직렬화하고 네트워크를 통해 다른 tracing 백엔드로 전송한다

micrometer에서 의존성을 제공하지 않고 외부에서 제공하는 의존성을 사용한다

```kotlin
implementation("io.zipkin.reporter2:zipkin-reporter:3.5.0")

implementation("io.opentelemetry:opentelemetry-exporter-otlp")
implementation("io.opentelemetry:opentelemetry-exporter-zipkin")
implementation("io.opentelemetry:opentelemetry-exporter-jaeger")
implementation("io.opentelemetry:opentelemetry-exporter-prometheus")
```

otlp exporter: grafana tempo, opentelemetry collector로 전송한다

zipkin, jaeger, prometheus exporter: 각 tracer 구현체에게 전송한다


#### external tracing system

수집된 추적 데이터를 저장하고 시각화하는 시스템으로 latency 분석, trace id 기반 호출 흐름 분석 등의 기능을 제공한다

주요 시스템
- grafana tempo: otlp exporter 사용, 분산 추적 데이터 저장 및 시각화
- zipkin: zipkin exporter 사용, 간단한 ui를 통한 trace 분석
- jaeger: jaeger exporter 사용, trace 시각화 및 latency 분석
- prometheus: prometheus exporter 사용, 메트릭 기반 모니터링, 알람 시스템