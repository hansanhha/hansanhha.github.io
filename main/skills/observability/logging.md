---
layout: default
title:
---

#### index
- [logging](#logging)
- [logging system components](#logging-system-components)
- [log level](#log-level)
- [log format](#log-format)
- [logging destination](#logging-destination)
- [logger vs console](#logger-vs-console)
- [log file management strategies](#log-file-management-strategies)
- [logging best practice](#logging-best-practice)


## logging

로깅은 소프트웨어가 실행되는 동안 발생하는 정보를 기록하는 작업을 말한다

기록되는 데이터를 로그라고 하며 이 데이터를 통해 시스템의 상태를 파악하고 오류를 분석하며 성능을 모니터링한다 (observability)

다음과 같은 목적으로 애플리케이션 내부에서 일어나는 일을 로그를 사용할 수 있다

### debugging, troubleshooting

특정 api에 호출 시 오류가 발생했다면 로그를 통해 원인을 분석한다

e.g) 결제 시스템에서 오류 발생 -> 로그를 조회하여 문제 발생 지점 확인

### auditing, security

보안 및 법적 규제 준수를 위해 로그 기록을 활용한다

e.g) 사용자 로그인 이력, 데이터 수정 이력 등

### performance monitoring

api 응답 시간이 느려지는 경우 로그를 통해 특정 요청에서 병목 현상이 있는지 확인한다

e.g) 데이터베이스 쿼리 시간의 로그를 확인하여 성능 확인


## logging system components

logger
- 로그를 생성하고 출력하는 컴포넌트 애플리케이션에서 로거를 통해 로그를 생성한다
- logback, log4j2 등

log forwarder
- 로그를 로그 분석 도구로 전송하는 컴포넌트
- promtail, filebeat, fluentd 등

log storage
- 생성된 로그를 저장하는 컴포넌트 (파일 시스템, 데이터베이스, 클라우드 스토리지 등)

log analysis tools
- 저장된 로그 데이터를 모니터링하거나 필요한 정보를 추출하기 위해 가공(집계/필터링/시각화), 분석, 검색 등의 작업을 수행하는 컴포넌트
- grafana loki, kibana, aws cloudwatch 등

alerting system
- 로그 데이터를 기반으로 애플리케이션의 문제 발생을 파악한 뒤 알리는 컴포넌트
- alertmanager 등


## log level

로그는 중요도(severity)에 따라 여러 레벨로 구분된다

모니터링 시스템이나 알림 시스템에서 로그 레벨을 통해 특정 로그만 필터링하거나 저장할 수 있다

주요 로그 레벨
- TRACE: 가장 상세한 로그
- DEBUG: 개발 및 디버깅을 위한 상세한 정보
- INFO: 일반적인 운영 정보 기록 (서비스 시작, 요청 처리 완료 등)
- WARN: 비정상적이지만 시스템에 치명적이지 않은 정보 (db 연결 지연 등)
- ERROR: 실행 중 발생한 오류 (시스템, 비즈니스 오류)
- FATAL: 시스템을 중단해야 할 심각한 오류


## log format

### unstructured(human-readable): simple text log

일반적인 로그는 사람이 읽기 쉬운 단순한 텍스트 포맷을 가진다

이 포맷은 로깅 시스템을 통해 검색, 필터링, 자동 분석 등의 기능을 활용하기 어렵다

```text
2025-03-01 10:00:00 INFO User login: userId=12345
2025-03-01 10:00:05 ERROR Database connection failed
```

### structured(machine-readable): json, key-value

structured 로깅은 기계가 읽을 수 있는 포맷으로 로그를 출력하는 방법을 말하며 로그 관리 시스템이 효율적인 검색 및 분석을 가능하게 한다

json, key-value 방식의 포맷을 가질 수 있으며 로그 관리 벤더에 따라 ecs (elastic common schema), logstash 포맷 등으로 나뉠 수 있다 

```json
{
  "timestamp": "2025-03-01T10:00:00Z",
  "level": "INFO",
  "message": "User login",
  "userId": "12345"
}
```


## logging destination

표준: 터미널에서 로그를 출력한다 (System.out.println())

파일: 로그를 파일로 저장한다 (/var/log/app.log)

로깅 시스템: grafana loki, elasticsearch, opentelemetry collector, aws cloudwatch 등과 같은 로깅 시스템에 로그 내용을 전달한다 (structured format)

데이터베이스: db에 로그를 저장하고, 로그 관리 시스템과 연동한다


## logger vs console

콘솔은 사용자와 컴퓨터가 상호작용할 수 있는 텍스트 기반 입출력 인터페이스를 말한다

일반적으로 운영체제나 개발환경에서 제공하는 CLI(터미널, 명령 프롬프트, IDE 터미널)를 콘솔이라고 말한다

로거와 콘솔을 사용하는 개발자의 입장에서 보면 문자열을 출력하는 동작을 수행하기 위해 참조하는 객체가 다를 뿐 둘의 차이가 크게 느껴지지 않을 수 있다

```java
// Hello Logger 로깅
logger.info("Hello Logger");

// 콘솔에 Hello Console 출력
System.out.println("Hello Console");
```

그러나 로거와 콘솔은 근본적으로 용도 자체가 다르다

콘솔은 터미널이라고도 하며, 앞서 설명했듯이 사용자가 컴퓨터에게 명령을 내리거나 컴퓨터가 처리한 결과값을 사용자에게 보여주기 위한 텍스트 기반의 입출력 인터페이스로써 사용된다

위의 `System.out.println` 메서드는 특정 문자열을 출력해달라고 운영 체제에게 요청하는 동작을 수행하며, 운영 체제는 전달받은 문자열을 터미널로 출력할 뿐이다

즉, 콘솔은 특정 운영 체제와 통신할 수 있는 도구에 불과하다

반면 로거는 애플리케이션을 모니터링하기 위한 도구로써 존재한다

데이터를 콘솔과 마찬가지로 터미널 창에 출력할 수 있을 뿐만 아니라, 네트워크를 통해 외부 모니터링 시스템이나 수집기에 전달하거나 데이터베이스에 저장할 수 있다

로거와 콘솔의 추가적인 차이점은 다음과 같다

#### log level

콘솔: 로그 수준 지원 X

로거: DEBUG, INFO, WARN, ERROR, TRACE 등의 로그 레벨을 통해 메시지 우선순위와 중요도 구분을 지원하여 외부 모니터링 시스템에서 필터링할 수 있도록 한다

#### formatting

콘솔: 수동으로 포맷을 작성해야 한다

로거: 날짜, 시간, 클래스명, 스레드 정보 등을 자동으로 포함하여 내용을 구성할 수 있다 (로그 패턴 활용)

#### performance

콘솔: 동기식으로 동작하며 대량 로그 시 성능 저하가 발생한다

로그: 비동기 로깅, 버퍼링 등으로 성능을 최적화할 수 있다

#### function expansion

콘솔: 확장 불가

로거: 다양한 로깅 프레임워크(slf4j, logback, log4j 등)와 통합하여 기능을 확장할 수 있다

#### maintenance

콘솔: 로그 내용을 유지보수하기 상당히 어렵다

로거: 구조화된 로그 기록으로 분석하거나 문제 원인을 찾는 등 유지보수하기 용이하다


## log file management strategies

파일에 로그 데이터를 저장하여 관리하는 방식을 사용하면 다음과 같은 사항을 고려할 필요가 있다
- 파일 저장 위치
- 로그 파일 크기 관리
- 로그 보관 기간
- 로그 회전(rotation) 및 압축 조건
- 파일 접근 제어

운영 환경에서 파일 기반 로깅을 효율적으로 관리할 수 있는 전략은 다음과 같다

### 로그 파일 회전, 압축 및 삭제

로테이션이란 특정 조건을 기준으로 새로운 로그 파일을 만들거나(로테이션) 기존의 로그 파일들을 압축 또는 삭제하여 파일 크기와 로그 저장소의 공간을 관리하는 방법을 말한다

일반적으로 다음과 같은 유형으로 나뉜다
- 용량 기반 로테이션: 로그 파일이 특정 크기에 도달하면 새 로그 파일을 생성하고 오래된 파일은 압축하거나 삭제하는 방식
- 시간 기반 로테이션: 일정 시간(매일, 매주, 매월)이 지나면 로그 파일을 새롭게 만드는 방식 - 시간 별로 로그를 다루면 일정한 간격으로 로그 파일을 분석하거나 유지보수할 수 있다
- 복합 로테이션: 용량과 시간 기준을 조합하여 로그 파일을 설정하는 방식

아래와 같은 설정으로 스프링 부트의 기본 로깅 프레임워크인 logback을 이용한 로그 파일 관리 전략을 수립할 수 있다 

### 로그 파일 분리

날짜별 로그를 저장하거나 INFO, ERROR 로그 레벨별로 파일을 구분하여 저장할 수 있다

날짜별 로그 저장: app-2025-03-01.log, app-2025-03-02.log

레벨별 로그 저장: info.log, error.log


## logging best practice

Observability에서 Logging이 가지는 한계

Observability에서는 로그뿐만 아니라 메트릭(Metrics)과 트레이스(Tracing)이 함께 사용되어야 함.
로그는 개별 이벤트 중심이므로 전체 흐름을 파악하기 어려움
고성능 분산 시스템에서는 로그만으로 병목 현상과 지연 시간을 분석하기 어려움
대량의 로그를 저장하고 분석하는 비용이 큼

따라서 Structured Logging, Centralized Logging, Correlation ID(MDC) 적용이 필수적


