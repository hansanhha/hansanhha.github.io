---
layout: default
title:
---

[spring testcontext framework](#spring-testcontext-framework)

[architecture, workflow](#architecture-workflow)

[spring TestExecutionListener](#spring-testexecutionlistener)

[example for custom TestExecutionListener](#example-for-custom-testexecutionlistener)

[SpringExtension, @SpringJUnitConfig](#springextension-springjunitconfig)

[test execution events](#test-execution-events)

[context management](#context-management)

[transaction management](#transaction-management)

[dependency injection of test fixtures](#dependency-injection-of-test-fixtures)

[executing sql scripts](#executing-sql-scripts)

[parallel test execution](#parallel-test-execution)


## spring testcontext framework

스프링 부트 테스트는 스프링 TestContext 프레임워크를 기반으로 @SpringBootTest, @WebMvcTest, @DataJpaTest 등의 기능을 제공한다 

TestContext 프레임워크는 스프링 테스트(`org.springframework.test.context` 패키지)에서 제공하는 것으로는 사용 중인 테스트 프레임워크에 상관없이 어노테이션 기반 단위 테스트 및 통합 테스트를 지원한다

또한 junit 4/5, testng, mockito 등에 대한 명시적인 지원을 제공한다

#### 주요 기능

애플리케이션 컨텍스트 관리: 테스트 실행 시 컨텍스트를 생성하고 필요에 따라 캐싱한다

트랜잭션 관리: @Transactional을 통한 테스트 환경에서의 자동 롤백 등의 트랜잭션을 관리할 수 있는 기능을 제공한다

테스트 설정 지원: @ContextConfiguration, @TestPropertySource 등을 활용하여 테스트를 설정할 수 있는 기능을 제공한다

mocking 및 테스트 환경 제어: @MockitoBean, @MockitoSpyBean, @DirtiesContext를 활용하여 동적 컨텍스트를 구성할 수 있는 기능을 제공한다

커스터마이징 지원: junit 4 runner/rule, junit 5 extension, custom listener, custom loader를 설정할 수 있는 기능을 제공한다


## architecture, workflow

스프링 테스트 컨텍스트 프레임워크는 TestContextManager -> TestContext -> ApplicationContext 구조로 동작한다

#### 구성 요소

TestContextManager: 테스트 실행 시 TestContext를 생성/관리한다

TestContext: 현재 테스트 실행과 관련된 메타데이터(컨텍스트, 설정 정보 등)를 관리한다

TestExecutionListener: 테스트 생명주기에 따라 특정 이벤트를 실행하는 콜백 인터페이스

ApplicationContext: 스프링 IoC 컨테이너, 테스트 실행 시 생성된다

#### 동작 과정

테스트 실행
- junit @Test 실행 시 TestContextManager가 초기화된다

TestContext 생성 및 설정
- @ContextConfiguration 또는 @SpringBootTest 등의 어노테이션을 분석하여 설정 정보를 생성한다
- 분석한 정보를 바탕으로 ApplicationContext를 생성한다

TestExecutionListener 실행
- 테스트 실행의 생명 주기에 따라 TestExecutionListener의 메서드(beforeTestMethod 등)를 호출한다

테스트 실행
- @Test 테스트 메서드를 실행한다
- mocking, 트랜잭션 관리, 애플리케이션 컨텍스트 등을 활용한다

테스트 종료 및 정리
- @DirtiesContext 설정 여부에 따라 컨텍스트 재사용 여부를 결정한다
- 트랜잭션 롤백을 수행한다


## spring TestExecutionListener

스프링에서 기본적으로 제공하는 TestExecutionListener 구현체들은 다음과 같다

SpringExtension(junit jupiter 확장 모델)을 사용하면 이 구현체들이 자동으로 등록된다

#### DependencyInjectionTestExecutionListener

테스트 클래스에 스프링 빈 의존성 주입(@Autowired)을 하는 리스너

#### DirtiesContextBeforeModesTestExecutionListener

@DirtiesContext 설정을 처리하는 리스너

#### TransactionalTestExecutionListener

@Transactional을 적용하여 트랜잭션을 관리하는 리스너

#### SqlScriptsTestExecutionListener

@Sql을 실행하여 sql 스크립트를 적용하는 리스너


## example for custom TestExecutionListener

TestExecutionListener 인터페이스를 구현하고 테스트 클래스에 적용하면 해당 테스트 케이스의 생명 주기에 따라 콜백 메서드를 호출한다

아래의 LoggingTestExecutionListener는 테스트 메서드의 실행 전/후로 콘솔에 메시지를 출력한다

```java
public class LoggingTestExecutionListener implements TestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) {
        System.out.println(getCurrentTestCaseName(testContext) + ": test case executing");
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        System.out.println(getCurrentTestCaseName(testContext) + ": test case executed");
    }

    private String getCurrentTestCaseName(TestContext testContext) {
        String className = testContext.getTestClass().getSimpleName();
        String methodName = testContext.getTestMethod().getName();

        return className + "." + methodName;
    }

}
```

테스트 클래스에 구현한 리스너를 등록하여 리스너의 기능을 사용한다

```java
@TestExecutionListeners(LoggingTestExecutionListener.class)
@SpringBootTest
public class SimpleSpringBootTest {

    @Test
    void contextLoads() {

    }
}
```

```text
SimpleSpringBootTest.contextLoads: test case executing
SimpleSpringBootTest.contextLoads: test case executed
```


## SpringExtension, @SpringJUnitConfig

SpringExtension은 스프링 테스트 컨텍스트 프레임워크와 junit jupiter를 통합하는 역할을 하는 junit 5 extension model 구현체다

테스트 클래스에 `@ExtendWith(SpringExtension.class)` 어노테이션을 적용하여 활성화할 수 있으며 스프링 부트 테스트에서 제공하는 @SpringBootTest 어노테이션에는 메타 어노테이션으로 미리 적용되어 있다

주요 기능
- spring TestExecutionListener 등록 (TransactionalExecutionListener/SqlExecutionListener 등)
- ApplicationContext/WebApplicationContext 초기화 및 캐싱
- 의존성 주입: 테스트 생성자, 메서드 파라미터, 라이프사이클 콜백 메서드 파라미터 의존성 주입
- 트랜잭션 관리
- 이벤트 처리(application/test execution events)
- sql script 실행
- SpEL, 환경 변수, 시스템 프로퍼티 등을 기반으로 한 조건부 테스트 실행 (spring의 @EnabledIf, @DisabledIf)

### SpringExtension + @ContextConfiguration

@ContextConfiguration을 사용하여 애플리케이션 컨텍스트의 일부분만 구성하여 효율적인 테스트를 구성할 수 있다

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SimpleBeanConfig.class)
class testClass {
    
    @Test
    void test() {
        
    }
}
```

### @SpringJUnitConfig, @SpringJUnitWebConfig

스프링은 `@ExtendWith(SpringExtension.class)`과 `@ContextConfiguration`의 합성 어노테이션인 @SpringJUnitConfig와 @SpringJUnitWebConfig 어노테이션을 제공한다

```java
@SpringJUnitConfig(classes = SimpleBeanConfig.class)
class testClass {
    
    @Test
    void test() {
        
    }
}
```


## test execution events

테스트 실행 이벤트는 테스트의 생명주기와 관련된 이벤트로 junit이나 testng의 테스트 실행 시 특정 시점에 TestContextManager에 의해 트리거된다

TestExecutionListener들이 이 이벤트를 감지하여 의존성 주입, 트랜잭션 관리, sql 실행 등의 기능을 수행한다

|이벤트|발생 시점| TestExecutionListener |
|---|---|-----------------------|
|beforeTestClass|테스트 클래스 실행 전| 컨텍스트 초기화, mock설정      |
|prepareTestInstance|테스트 인스턴스 생성 후| 의존성 주입(@Autowired 처리) |
|beforeTestMethod|각 테스트 메서드 실행 전| 트랜잭션 시작, SQL 데이터 초기화  |
|afterTestMethod|각 테스트 메서드 실행 후| 트랜잭션 롤백, 로그 출력        |
|afterTestClass|테스트 클래스 실행 후| 컨텍스트 정리               |


## context management

테스트 컨텍스트 프레임워크가 ApplicationContext 또는 WebApplicationContext를 로드하면 캐싱하여 **동일한 test suite** 내에서 **고유한 컨텍스트 구성**을 선언한 모든 하위 테스트에서 재사용할 수 있게 한다

### 동일한 test suite

테스트 컨텍스트 프레임워크는 정적 캐시에 애플리케이션 컨텍스트를 저장한다 (말 그대로 static 변수에 애플리케이션 컨텍스트를 저장)

따라서 서로 다른 프로세스에서 테스트를 수행하거나 빌드 도구로 테스트들을 병렬 실행(fork)하는 경우 테스트 실행 간 정적 캐시가 삭제되므로 캐싱 기능을 사용할 수 없게 된다

캐싱 메커니즘을 효율적으로 사용하기 위해선 **모든 테스트가 같은 프로세스나 test suite에 있는 상태에서 테스트를 실행**해야 한다


### 고유한 컨텍스트 구성

각 ApplicationContext를 캐시하고 식별하기 위해 로드하기 위해 설정한 configuration 파라미터를 조합하여 고유한 키를 생성한다

[configuration parameters](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html)

### @DirtiesContext

@DirtiesContext를 테스트 클래스/메서드에 적용하면 테스트마다 컨텍스트를 초기화하여 새로운 컨텍스트를 로드한다


## transaction management

### 테스트 환경에서의 @Transactional 기본 동작

테스트 환경에서 @Transactional이 적용되면 TransactionalTestExecutionListener에 의해 기본적으로 테스트 메서드의 실행이 완료된 후 자동적으로 롤백을 수행한다

메서드에 선언한 경우 해당 메서드는 트랜잭션 범위에서 실행되며 메서드 실행 후 롤백을 수행한다

클래스에 선언한 경우 하위 메서드에 모두 적용된다

### 트랜잭션이 적용되지 않는 경우

@Transactional 어노테이션을 적용하지 않은 경우 당연히 트랜잭션이 적용되지 않는다

테스트 프레임워크에서 제공하는 라이프사이클 기능을 수행하는 메서드 중 테스트 클래스 또는 test suite 레벨에서 동작하는 메서드(@BeforeAll, @AfterAll 등)는 기본적으로 @Transactional을 적용해도 트랜잭션이 적용되지 않는다

[테스트 클래스 레벨에서 트랜잭션 적용하기](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/tx.html#testcontext-tx-enabling-transactions)

반면 @BeforeEach, @AfterEach와 같이 메서드 레벨의 라이프사이클 메서드에서는 트랜잭션이 적용된다

또한 @Trasactional 어노테이션을 테스트 메서드에 선언했으나 propagation 속성에 NOT_SUPPORTED 또는 NEVER 값을 설정한 경우에도 적용되지 않는다

### 테스트 환경에서 트랜잭션 커밋하기

스프링은 테스트 환경에서 기본적으로 테스트 메서드 실행 후 롤백을 수행하기 때문에 트랜잭션을 커밋하려면 테스트 클래스 또는 테스트 메서드 레벨에서 @Commit 어노테이션을 명시적으로 선언해주면 테스트 환경에서도 트랜잭션을 커밋할 수 있다


## dependency injection of test fixtures

[테스트 코드](../src/test/java/hansanhha/DependencyInjectionTest.java)

@Autowired 또는 @Inject 어노테이션을 필드/파라미터/생성자에 적용하여 애플리케이션 컨텍스트로부터 빈을 주입받을 수 있다

타입을 기준으로 주입하기 때문에 동일한 타입을 주입받을 필요가 있는 경우 @Qualifier를 이용하여 주입받을 각 의존성을 명시적으로 구분해야 한다

### SpringExtension 의존성 주입

스프링 부트의 @SpringBootTest가 기본적으로 사용하는 SpringExtension은 junit jupiter 환경에서 스프링이 지원하는 확장 모델로 @Autowired 또는 @Inject 어노테이션을 인식하여 의존성 주입을 할 수 있다

#### 필드 주입

필드에 의존성 객체와 @Autowired를 적용하여 주입받는 가장 기본적인 방법

```java
@Autowired
private OrderService orderService;
```

#### 생성자 주입

생성자에 @Autowired를 선언하여 생성자를 통해 의존성 주입을 받을 수 있다

```java
@Autowired
public DependencyInjectionTest(ProductService productService, ProductRepository productRepository) {
    this.productService = productService;
    this.productRepository = productRepository;
}
```

#### 메서드 파라미터 주입

메서드의 각 파라미터에 @Autowired를 선언하여 의존성을 주입받을 수 있다

```java
@Test
void methodParameterInjectionTest(@Autowired ProductService paramProductService) {
        Product product = paramProductService.create("test product", 10, 10_000);
        Assertions.assertThat(product).isNotNull();
}
```

#### 라이프사이클 메서드 파라미터 주입

@BeforeEach, @BeforeAll과 같은 라이프사이클 메서드의 각 파라미터에 @Autowired를 선언하여 의존성을 주입받을 수 있다

```java
@BeforeEach
void setUp(@Autowired ProductService lifecycleMethodInjection) {
    if (lifecycleMethodInjection == null) {
        throw new TestInstantiationException("라이프사이클 메서드 의존성 주입 실패");
    }

    System.out.println("라이프사이클 메서드(@BeforeEach) 의존성 주입 성공");
}
```


### 참고: junit jupiter를 제외한 다른 테스트 프레임워크

스프링은 junit jupiter를 제외한 다른 테스트 프레임워크의 테스트 클래스 초기화 과정에 관여하지 않는다

따라서 테스트 클래스의 생성자에 @Autowired 또는 @Inject를 적용해도 아무 효과가 발생하지 않는다


## executing sql scripts

[테스트 코드](../src/test/java/hansanhha/spring_extension/SqlScriptTest.java)

@Sql 어노테이션은 테스트 실행 전/후 특정 시점에 sql 스크립트를 실행하도록 지정한다

executionPhase 속성으로 실행 시점을 제어할 수 있다
- BEFORE_TEST_METHOD (기본값): 테스트 실행 직전 실행
- AFTER_TEST_METHOD: 테스트 실행 직후 실행
- BEFORE_TEST_CLASS: 테스트 클래스 실행 전 실행
- AFTER_TEST_CLASS: 테스트 클래스 실행 후 실행

[SqlScriptsTestExecutionListener](#spring-testexecutionlistener)가 @Sql 어노테이션을 인식하여 executionPhase에 지정된 값에 따라 sql 스크립트를 실행한다

@Transactional 어노테이션을 적용한 상태에서 sql 스크립트를 실행하면 스프링 테스트의 기본 동작에 따라 테스트가 완료되면 sql 스크립트의 반영사항이 롤백된다

하지만 @Transactional을 사용하지 않으면 sql 스크립트 실행 결과가 실제 db에 반영되므로 테스트가 끝나고 데이터가 유지된다

이 경우 `@Sql(scripts = "/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)`을 추가하여 테스트가 끝난 후 정리할 수 있다 

```java
@Sql(scripts = "classpath:product-data.sql")
```


## parallel test execution

[junit 5 테스트 병렬 실행](../../junit5/txt/parallel%20test.md)을 통해 테스트를 병렬로 실행할 수 있다

다만 애플리케이션 컨텍스트는 정적 캐시에 저장되므로 서로 다른 프로세스에서 실행되는 경우 캐싱 기능을 사용할 수 없으므로 어떤 방식이 테스트 성능을 개선할 수 있는지 파악하고 사용하는 것을 스프링에서 권장한다

