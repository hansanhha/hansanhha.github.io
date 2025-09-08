---
layout: default
title:
---

[extension model](#extension-model)

[extension points](#extension-points)

[extension point examples](#extension-point-examples)

[spring extension model](#spring-extension-model)

[spring TestExecutionListener](#spring-testexecutionlistener)

[mockito extension model](#mockito-extension-model)


## extension model

junit 5의 확장 모델은 junit 4의 @RunWith와 TestRule을 대체하여 @ExtendWith 또는 extension api를 통해 테스트 실행을 유연하게 커스터마이징할 수 있는 기능을 제공한다

확장 기능을 모듈화하여 테스트 실행 과정의 여러 단계에서 개입할 수 있도록한다

junit 5 확장 모델의 장점
- 유연한 테스트 실행: 실행 단계별로 개입할 수 있다 (BeforeAll, BeforeEach 등)
- 의존성 주입 지원: ParameterResolver를 통해 필요한 의존성을 동적으로 주입할 수 있다
- 커스텀 예외 처리: TestExecutionExceptionHandler로 예외를 커스텀 처리할 수 있다
- 테스트 인스턴스 관리: TestInstancePostProcessor로 테스트 인스턴스를 추가적으로 설정할 수 있다
- 확장 모델 조합: @ExtendWith, @RegisterExtension 등을 활용하여 확장 모델을 조합할 수 있다


## extension points

| 확장 포인트 인터페이스                          | 설명                              |
|---------------------------------------|---------------------------------|
| BeforeAllCallback                     | @BeforeAll 실행 전에 실행된다           |
| BeforeEachCallback                    | @BeforeEach 실행 전에 실행된다          |
| BeforeTestExecutionCallback           | 테스트 실행 직전에 실행된다                 |
| AfterTestExecutionCallback            | 테스트 실행 직후에 실행된다                 |
| AfterEachCallback                     | @AfterEach 실행 후에 실행된다           |
| AfterAllCallback                      | @AfterAll 실행 후에 실행된다            |
| TestInstancePostProcessor             | 테스트 인스턴스가 생성된 후 실행된다 (초기화 작업)   |
| TestExecutionExceptionHandler         | 테스트 실행 중 발생한 예외를 처리한다           |
| ParameterResolver                     | 테스트 메서드의 파라미터를 주입한다             |
| TestTemplateInvocationContextProvider | @TestTemplate를 활용한 동적 테스트를 생성한다 |


## extension point examples

#### TestInstancePostProcessor를 활용한 테스트 인스턴스 초기화

```java
public class GreetingTestInstancePostProcessor implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        if (testInstance.getClass().isAnnotationPresent(Greeting.class)) {
            System.out.println("detected @Greeting annotation by GreetingTestInstancePostProcessor");
        }
    }
}

@Greeting
@ExtendWith(GreetingTestInstancePostProcessor.class)
public class ExtensionModelTest {

    @Test
    void postProcessTest() {
    }
}
```


## spring extension model

spring boot가 제공하는 @SpringBootTest에는 `@ExtendWith(SpringExtension.class)` 어노테이션이 적용되어 있다

스프링은 테스트 환경에서 스프링 테스트 컨텍스트 프레임워크를 제공하는데 스프링의 SpringExtension을 활용해 junit jupiter와 통합한다

```java
// junit 5에서 spring 테스트 컨텍스트 확장 적용
@ExtendWith(SpringExtension.class) 

// 통합 테스트를 위해 스프링 컨텍스트 로드. 내부적으로 @ExtendWith(SpringExtension.class) 어노테이션이 선언되어 있다
@SpringBootTest
class SpringBootExtensionTest {

    @Autowired // 스프링 빈 주입
    private MyService myService; 

    @Test
    void testService() {
        String result = myService.process();
        System.out.println(result);
    }
}
```

## spring TestExecutionListener

junit 5 확장 모델과 별개로 스프링에서 제공하는 TestExecutionListener 인터페이스를 활용하여 특정 테스트 시점(테스트 실행 전/후, 테스트 클래스/인스턴스 구성)에 대한 콜백 작업을 수행할 수 있다

스프링 테스트 컨텍스트 프레임워크는 TestContextManager를 통해 TestContext를 관리하는데, TestContextManager는 각 테스트 시점에 TestExecutionListener를 호출한다

#### junit 5 extension model vs spring TestExecutionListener

junit 5와 스프링은 각각 특정 테스트 실행 시점에 개입할 수 있는 지점을 제공한다

junit 5 extension model
- 스프링 컨텍스트와 상관없이 테스트 실행 시점에 개입할 수 있다
- 스프링과 독립적인 junit 5 확장 기능이 필요할 때
- 특정 @Test 메서드에 파라미터 주입이 필요할 때

spring TestExecutionListener
- 스프링 컨텍스트와 관련된 설정이 필요할 때(트랜잭션, 데이터 초기화 등)
- 여러 개의 테스트 클래스에 공통된 스프링 관련 전역 설정이 필요할 때

#### 스프링 TestExecutionListener의 callback points

|메서드| 설명                                               |
|---|--------------------------------------------------|
|beforeTestClass| 클래스의 모든 테스트를 실행하기 전에 전처리를 수행한다                   |
|prepareTestInstance| 테스트 인스턴스의 테스트를 실행하기 전에 전처리를 수행한다 (의존성 주입 등)      |
|beforeTestMethod| 테스트 프레임워크의 라이프사이클 콜백(@BeforeEach 등) 전에 전처리를 수행한다 |
|beforeTestExecution| 테스트 메서드를 실행하기 전에 전처리를 수행한다                       |
|afterTestExecution| 테스트 메서드를 실행한 후에 후처리를 수행한다                        |
|afterTestMethod| 테스트 프레임워크의 라이프사이클 콜백(@AfterEach 등) 후에 후처리를 수행한다  |
|afterTestClass| 클래스의 모든 테스트를 실행한 후에 후처리를 수행한다                    |

#### 구현

TestExecutionListener 인터페이스를 구현하고, 테스트 클래스에 @TestExecutionListeners 어노테이션을 통해 적용할 수 있다 

```java
// 스프링 테스트 콜백 인터페이스 구현
public class SpringContextTestListener implements TestExecutionListener {

    @Override
    public void beforeTestExecution(TestContext testContext) {
        System.out.println("스프링 테스트 실행 전 초기화 작업 수행");
    }

    @Override
    public void afterTestExecution(TestContext testContext) {
        System.out.println("스프링 테스트 실행 후 정리 작업 수행");
    }
}
```

```java
@SpringBootTest
// 콜백 인터페이스 등록
@TestExecutionListeners(value = {SpringContextTestListener.class})
class SpringContextTest {

    @Test
    void test() {
        System.out.println("테스트 실행");
    }
}
```


## mockito extension model

mockito 라이브러리는 제공하는 MockitoExtension을 통해 @Mock과 @InjectMocks 등 mockito의 어노테이션 기능을 사용할 수 있다

```java
@ExtendWith(MockitoExtension.class) // mockito 확장 적용
class MockitoExtensionTest {

    @Mock // mock 객체 생성
    private DependencyService dependencyService; 

    @InjectMocks // mock 객체 의존성 주입
    private MyService myService; 

}
```


### mockito + spring boot test

스프링 컨텍스트에서 특정 빈을 모킹하려면 스프링 부트에서 제공하는 @MockBean(deprecated) 또는 스프링의 @MockitoBean 어노테이션을 적용하면 된다

```java

@SpringBootTest
class SpringBootMockitoTest {

    @MockBean // 스프링 빈 mocking 주입
    private DependencyService dependencyService;

    @Autowired // 실제 빈 주입
    private MyService myService; 

}
```
