---
layout: default
title:
---

[monitoring types](#monitoring-types)

[monitoring data collection methods](#monitoring-data-collection-methods)


## monitoring types

### infrastructure(performance, resource) monitoring

목적 및 용도
- 서버, 네트워크, 데이터베이스 등의 인프라 자원의 상태 및 성능 모니터링
- 서버 과부화 감지 및 자원 최적화 수행, 클라우드 인스턴스 상태를 추적 및 자동 확장(auto-scaling) 트리거
- 모니터링 대상: cpu 사용률, 메모리 사용량, 디스크 i/o, 네트워크 트래픽 등

도구: prometheus, grafana, zabbix, datadog

### application monitoring

목적 및 용도
- 애플리케이션의 성능 및 오류 상태 모니터링
- api 응답 속도 및 오류율 추적, 성능 병목 구간 파악
- 모니터링 대상: 응답 시간, 트랜잭션 속도, 오류율, 메서드 호출 시간 등

도구: new relic, spring boot actuator + prometheus

### logging/error monitoring, tracing

목적 및 용도
- 로그 데이터를 분석 및 시각화하여 문제 원인 분석 및 추적
- 예외 및 오류 발생 원인 분석, 비정상적인 활동(보안 침입 등) 탐지
- 모니터링 대상: 애플리케이션 로그, 서버 로그, 보안 로그

도구: elk stack, loki + grafana

### distributed tracing

목적 및 용도
- 마이크로서비스 아키텍처에서 서비스 간 호출 관계 및 성능 추적
- 마이크로서비스 간 호출 병목 파악, 트랜잭션 흐름 추적 및 성능 개선
- 모니터링 대상: 서비스 호출 흐름, 트랜잭션 경로, 응답 시간

도구: jaeger, zipkin, spring cloud sleuth

### security monitoring

목적 및 용도
- 보안 위협 탐지 및 대응
- 비정상 로그인 시도 탐지 및 알림, 실시간 보안 위협 분석 및 대응
- 모니터링 대상: 로그인 시도, 비정상적인 트래픽, 접근 제어 로그

도구: splunk, elk stack, wazuh


## monitoring data collection methods

### push monitoring

에이전트(애플리케이션 서버 등)가 데이터를 주기적으로 모니터링 서버로 전송하는 방식으로 서버의 부담을 줄이고 확장성을 늘릴 수 있다

다만 에이전트에서 오류가 발생하는 경우 데이터가 누락될 가능성이 있다

도구: prometheus pushgateway, logstash

### pull monitoring

모니터링 서버가 주기적으로 대상(애플리케이션 서버 등)에서 데이터를 가져오는 방식으로 중앙 집중 관리가 가능하고 대상 서버의 상태를 확인하기에 용이하다

대상 서버 수가 많으면 모니터링 서버의 성능이 저하될 수 있다

도구: prometheus, nagios