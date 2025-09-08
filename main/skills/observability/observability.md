---
layout: default
title:
---

#### index
- [monitoring](#monitoring)
- [observability](#observability)
- [observability design principles](#observability-design-principles)
- [observability architectures](#observability-architectures)

## monitoring

모니터링은 **시스템의 상태를 확인 및 감지하고 경고**를 하는 것이 주된 목적이다

시스템의 상태가 정상인지 비정상인지를 감지하고 경고를 발생시키는 데 유용하다 - e.g) 서버 다운 시 슬랙 알림 받기

사전에 정의한 지표(metrics)나 로그에 기반(의존)하여 시스템 상태를 감시한다

설정된 지표와 임계값에 따라 알림을 발생시키는 구조를 가지고 **정적이고 수동적인 접근 방식**을 취한다

과거와 현재 상태를 파악하는 데 중점을 두며 다음과 같이 미리 정의된 질문에 대한 답을 제공한다

e.g) cpu 사용률이 80%를 넘겼는가?, 응답 시간이 2초를 초과했는가?

주요 도구: prometheus (메트릭 수집 및 경고 설정), grafana (시각화 대시보드), nagiox/zabbix (시스템 및 네트워크 상태 모니터링) 


## observability

observability는 가시성 또는 관찰 가능성이라고 하며 **시스템 내부 상태와 동작을 이해하고 분석**하는 것이 주된 목적이다

문제의 원인을 분석하고 성능 최적화를 위해 시스템 내부 상태와 동작을 깊이 이해하는 데 유용하다 - e.g) 특정 api의 응답 시간이 느린 원인 분석

주로 다음과 같은 목표를 가진다
- 문제 발생의 원인을 신속하게 파악
- 성능 병목(performance bottleneck)을 분석 및 최적화
- 서비스 장애(incident)를 예방 및 대응 시간 단축
- 분산 트랜잭션 추적 및 사용자 경험 모니터링 

모니터링이 가지는 정적이고 수동적인 접근 방식과 달리 **동적이고 탐색적인 접근 방식**을 취하여 다음과 같이 미리 정의하지 않은 질문에도 답을 제공한다

e.g) 왜 특정 시간대에 응답 시간이 증가했는가?, 특정 사용자의 오류 비율이 높은 이유는 무엇인가?

