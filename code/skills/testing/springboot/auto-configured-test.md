---
layout: default
title:
---

## auto-configured test

스프링 부트의 자동 구성 기능이 유용하지만 테스트 환경에선 불필요한 오버헤드가 될 수 있다

auto-configured test는 테스트 시 딱 필요한 부분(빈 등록 등)만 잘라내어 컨텍스트를 구성하는 방법이다

spring-boot-test-autoconfigure 모듈에서는 유사한 동작(웹 계층, 데이터베이스 계층 등)을 수행하는 것들끼리 묶어 ApplicationContext를 로드하는 `@...Test` 어노테이션(@WebMvcTest 등)을 제공한다

각 `@...Test` 어노테이션은 한 개 이상의 `@AutoConfigure...`(@AutoConfigureWebMvc 등) 어노테이션이 적용되어 있는데, 사용자가 필요한 경우 추가적으로 선언하여 테스트 환경의 자동 구성을 커스터마이징할 수 있다

#### 규칙

`@...Test` 어노테이션에 설정된 특정 자동 구성을 비활성화하고 싶은 경우
- excludeAutoConfiguration 속성을 사용하여 제외할 자동 구성을 명시한다
- @ImportAutoConfiguration의 exclude를 통해 명시한다

하나의 테스트에서 두 개 이상의 `@...Test`를 적용할 수 없다
- 하나만 선택하고 나머진 `@AutoConfigure...` 어노테이션을 추가해서 자동 구성을 활성화한다

@SpringBootTest와 여러 `@AutoConfigure...` 조합해서 사용할 수 있다
- 전체 애플리케이션 컨텍스트를 로드하면서 자동 구성된 테스트 빈이 필요한 경우 이 조합을 사용한다




