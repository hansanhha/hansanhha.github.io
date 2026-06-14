---
layout: default
title:
---

#### index
- [observability 3 pillars](#observability-3-pillars)
- [(important) spring boot observability architecture](#important-spring-boot-observability-architecture)
- [observability autoconfiguration](#observability-autoconfiguration)
- [user-defined observation beans](#user-defined-observation-beans)
- [common tags](#common-tags)
- [preventing observations](#preventing-observations)


## observability 3 pillars

observability(가시성, 관측 가능성)는 외부에서 실행 중인 시스템의 내부 상태를 관찰하는 기능 또는 능력을 말하며 logging, metrics, traces 라는 세 가지의 틀로 구성된다

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


## (important) spring boot observability architecture

스프링 부트는 micrometer observation을 사용하여 metrics와 traces 컴포넌트를 통합한다

```text
        ┌────────────────────────────────────────┐
        │             spring boot             |
        │                                     │
        │   ┌────────────────────────────────┐   │
        │   │      actuator endpoints     |   |
        │   └────────────────────────────────┘   │
        │     │           │           │        │
        │     │           │           │        │
        │ ┌───▼─────┐  ┌───▼───┐   ┌────▼────┐  │
        │ │ traces │  │ logs  │   │ metrics │  │
        │ └─────────┘  └────────┘  └──────────┘  │
        |                                     |
        └───────────┬───────────────┬─────────────┘
                   │              │
                   ▼              ▼
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
- micrometer와 통합하여 metrics를 관리하고 traces와 logs도 수집한다

micrometer
- metrics 및 traces 집계 도구로 prometheus와 같은 모니터링 시스템과 연결한다
- @Timed, @Counted 등의 어노테이션으로 커스텀 메트릭을 정의하고 수집할 수 있다

prometheus
- micrometer와 연동하여 메트릭 수집 및 알림 설정에 사용된다

grafana
- prometheus 및 loki에서 수집된 데이터를 시각화 및 대시보드 형태로 보여준다

zipkin, opentelemetry
- 각 서비스의 traces와 span을 수집하여 요청의 흐름을 파악한다
- grafana와 연동하여 trace 데이터를 시각화할 수 있다


### observability workflow

#### 1. collection

micrometer는 스프링 부트 애플리케이션에서 merics와 traces 데이터를 수집한 뒤 스프링 부트 액추에이터의 엔드포인트(`/actuator/metrics` `/actuator/loggers` `/actuator/httptrace` 등)를 통해 데이터를 외부 모니터링 시스템(prometheus 등)에 전송한다

prometheus는 pull 방식을 사용하여 스프링 부트 액추에이터(`/actuator/prometheus`)에서 메트릭 데이터를 가져오는데, 이를 위해 Promethues MeterRegistry가 micrometer에 적용된다

#### 2. storage

prometheus는 metrics를 시계열 데이터베이스에 저장한다

zipkin은 trace 데이터를 저장하고, loki는 로그 데이터를 저장한다

#### 3. analysis & visualization

grafana는 저장된 데이터를 기반으로 대시보드를 생성하고 시각화한다

#### 4. alerting & incident response

grafana 및 prometheus alertmanager를 통해 sla/slo 위반 시 알림을 전송한다


## observability autoconfiguration

spring boot starter actuator 의존성을 추가하면 자동적으로 micrometer jakarta(commons, core)와 micrometer observation 의존성을 가져온다

micrometer observation은 micrometer metrics/tracing을 통합하여 메트릭과 추적 데이터를 수집하여 observability를 구현하는 micrometer의 기능으로 prometheus, grafana, loki와 같은 외부 모니터링 시스템과 쉽게 통합할 수 있다

다만 micrometer observation 라이브러리가 micrometer tracing을 의존성으로 가지고 있지 않고 필요 시 사용자가 필요한 경우에 추가해줘야 한다

스프링 부트는 다음과 같은 @AutoConfiguration 클래스로 observation과 관련된 자동 구성을 수행한다

### CompositeMeterRegistryAutoConfiguration

NoOpMeterRegistryConfiguration, CompositeMeterRegistryConfiguration 구성 클래스를 import하는 구성 클래스

NoOpMeterRegistryConfiguration: micrometer의 Clock이 스프링 빈으로 등록되어 있으면서 MeterRegistry 타입의 스프링 빈이 없는 경우에 활성화되며 비어 있는 CompositeMeterRegistry를 빈으로 등록한다

CompositeMeterRegistryConfiguration: 두 개 이상의 MeterRegistry 타입의 스프링 빈이 있는 경우 활성화되며 해당 빈들을 포함한 CompositeMeterRegistry를 빈으로 등록한다

### MetricsAutoConfiguration

[CompositeMeterRegistryAutoConfiguration](#compositemeterregistryautoconfiguration) 이후에 활성화되는 자동 구성 클래스

micrometer의 Clock 타입의 스프링 빈이 등록되지 않은 경우 Clock과 micrometer metrics와 관련된 빈(MeterRegistryPostProcessor, PropertiesMeterFilter, MeterRegistryCloser)들을 등록하며 MetricsProperties (`management.metrics`) 프로퍼티 설정을 활성화시킨다

MeterRegistryPostProcessor: MeterRegistry 빈 후처리기로 MeterRegistry에 대해 MeterRegistryCustomizer 적용하고 MeterFilter 등록하며, 필요에 따라 global registry로 지정하거나 binder를 적용하는 MeterRegistryPostProcessor를 등록한다 

PropertiesMeterFilter: MeterProperties의 설정 값을 적용한 한 개의 MeterFilter 인스턴스를 생성하고 Meter와 관련된 작업을 수행하는 PropertiesMeterFilter를 등록한다  

MeterRegistryCloser: 스프링 부트 애플리케이션이 종료될 때 등록된 MeterRegistry 목록을 전부 닫아주는 유틸 클래스를 등록한다

### MicrometerTracingAutoConfiguration

micrometer tracing 의존성이 클래스 패스에 존재하면서 Tracer 타입의 스프링 빈이 등록되었을 때 활성화되는 자동 구성 클래스

spring boot starter actuator를 추가해도 micrometer tracing 의존성을 자동적으로 불러오지 않기 때문에 (spring boot 3.4.2 기준) 기본적으로 활성화되지 않는다

이 클래스에 의해 스프링 빈으로 등록되는 tracing 관련 클래스 다음과 같다 

DefaultTracingObservationHandler, PropagatingSenderTracingObservationHandler, PropagatingReceiverTracingObservationHandler, SpanAspectConfiguration 

### ObservationAutoConfiguration

[CompositeMeterRegistryAutoConfiguration](#compositemeterregistryautoconfiguration), [MicrometerTracingAutoConfiguration](#micrometertracingautoconfiguration) 이후 평가되는 자동 구성 클래스

micrometer observation과 관련된 클래스들을 스프링 빈으로 등록하고 ObservationProperties (`management.observations`) 프로퍼티 설정을 활성화시킨다

자동 구성 대상 클래스 목록

ObservationRegistry: 스프링 컨텍스트에 없으면 `SimpleObservationRegistry` 타입으로 ObservationRegistry를 스프링 빈으로 등록한다

PropertiesObservationFilterPredicate: ObservationProperties를 기반으로 하나의 ObservationFilter를 생성하고 관련된 작업을 수행하는 PropertiesObservationFilterPredicate를 등록한다 

ObservationHandlerGrouping: observation 유형 별(traces, metrics)로 그룹화하는 클래스로 클래스패스의 있는 micrometer 의존성에 따라 tracing 단독, meter 단독 또는 두 개 묶음으로 그룹화하는 ObservationHandlerGrouping를 등록한다    

DefaultMeterObservationHandler: MeterObservationHandler와 Tracer 타입의 스프링 빈이 등록되지 않은 경우 counter와 timer를 측정하는 DefaultMeterObservationHandler를 등록한다

TracingAwareMeterObservationHandler: Tracer 타입의 스프링 빈이 등록된 경우 tracing 데이터에 접근할 수 있는 TracingAwareMeterObservationHandler를 등록한다 

ObservedAspect: aspectj weaver가 클래스패스에 있는 경우 aop 기반으로 observation 기능을 수행하는 micrometer의 ObservedAspect를 스프링 빈으로 등록한다



## user-defined observation beans

사용자가 micrometer observation에서 제공하는 타입을 스프링 빈으로 등록하면 스프링 부트 자동 구성에 의해 등록된 ObservationRegistry에 모두 등록된다

ObservationPredicate, GlobalObservationConvention, ObservationFilter, ObservationHandler

ObservationRegistryCustomizer를 빈으로 등록하면 ObservationRegistry를 커스터마이징할 수 있다




## common tags

프로퍼티를 통해 모든 observation에 low cardianlity(제한된 범위의 태그)로써 os, host, region, stack 등과 같은 공통 태그를 적용할 수 있다

```yaml
management:
  observations:
    key-values:
      region: us-east-1
      stack: prod
```


## preventing observations

프로퍼티를 통해 다음과 같이 특정 observation 들이 리포팅되는 것을 방지할 수 있다

아래의 예제는 `denied.prefix` 또는 `another.denied.prefix`로 시작되는 observation 들의 리포팅을 방지한다

```yaml
management:
  observations:
    enable:
      denied:
        prefix: false
      another:
        denied:
          prefix: false
```

아래와 같이 설정하면 스프링 시큐리티와 관련된 observation 리포팅을 방지할 수 있다

```yaml
management:
  observations:
    enable:
      spring:
        security: false
```


또는 ObservationPredicate 타입을 빈으로 등록하여 좀 더 상세하게 리포팅 방지를 제어할 수 있다

```java
@Component
class MyObservationPredicate implements ObservationPredicate {

	@Override
	public boolean test(String name, Context context) {
		return !name.contains("denied");
	}

}
```



