---
layout: default
title:
---

[architectures](#architectures)

[workflow](#workflow)


## architectures

junit 5는 junit 4와 다르게 모듈화된 아키텍처를 가진다

platform, jupiter, vintage라는 3개의 주요 컴포넌트로 구성되어 확장성이 뛰어나며 다양한 테스트 실행 환경을 지원한다

```text
+------------------------------------------------+
|               junit platform                   |
|   (test discovery, execution, reporting)       |
+------------------------------------------------+
            |                          |                  
+--------------------+      +--------------------+
|   junit jupiter   |       |   junit vintage    |
| (junit 5 tests)   |       | (junit 3/4 tests)  |
+--------------------+      +--------------------+
```

### junit platform 모듈

junit 5에서 테스트를 실행하기 위한 기반 플랫폼으로 테스트 엔진(jupiter, vintage)을 실행하고 테스트를 관리하는 역할을 한다

#### 주요 기능

test engine api 제공: junit, spock, cucumber 같은 다양한 테스트 프레임워크를 실행할 수 있도록 지원한다

test discovery, execution 관리: 실행할 테스트를 찾아 실행하고 결과를 리포팅한다

ide, build tool 통합 지원: intellij, eclipse, gradle, maven 같은 도구와 연동한다

동적 테스트 실행 지원: `@TestFactory`를 활용해 런타임에 테스트를 생성할 수 있게 한다

#### 주요 구성 요소

`Launcher`: 테스트 실행을 담당하는 핵심 인터페이스

`LauncherDiscoveryRequest`: 실행할 테스트를 검색하는 요청 객체

`TestEngine`: junit jupiter, junit vintage와 같은 테스트 엔진을 위한 인터페이스

`TestExecutionListener`: 테스트 실행 이벤트를 감지하는 리스너

#### 실행 과정

1. `LauncherDiscoveryRequest`를 통해 실행할 테스트 목록을 검색한다
2. `TestEngine`을 호출하여 테스트 실행한다
3. `TestExecutionListener`를 통해 실행 결과를 수집하고 리포팅한다

### junit jupiter 모듈

junit 5에서 새롭게 제공하는 테스트 모델로 테스트 api 및 실행 엔진을 담당하는 모듈이다

`@Test` `@BeforeEach` `@AfterEach`와 같은 junit 5의 기능을 정의한 핵심 모듈

#### 주요 기능

어노테이션 기반 테스트 제공: `@Test` `@Nested` `@ExtendWith` 등

동적 테스트(`@TestFactory`) 지원

확장 모델(extension api) 지원

라이프사이클 관리: `@TestInstance` `@BeforeEach` `@AfterEach`

#### 주요 구성 요소

`@Test`: 테스트 메서드 실행

`@BeforeEach` `@AfterEach`: 테스트 실행 전후 훅 제공

`@Nested`: 중첩 테스트

`@ExtendWith`: 확장 모델을 통한 추가 기능 적용

`TestInstance.Lifecycle.PER_CLASS`: 클래스 단위의 테스트 인스턴스 유지

#### 실행 과정

1. junit platform의 `Launcher`가 실행될 때 jupiter의 `TestEngine`이 감지된다
2. jupiter는 `TestDescriptor`를 활용해 테스트 클래스와 메서드를 검색한다
3. 테스트를 실행하고 platform의 `TestExecutionListener`를 통해 결과를 리포팅한다

### junit vintage 모듈

junit 3, 4로 작성된 기존 테스트 코드를 junit 5 환경에서 실행할 수 있도록 지원하는 모듈이다

#### 실행 과정

1. platform에서 junit vintage의 `TestEngine`을 로드한다
2. junit 3,4 기반의 테스트 클래스를 감지한다
3. junit 5 환경에서 실행할 수 있도록 래핑한다
4. 실행 결과를 platform의 `TestExecutionListener`를 통해 리포팅한다

### extension api

junit 5는 확장성을 넓히기 위해 기존 junit 4의 `@Rule` `@ClassRule`을 대체하는 확장 모델을 제공한다

[자세한 내용](./extension%20model.md)


## workflow

junit 5는 다음과 같은 단계를 거쳐 테스트를 실행한다

#### 1. 테스트 엔진 검색

ServiceLoader를 통해 junit 5 엔진(junit jupiter의 JupiterTestEngine)을 찾는다

junit 4 테스트를 실행하는 경우 junit vintage 엔진을 포함한다

#### 2. 테스트 탐색 (test discovery)

JupiterTestEngine은 TestDescriptor를 기반으로 테스트를 탐색한다

@Test, @TestFactory, @RepeatedTest, @ParameterizedTest 등을 식별하여 테스트 클래스와 메서드에 대한 TestDescriptor를 생성하고 계층 구조를 표현한다

이후 DiscoverySelector를 이용하여 실행할 테스트 클래스와 메서드를 수집한다

#### 3. 테스트 실행 (test execution)

각 TestDescriptor에 등록된 테스드들을 TestEngine이 실행하는데, TestInstanceFactory를 이용해 각 테스트 메서드마다 새로운 인스턴스를 생성한다

생성된 테스트 인스턴스에 대해 확장 모델(@ExtendWith) 및 라이프사이클 어노테이션(@BeforeEach 등)을 적용한다

테스트 어노테이션(@Test, @RepeatedTest 등)에 따라 테스트를 실행한다

참고
- 모든 테스트 인스턴스를 한꺼번에 생성한 후 실행하는 것이 아니라 각 테스트 메서드가 실행될 때마다 새로운 인스턴스를 생성한다
- 다만 @TestInstance(Lifecycle.PER_CLASS)를 사용하면 클래스 전체에서 하나의 인스턴스만 생성한다

#### 4. 결과 리포팅 (test reporting)

테스트 실행이 끝나면 TestExecutionListener를 통해 실행 결과를 빌드 도구(maven, gradle) 또는 ide에 전달한다

참고
- 테스트 실행이 끝나는 대로 리포팅이 이뤄진다
- 개별 테스트 실행 후 즉시 성공/실패 상태를 기록하고 전체 실행이 끝나면 최종 보고서를 생성한다



