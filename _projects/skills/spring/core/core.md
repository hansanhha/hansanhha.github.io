---
layout: default
title:
---

spring 설계 철학(https://docs.spring.io/spring-framework/reference/overview.html#overview-philosophy)

1. 모든 레벨에서 선택권 제공
코드를 변경하지 않고도 configuration을 통해 persistence provider를 변경할 수 있음

2. 다양한 관점 수용
유연성을 수용하고 작업 방식에 대해 고집하지 않음

다양한 관점의 애플리케이션 요구 사항 지원

3. 강력한 하휘 호환성 유지
스프링 버전 간 큰 변화를 강요하지 않도록 관리됨

엄선된 범위의 jdk 버전과 third-party 라이브러리 지원

4. API 설계 관심
Spring 팀은 직관적이고 여러 버전과 여러 해에 걸쳐 유지되는 API를 만들기 위해 많은 고민과 시간을 투자함

5. 코드 품질에 대한 높은 표준 설정
Spring 프레임워크는 자바독에 중점을 둠

패키지 간에 순환 종속성이 없는 깔끔한 코드 구조를 주장할 수 있는 몇 안 되는 프로젝트 중 하나임

---

core 모듈 역할
- IoC(spring 애플리케이션 기반 형성)
    - 의존성 주입
    - 이벤트 처리
    - 리소스 관리 등
- 스프링 AOP(Aspect Oriented Programming, 공통 관심사 모듈화)
- PSA(Potable Service Abstractions, 일관된 서비스 추상화)

---

core 모듈 패키지

IoC 관련

org.springframework.context
- 이벤트 발행
- 리소스 로딩
- 메시지 처리(국제화 등)
- BeanFactory, ApplicationContext
- MessageSouree

org.springframework.beans
- Bean 생성, 생명주기 관리, 관계 설정 등
- BeanPostProcessor
- Autowired
- Value

org.springframework.core
- 의존성 주입 관련
- ParameterizedTypeReference
- ResolvableType
- SpringProperties

org.springframework.core.env
- 애플리케이션 환경 설정 관련
- Environment
- Profiles
- PropertySource
- PropertyResolver, PropertyParser

org.springframework.core.io
- 리소스 로딩 관련
- Resource
- ResourceLoader
- FileSystemResource
- ClassPathResource
- UrlResponse

스프링 AOP 관련

org.springframework.aop
- 스프링 AOP 관련 
- Advisor
- AopUtils
- MethodMatcher

PSA 관련

애플리케이션 서비스가 특정 기술에 종속되지 않도록 일관된 프로그래밍 모델 제공

여러 패키지에 걸쳐서 제공함

org.springframework.transaction
- 트랜잭션 관리 관련
- PlatformTransactionManager

org.springframework.jdbc/orm
- 데이터 접근 관련
- JdbcTemplate
- JpaTransactionManager

org.springframework.messaging
- 애플리케이션 간 메시지 기반 커뮤니케이션 추상화
- Message
- MessageChannel(JMS, AMQP, WebSocket)

org.springframework.web.servlet/reative
- 웹 개발 관련
- DispatcherServlet
- WebClient
