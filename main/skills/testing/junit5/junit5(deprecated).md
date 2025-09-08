## JUnit 5 Architecture

3개의 모듈로 구성

JUnit5 = JUnit Platform + JUnit Jupiter + Junit Vintage

Platform
- JVM에서 테스트 프레임워크를 실행하기 위한 기반 역할
- 이외에도 TestEngine API, Console Launcher, JUnit Platform Suite Engine 제공
- JUnit5 공식문서에서 Platform에 대한 2가지 개념을 정의함
  - Container : 테스트 클래스 - 다른 컨테이너나 테스트를 자식으로 갖고 있는 test tree node
  - Test : 테스트 메서드 - 실행 결과와 기댓값을 확인하는 test tree node

Jupiter
- JUnit5에서 테스트와 extension 작성을 위한 모델 제공(programming model, extension model)
- Jupiter 서브 프로젝트에서 TestEngine 제공(Platform에서 Jupiter 기반 테스트 실행 목적으로 사용)
- JUnit5 공식문서에서 Jupiter에 대한 3가지 개념을 정의함
  - Test Class(Container) : Test 메서드를 최소 1개 이상 가지고 있는 top-level class, static member class, @Nested class - 추상 클래스일 수 없고, 생성자가 하나만 있어야 됨
  - Lifecycle Method : @BeforeAll, @AfterAll, @BeforeEach, or @AfterEach이 선언된 메서드
  - Test Method : @Test, @RepeatedTest, @ParameterizedTest, @TestFactory, or @TestTemplate이 선언된 인스턴스 메서드
    - Lifecycle/Test Method는 public 접근 제어자와 반환 값을 가지지 않음
    - @Test를 제외한 나머지 어노테이션이 적용된 Test 메서드는 test tree에 container를 생성함

Vintage (하위 버전 호환)
- Platform에서 JUnit3, JUnit4 기반 테스트 실행을 위한 TestEngine 제공

## Test Instance Lifecycle

기본적으로 각 test class의 각 test method가 실행되기 전에 새로운 인스턴스를 생성함(per-method)
- 격리된 테스트 실행
- mutable 테스트 인스턴스 상태로 인한 사이드 이펙트 방지

per-class 테스트 인스턴스 생성
- @TestInstance(Lifecycle.PER_CLASS) 선언
- 인스턴스 메서드에 @BeforeAll, @AfterAll 선언이 가능해짐 

## Test Class, Test Method

```java
class StandardTests {

    @BeforeAll
    static void initAll() {
    }

    @BeforeEach
    void init() {
    }

    @Test
    void succeedingTest() {
    }

    @Test
    void failingTest() {
        fail("a failing test");
    }

    @Test
    @Disabled("for demonstration purposes")
    void skippedTest() {
        // not executed
    }

    @Test
    void abortedTest() {
        assumeTrue("abc".contains("Z"));
        fail("test should have been aborted");
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }

}
```

@Disabled : 클래스와 메서드에 선언 가능, 테스트를 실행하지 않음

## Tagging, Filtering

태그는 테스트를 마킹하고 필터링하는 목적으로 사용함

특징
- tag의 값이 null이거나 blank이면 안됨
- !, &, | 연산자를 사용할 수 있음 - 필터링

```java
@Tag("oauth2")
@Tag("dev")
class OAuth2DevTest {
    
}
```

## Assertions

- assertEquals
- assertNotEquals
- assertTrue
- assertFalse
- assertAll
- assertThrows
- assertTimeout
- assertTimeoutPreemptively

## Extensions

### 의존성 주입 Extensions

Junit을 사용해서 의존성 주입을 할 수 있지만 대부분의 경우 외부 Extension 사용

```java
@ExtendWith(Mockito.class)
@ExtendWith(SpringExtension.class)
class MyTest {
 
    @Mock
    private MyService myService;
 
    @Autowired
    private MyRepository myRepository;
 
    @Test
    void test() {
        // ...
    }
} 
```

## With Gradle

```groovy
// test case를 src/test/java 하위에 두고, gradle test 실행
dependencies {
    testImplementation('org.junit.jupiter:junit-jupiter-api:5.7.0')
    testRuntimeOnly('org.junit.jupiter:junit-jupiter-engine:5.7.0')
}

// JUnit Platform enable 
tasks.named('test', Test) {
  useJUnitPlatform()
}
```

## Junit5 Annotation, Composed Annotations

코어 어노테이션 위치 : junit-jupiter-api 모듈의 org.junit.jupiter.api 패키지

[Junit5 Annotation 종류](https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations)

JUnit5 어노테이션은 메타 어노테이션으로 사용 가능 -> 복합 어노테이션으로 재정의 

```java
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Tag("fast")
@Test
public @Interface FastTest() {
    
}
```

```java
@FastTest
void oneTest() {
    
}
```

## Test Execution Order

## Tests

### Nested Tests

### Repeated Tests

### Dynamic Tests

### Timeout

## Conditional Test Execution

## Test Interface, Default Method