근본 원인 분석과 예측에 중점을 두며 다음 세 가지의 개념을 기반으로 한다 [observability 3 ways: loggings, metrics & tracing](https://www.youtube.com/watch?v=juP9VApKy_I)

#### 메트릭(metrics)
- 시스템 상태의 수치화된 데이터 (cpu 사용률, 요청 수, 에러 비율 등)
- 고해상도 메트릭: 초 단위 데이터 수집 (prometheus + thanos)
- 메트릭 태깅 및 라벨링: 서비스, 지역 및 환경 별 태그 추가
- 고급 알림 설정: 예측 분석 기반 임계값 설정 (aiops 도구와 연계)
- 주요 도구: prometheus + thanos, victoriametrics, datadog, new relic

#### 로그(logs)
- 이벤트 중심의 기록(에러 메시지, 상태 변경 등)으로 디버깅 및 감사(audit) 용도로 사용한다
- 구조화된 로그 사용: 분석에 용이한 포맷(json, xml 등)으로 로그를 저장
- 로그 샘플링: 트래픽 폭증 시 로그 볼륨 관리(loki 로그 샘플링 등)
- 로그 상관 관계 분석: 로그 간 상관 관계 분석으로 문제 발생의 원인을 추적
- elk stack (elasticsearch, logstash, kibana), loki + grafana, fluentd, splunk

#### 추적(traces)
- 분산 시스템에서 요청의 흐름(마이크로서비스 간 호출 경로 등)과 지연 원인을 분석한다
- end-to-end 분산 추적: 사용자 요청의 시작부터 끝까지 전체 경로 추적
- trace 샘플링 및 필터링: 고비용 트랜잭션만 선택적으로 저장
- span 태그 추가: 호출 간 관계 및 상관 관계 분석
- jaeger, zipkin, opentelemetry, aws x-ray


## observability design principles

가시성 설계에 관한 원칙들은 아래와 같으며 설계자는 [observability 아키텍처](#observability-architectures)에 따라 적절하게 준수할 수 있다 

### instrumentation first (계측 우선 설계)

코드에 metrics, logs, traces를 위한 계측 도구를 먼저 설계한다

micrometer + open telemetry를 사용하여 metrics와 traces를 자동으로 수집할 수 있다

### context correlation (컨텍스트 상관관계)

correlation id (trace id 등)를 로그, 메트릭, 트레이스에 포함하여 이벤트 연관 관계를 분석한다

e.g) 사용자 요청이 여러 마이크로서비스를 거칠 때 동일한 trace id를 사용한다

### high availability and scalability

observability 도구 자체가 장애났을 때 애플리케이션에 영향을 주지 않도록 설계해야 한다

thanos나 victoriametrics와 같은 수평 확장 가능한 메트릭 저장소를 사용한다

elasticsearch 클러스터를 다중 노드로 구성하여 로그 저장소를 확장한다


## observability architectures

### simple observability system

시스템의 상태를 확인하고 감지하는 모니터링을 중심으로 하는 시스템을 구축하는 전략으로 주로 소규모 애플리케이션에서 사용한다

prometheus + grafana로 주요 메트릭과 상태를 감시하거나 알림을 보내며 추가적으로 loki, logstash 등의 도구를 활용하여 필요한 로그를 수집할 수 있다

### enhancement observability system

기본적인 모니터링 시스템(prometheus + grafana)을 기반으로 zaeger, zipkin 등의 tracing 도구를 도입하여 observability를 강화하는 전략

opentelemetry로 통합된 데이터를 수집한다

### large-scale application observability system

시스템이 복잡하고 대규모 분산 환경(마이크로서비스, 컨테이너 기반 배포 등)에서 사용하는 전략으로 아래의 요소를 만족하는 아키텍처를 설계한다

1. 중앙화된 observability platform 구축
2. 데이터 저장소 분리 및 확장
3. 멀티 클러스터 및 멀티 리전 환경 모니터링
4. 자동화된 알림 및 incident 관리
5. 모니터링 데이터 최적화
6. ci/cd 파이프라인 통합

```text
                         +-------------------+
                         |   ci/cd pipeline   |
                         | (gitHub actions,   |
                         |  jenkins, argo cd) |
                         +---------|----------+
                                   |
                            [ code deploy ]
                                   |
         +-------------------------|---------------------------+
         |                         |                          |
+--------v--------+        +-------v-----------+        +--------v--------+
|   application   |        |  sidecar injector |        |  service mesh   |
|  (microservice) |        |  (opentelemetry)  |        | (istio/linkerd) |
+--------|--------+        +---------|----------+       +---------|-------+
         |                           |                            |
         |      +--------------------|--------------------+       |
         |      |                    |                    |       |
         |      |                    |                    |       |
         |  (metrics)             (logs)              (traces)    |
         |      |                    |                    |       |
+--------v------v-+         +--------v-------------+     +v-------v-------+
|   prometheus    |         |        loki          |     | jaeger, zipkin |
| +thanos/cortex  |         |  (fluentd/fluent bit)|     | (opentelemetry)|
+--------|--------+         +---------|------------+     +--------+-------+
         |                            |                          |
         | (long-term storage)        |                          |
         |                            |                          |
+--------v------+           +---------v----------+        +------v------+
|  object store |           |   elasticsearch    |        |   tempo     |
|  (s3/gcs)     |           |   (optional)       |        | (optional)  |
+---------------+           +--------------------+        +-------------+

                         +------------------------------+
                         |        grafana               |
                         |   (dashboards, alerts)       |
                         +------|--------------|--------+
                                |              |
                     +----------v--------------v-------------+
                     |      alertmanager + pagerduty         |
                     | (alert routing & incident management) |
                     +---------------------------------------+
```

#### 중앙화된 observability platform 구축

metrics, logs, traces를 중앙 집중화하여 한 곳에서 모니터링 및 분석을 수행한다

opentelemetry collector로 통합된 데이터 수집 및 전송

도구: opentelemetry collector + grafana + loki + jaeger

#### 데이터 저장소 분리 및 확장

metrics, logs, traces 각각의 저장소를 분리하여 성능 및 확장성을 향상시킨다

metrics: prometheus + thanos/cortex, grafana, object store(s3/gcs)

logs: loki, elk stack, fluentd

traces: jaeger, tempo, zipkin, opentelmetry

#### 멀티 클러스터 및 멀티 리전 환경 모니터링

쿠버네티스 멀티 클러스터 또는 멀티 리전 배포 환경에서 thanos 또는 cortex 사용

thanos: prometheus 메트릭을 중앙에서 수집 및 분석, 수평 확장

cortex: 멀티 테넌트 환경에서 메트릭 저장 및 관리

#### 자동화된 알림 및 incident 관리

alertmanager: prometheus에서 설정한 alert rule 기반 알림

pagerduty: incident 관리 및 on-call 대응 / sms, slack, email 등 다양한 알림 채널 통합 등


#### 모니터링 데이터 최적화

noise reduction: 불필요한 알림 감소 (alertmanager에서 rate limit 적용)

root cause analysis: traces, logs, metrics 상관 관계 분석, grafana explore 기능 활용

alops 연계: datadog, new relic 같은 ai 기반 이상 탐지 sass 활용


#### ci/cd 파이프라인 통합

github actions, jenkins, argocd 등의 파이프라인 도구를 사용하여 코드 변경 시 빌드, 테스트, 배포 수행

argocd: gitops 방식으로 쿠버네티스 배포를 자동화한다

opentelemetry instrumentation: ci/cd 과정에서 애플리케이션에 계측 코드를 자동으로 주입한다










