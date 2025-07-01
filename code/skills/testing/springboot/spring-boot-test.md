---
layout: default
title:
---

[spring boot test](#spring-boot-test)

[spring boot starter test](#spring-boot-starter-test)

[spring boot test autoconfiguration](#spring-boot-test-autoconfiguration)


## spring boot test

스프링 부트 테스트는 [spring testcontext framework](./spring%20test%20context%20framework.md)를 기반으로 동작하며 애플리케이션 컨텍스트를 관리하고 자동 설정을 활용하여 테스트를 쉽게 수행할 수 있도록 지원하는 모듈이다

#### spring boot test module

스프링 부트 테스트는 크게 두 가지 모듈로 구성된다

spring-boot-test: 스프링 부트 테스트 코어 모듈

spring-boot-test-autoconfigure: 스프링 부트 테스트 자동 설정 모듈


## spring boot starter test

스프링 부트에서 제공하는 테스트 스타터를 의존성으로 추가하면 아래의 의존성을 함께 가져온다

spring test, spring boot, junit5, assertj, hamcrest, mockito, jsonassert, jsonpath, awaitility, xmlunit

```kotlin
dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```


## spring boot test autoconfiguration

스프링 부트의 자동 구성 기능은 기본적으로 테스트 환경에서도 동일하게 적용되는데 효율적인/명시적인 테스트 실행을 위해 특정 어노테이션을 적용하여 필요한 자동 구성만 수행할 수 있다

@SpringBootTest: 스프링 부트의 전체 컨텍스트 로딩, 모든 autoconfiguration가 활성화되며 실제 애플리케이션과 동일한 환경에서 테스트를 수행한다

@WebMvcTest: 스프링 web mvc와 관련된 빈(필터, 컨트롤러 등)만 컨텍스트에 로딩한다

@DataJpaTest: jpa와 관련된 빈만 컨텍스트에 로딩하며 실제 데이터베이스 대신 임베디드 데이터베이스(h2 등)를 사용한다

@TestConfiguration: 테스트 환경에서 추가적인 빈을 정의하거나 커스터마이징이 필요한 경우 사용한다


