---
layout: default
title:
---

기본 개념
- [logging](#logging)
- [console](#console)
- [logging vs console (System.out.println)](#logging-vs-console-systemoutprintln)
- [로깅 시스템 구성 요소](#로깅-시스템-구성-요소)
- [자바의 로깅 생태계](#자바의-로깅-생태계)

[스프링 부트 로깅](#스프링-부트-로깅)
- [로그 파일 설정](#로그-파일-설정)
- [로그 로테이션 전략](#로그-파일-로테이션-전략)
- [스프링 부트 로그 파일 로테이션](#스프링-부트-로그-파일-로테이션)
- [로그 레벨 지정](#로그-레벨-지정)
- [로그 그룹](#로그-그룹)
- [로거 커스텀 설정](#로거-커스텀-설정)
- [구조화된 로깅 출력](#구조화된-로깅-출력)
- [logback-spring.xml example](#logback-springxml-example)
- [logback, log4j2 추가 설정](#logback-log4j2-추가-설정)

[컨테이너/클라우드 환경에서 로그 분석 도구를 통한 로그 데이터 활용](#컨테이너클라우드-환경에서-로그-분석-도구를-통한-로그-데이터-활용)


## logging

프로그램 실행 중 발생하는 정보를 기록하여 애플리케이션의 상태를 모니터링하거나 디버깅, 성능 분석 등을 수행할 수 있도록 돕는 작업을 말한다

### 로깅 주요 목표

#### 디버깅 및 문제해결

애플리케이션의 오류를 추적하고 원인을 분석하는 데 사용된다

#### 운영 상태 모니터링

애플리케이션이 정상적으로 동작하고 있는지 확인할 수 있다

#### 보안, 감사

중요한 이벤트(사용자 로그인 등)를 기록하여 보안을 강화하고 감사 로그를 제공한다

#### 성능 분석

실행 시간, 리소스 사용량을 기록하여 성능 병목을 파악할 수 있다


## console

사용자와 컴퓨터가 상호작용할 수 있는 텍스트 기반 입출력 인터페이스를 말한다

일반적으로 운영체제나 개발환경에서 제공하는 CLI를 콘솔이라고 말한다

터미널(terminal), 명령 프롬프트(command prompt), IDE 출력 창

```java
// Hello Console 출력은 오로지 콘솔 창에만 표시할 수 있다
System.out.println("Hello Console");
```


## logging vs console (System.out.println)

#### 출력 대상 차이

콘솔은 콘솔에만 내용을 출력할 수 있다 

로거는 콘솔, 파일, 데이터베이스, 원격 서버 등 다양한 출력 대상을 지원한다

#### 로그 수준 지원

콘솔은 로그 수준을 지원하지 않는다

로거는 로그 레벨(DEBUG, INFO, WARN, ERROR, TRACE)을 통해 메시지 우선순위와 중요도 구분을 지원한다

#### 자동 포맷팅

콘솔은 수동으로 포맷을 작성해야 한다

로거는 날짜, 시간, 클래스명, 스레드 정보 등을 자동으로 포함하여 내용을 구성할 수 있다

#### 성능 차이

콘솔은 동기식으로 동작하며 대량 로그 시 성능 저하가 발생한다

로그는 비동기 로깅, 버퍼링 등으로 성능을 최적화할 수 있다

#### 필터링

콘솔은 로그 내용을 필터링할 수 없다

로거는 특정 로그 레벨 또는 카테고리별로 로그를 필터링할 수 있다

#### 확장성

콘솔은 확장이 불가능하다

로거는 다양한 로깅 프레임워크(slf4j, logback, log4j 등)와 통합하여 기능을 확장할 수 있다

#### 유지보수성

콘솔로 로그 내용을 유지보수하기 상당히 어렵다

로거는 구조화된 로그 기록으로 분석하거나 문제 원인을 찾는 등 유지보수하기 용이하다


## 로깅 시스템 구성 요소

#### 로거

로그를 생성하고 출력하는 컴포넌트

애플리케이션은 로거를 통해 로그를 생성한다

#### 로그 저장소

생성된 로그를 저장하는 컴포넌트

파일 시스템, 데이터베이스, 클라우드 스토리지 등

### 로그 분석 도구

저장된 로그 데이터를 기반으로 가공(집계/필터링/시각화), 분석, 검색 등의 작업을 수행하는 컴포넌트

로그 데이터로부터 애플리케이션의 상태를 모니터링하거나 필요한 정보를 추출한다

#### 알림 시스템

로그 데이터를 기반으로 애플리케이션의 문제 발생을 파악하고 관리자에게 알리는 컴포넌트 


## 자바의 로깅 생태계

자바의 로깅 생태계는 크게 로깅 API와 로깅 구현체로 나뉜다

로깅 API: 애플리케이션에서 사용할 로깅 인터페이스, 특정 로깅 구현체에 의존하지 않고 독립적으로 로깅을 처리할 수 있도록 설계된다

로깅 구현체: 실제로 로그를 기록하는 역할, 다양한 로깅 API와 통합될 수 있다

### 로깅 API

#### java.util.logging (jul)

자바 표준 api로 제공되는 기본 로깅 api이자 구현체

간단하지만 기능이 제한적이다

#### slf4j (simple logging facade for java )

대표적인 자바 로깅 추상화 계층

스프링 부트가 기본적으로 사용하는 로깅 api

#### commons logging (jakarta commons logging)

apache 프로젝트에서 제공하는 추상화 계층

### 로깅 구현체

#### lobback

slf4j의 기본 구현체

스프링 부트가 기본적으로 사용하는 로깅 구현체

#### log4j, log4j2

#### tinylog


## 스프링 부트 로깅

스프링 부트는 기본적으로 slf4j api와 logback 구현체를 사용하며 **콘솔에만 로그를 출력한다**

### 로그 파일 설정

콘솔과 더불어 로깅 파일에도 로그 메시지를 출력하려면 `logging.file.name` 또는 `logging.file.path` 프로퍼티를 설정해야 한다

아무 설정을 하지 않으면 스프링 부트는 기본적으로 콘솔에만 로그 메시지를 출력한다

| logging.file.name | logging.file.path | 설정                                                                      |
|-------------------|-------------------|-------------------------------------------------------------------------|
| none              | none              | 콘솔에만 로그 내용 출력                                                           |
| 특정 파일(my.log)     | none              | 현재 디렉토리를 기준으로 logging.file.name에 명시한 위치(절대/상대)의 파일에 로그 내용 작성            |
| none              | 특정 디렉토리(/var/log) | 현재 디렉토리를 기준으로 logging.file.path에 명시한 위치(절대/상대)의 spring.log 파일에 로그 내용 작성 |
| 특정 파일             | 특정 디렉토리           | logging.file.name 프로퍼티만 적용                                              |

### 로그 파일 로테이션 전략

하나의 로그 파일에만 애플리케이션의 모든 로그 메시지를 누적하면 파일 내용의 크기가 커져서 관리하기가 어려워질 것이다

효율적으로 관리하기 위해 로그 파일을 로테이션하여 관리하는 데 일반적으로 적용하는 기준은 다음과 같다

#### 용량 기반 로테이션

로그 파일이 특정 크기에 도달하면 새 로그 파일을 생성하는 방식

오래된 파일은 압축하거나 삭제할 수 있다

e.g app.log 파일이 10mb에 도달하면 app.1og.1로 이름을 변경하고 새로운 app.log 파일을 생성한다

#### 시간 기반 로테이션

일정 시간(매일, 매주, 매월)이 지나면 로그 파일을 새로 만드는 방식

로그를 시간별로 관리하기 쉽고, 일정한 간격으로 유지보수 및 분석이 가능하다

e.g 매일 자정에 app.log 파일을 app-YYYY-MM-DD.log로 변경하고 새 app.log 파일을 생성한다

#### 복합 로테이션

용량과 시간 기준을 조합하여 로그 파일을 설정하는 방식

e.g 로그 파일이 10mb에 도달하거나 하루가 지나면 새 로그 파일을 생성한다

### 스프링 부트 로그 파일 로테이션

스프링 부트는 logback과 관련된 파일 로테이션 설정을 지원한다

다른 로깅 구현체를 사용하는 경우 로테이션 설정을 직접해야 한다 (log4j2.xml, log4j2-spring.xml 등)

스프링 부트에서 지원하는 logback 파일 로테이션 프로퍼티

```yaml
logging:
  logback:
    rollingpolicy:
      file-name-pattern: # 아카이브할 로그 파일 이름 패턴 (기본값: ${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz)
      clean-history-on-start: # 애플리케이션 시작 시 아카이브된 로그 파일 삭제 여부
      max-file-size: # 최대 로그 파일 사이즈 (용량 기반 로테이션, 기본값: 10MB)
      total-size-cap: # 아카이브할 최대 로그 파일 사이즈 (넘치면 아카이브된 로그 파일 삭제, 기본값: 0B)
      max-history: # 아카이브할 최대 기간 (기본값: 일주일) 
```

### 로그 레벨 지정

애플리케이션 전역 또는 특정 패키지에서 적용될 로그 레벨을 지정할 수 있다

```yaml
logging:
  level:
    root: info # 기본 전역 로그 레벨
    org.springframework.web: debug # 스프링 web 패키지 로그 레벨
    hansanhha:  # 애플리케이션 패키지 로그 레벨
      logging: debug
```

### 로그 그룹

비슷한 기능을 수행하는 패키지를 그룹으로 묶어서 한 번에 로그 레벨을 설정할 수 있다

```yaml
logging:
  group:
    custom-group: org.springframework.boot.autoconfigure.logging, hansanhha.logging

  level:
    custom-group: debug
```

#### 스프링 부트가 미리 설정한 로그 그룹

"web" 로거 그룹
- org.springframework.core.codec
- org.springframework.http
- org.springframework.web
- org.springframework.boot.actuate.endpoint.web
- org.springframework.boot.web.servlet.ServletContextInitializerBeans

"sql" 로거 그룹
- org.springframework.jdbc.core
- org.hibernate.SQL
- LoggerListener

### 로거 커스텀 설정

[상세 내용](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.custom-log-configuration)

상세한 locback 설정이 필요하거나 다른 로그 구현체를 사용하는 경우 별도의 구성 파일을 스프링 부트에게 전달해야 한다

루트 클래스 패스 또는 `logging.config` 프로퍼티에 구성 파일을 명시하면 된다

logback
- logback-spring.xml
- logback-spring.groovy

log4j2
- log4j2-spring.xml

### 구조화된 로깅 출력

구조화된 로깅은 사람이 읽을 수 있을 뿐만 아니라 기계가 읽을 수 있는 형식으로 로그 출력을 기록하는 기술이다

스프링 부트는 구조화된 로깅과 함께 다음 json 형식을 지원한다
- elastic common schema (ecs)
- graylog extended log format (gelf)
- logstash

구조화된 로깅을 사용하려면 `logging.structured.format.console` (콘솔) 또는 `logging.structured.format.file` (파일) 설정에 사용할 포맷 id를 명시하면 된다

커스텀 로그 설정을 사용하는 경우 [참고](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.structured)

json 로그 출력을 커스텀할 경우 [참고](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.structured.customizing-json)

#### elastic common schema (ecs)

```yaml
logging.structured.format.console=ecs
logging.structured.format.file=ecs
```

#### graylog extended log format (gelf)

```yaml
logging.structured.format.console=gelf
logging.structured.format.file=gelf
```

#### logstash

```yaml
logging.structured.format.console=logstash
logging.structured.format.file=logstash
```


### logback-spring.xml example

```xml
<configuration>

    <!-- 콘솔 로그 설정 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%level] [%thread] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 로그 파일 설정 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <!--  로그 파일 로테이션 설정      -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern> <!-- 하루마다 새로운 로그 파일 생성 -->
            <maxHistory>30</maxHistory> <!-- 30일 동안 로그 보관 -->
            <totalSizeCap>100MB</totalSizeCap> <!-- 10MB 이상이면 압축하여 저장 -->
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%level] [%thread] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 루트 로거 설정 -->
    <root level="info">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### logback, log4j2 추가 설정

[logback 추가 설정](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.logback-extensions)

[log4j2 추가 설정](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.log4j2-extensions)


## 컨테이너/클라우드 환경에서 로그 분석 도구를 통한 로그 데이터 활용

컨테이너 기반, 클라우드 기반 환경에서는 기본적으로 로그 데이터를 수집하고 활용하기 위해 **콘솔 표준 출력/표준 에러**를 사용한다

### 콘솔 표준 출력, 표준 에러를 사용하는 이유

#### 1. 컨테이너의 특성

컨테이너 특성 상 다른 컨테이너와 호스트 os로부터 격리된 가상 환경에서 애플리케이션을 실행한다

컨테이너의 파일 시스템은 일시적이므로 컨테이너가 종료되면 로그도 같이 삭제되기 때문에 로그를 파일로 남기면 추적이 어려워진다

따라서 모든 로그는 기본적으로 stdout 및 stderr를 통해 출력된다

#### 2. 로그 수집 및 통합 도구의 호환성

elk, fluentd, promptail 같은 로그 수집 도구들은 표준 출력을 통해 로그를 수집하는 데 최적화되어 있다

클라우드 플랫폼(aws, azure, gcp) 역시 표준 출력 로그를 자동으로 수집하고 저장할 수 있도록 지원한다

#### 3. 유연한 로그 데이터 처리

표준 출력으로 로그를 출력하면 로그 데이터를 중앙화된 수집 시스템으로 쉽게 전송할 수 있다

로그 파일 관리(로테이션, 백업 등)를 애플리케이션에서 처리하지 않아도 된다

### 클라우드 네이티브 환경에서 로그 활용 방식

#### 표준 출력 및 표준 에러 사용

애플리케이션 로그는 stdout, stderr로 출력된다

컨테이너 런타임(kubernetes, docker)이 로그를 수집한 뒤 로그 관리 도구로 전달한다  

#### 로그 수집 도구 활용

elk 스택 등 활용
- logstash: 애플리케이션 로그 데이터 수집 및 처리
- elasticsearch: 처리된 데이터 저장
- kibana: elasticsearch에 저장된 데이터를 기반으로 시각화, 검색

#### 클라우드 플랫폼 제공 도구 활용

aws cloudwatch logs

google cloud logging

azure monitor






