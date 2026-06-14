---
layout: default
title:
---

#### index
- [software metric](#software-metric)
- [metrics in monitoring](#metrics-in-monitoring)
- [metric details](#metric-details)
- [metric data collection methods](#metrics-data-collection-methods)
- [metric collection tools](#metric-collection-tools)
- [metric visualization tools](#metric-visualization-tools)
- [metric usecases](#metric-usecases)


## software metric

[소프트웨어 메트릭](https://en.wikipedia.org/wiki/Software_metric)은 소프트웨어의 품질, 성능, 생산성, 유지보수성 등을 **정량적으로 측정**하기 위해 사용되는 지표를 말한다

소프트웨어 개발 과정과 운영 과정에서 모두 사용하며 다음과 같은 목적으로 사용한다

메트릭 목적
- **품질 관리**: 코드 품질 측정 및 개선 부분 식별
- **생산성 평가**: 개발팀의 생산성 평가 및 향상
- **유지보수성 평가**: 코드 변경 시 영향 분석 및 리팩토링 결정
- **프로젝트 관리**: 일정 예측, 비용 산정, 리스크 관리
- **성능 분석**: 응답 시간, 메모리 사용량 등 성능 지표 분석

주요 메트릭스 유형
- **프로세스 메트릭**: 개발 프로세스 측정 (결함 밀도, 코드 리뷰 횟수, 빌드 성공률, 테스트 커버리지 등)
- **제품 메트릭**: 소프트웨어 제품 품질 및 성능 측정 (코드 복잡도, 응답 시간, 메모리 사용량, 모듈 크기 등)
- **프로젝트 메트릭**: 프로젝트와 관련된 측정 (개발 시간, 비용, 버그 수정 시간, 일정 준수율 등)


## metrics in monitoring

모니터링에서 메트릭을 측정한다는 것은 특정 관점에 수치화된 데이터를 수집하고 분석한다는 의미이다

애플리케이션, 서버, 네트워크 등에서 객관적으로 측정 가능한 데이터를 수집하고 그래프, 대시보드, 알림 등의 형태로 분석하거나 평가한다

이를 통해 성능 분석, 문제 탐지, 가용성 등을 파악하거나 운영 효율성을 높일 수 있다

모니터링에서 사용되는 메트릭의 주요 목적은 다음과 같다
- **성능 모니터링 및 분석**: 애플리케이션 및 시스템 상태를 확인하고 병목 현상 및 성능 저하의 원인을 분석한다 
- **장애 감지 및 알림**: 비정상적인 패턴을 감지하고 임계값을 초과하면 경고 및 알림을 전송한다
- **리소스 최적화 및 용량 계획**: 리소스 사용량을 모니터링하고 예측하여 서버 증설 또는 축소를 결정한다
- **비즈니스 지표 분석 및 의사결정**: 사용자 활동, 전환율, 매출 등을 분석하여 데이터 기반 의사결정을 수행할 수 있도록 돕는다


## metric details

### metric collection target type

시스템 메트릭: 서버 및 인프라 리소스 사용량 수집 - cpu 사용률, 메모리 사용량, 디스크 i/o, 네트워크 트래픽 등

애플리케이션 메트릭: 애플리케이션 성능과 상태를 수집 - 요청 수, 응답 시간, 에러율 등

비즈니스 메트릭: 비즈니스 관련 지표 - 활성 사용자 수, 주문 건수, 매출 등

### 특성에 따른 메트릭 분류

카운터(counter): 초기화 전까지 감소하지 않고 증가만 하는 값 - 요청 수, 에러 발생 횟수 등

게이지(guage): 증가, 감소할 수 있는 실시간 상태 값 - cpu 사용률, 메모리 사용량 등

히스토그램(histogram): 값의 분포를 구간별로 집계 - 응답 시간 분포 (0-100ms, 100-200ms 등)

요약(summary): 통계적인 요약 데이터 (평균, 백분위) - 평균 응답 시간, 95 백분위 응답 시간 등

### 시간 특성에 따른 메트릭 분류

현재 상태(current state): 현재 시점의 상태 - cpu 사용률, 메모리 사용량 등

이벤트 발생(event count): 특정 이벤트 발생 횟수 - 요청 수, 오류 발생 수 등

시간 경과(time series): 시간에 따른 값의 변화 추적 - 시간별 트래픽, 초당 요청 수 등


## metrics data collection methods

pull: 모니터링 시스템이 주기적으로 데이터 수집 - prometheus가 대상 애플리케이션에서 메트릭 수집

push: 대상 시스템이 모니터링 서버로 데이터 전송 - statsd, graphite


## metric collection tools

spring boot actuator: 스프링 부트 애플리케이션의 기본 메트릭 제공 - 요청 수, 응답 시간, 에러율 등

micrometer: 스프링 부트 애플리케이션의 메트릭 수집 라이브러리 - prometheus, grafana와 통합 가능

prometheus: 시계열(time series) 데이터 수집 및 저장 - pull 방식, grafana와 통합 가능

statsd: 애플리케이션에서 메트릭을 push 방식으로 수집 - graphite, datadog 등과 연동


## metric visualization tools

grafana: 실시간 대시보드 시각화 및 알림 설정 - prometheus, graphite, elasticsearch 등과 연동

kibana: elasticsearch 로그 및 메트릭 시각화 - elk stack

alertmanager: prometheus와 연동하여 알림 관리 - 임계값 초과 시 슬랙, 이메일 등 알림


## metric usecases

### application performance monitoring

spring boot actuator + prometheus + grafana

메트릭: 요청 수, 응답 시간, 에러율, cpu/메모리 사용량 등

활용 방법
- 응답 시간 분석: 히스토그램을 통해 지연 원인 파악
- 에러율 추적: 에러율 증가 시 alertmanager로 알림 전송

















