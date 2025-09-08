---
layout: default
title:
---

[standard annotations](#standard-annotations)

[spring testing annotations](#spring-testing-annotations)

[spring junit annotations](#spring-junit-annotations)


## standard annotations

스프링 테스트 환경에서 사용할 수 있는 기본 어노테이션

[상세 목록](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-standard.html)

@Autowired

@Qualifier

@Value

@PersistenceContext

@Transactional (일부 속성만 지원한다)


## spring testing annotations

스프링 테스트 환경에서만 사용할 수 있는 어노테이션

### @BootstrapWith

스프링 테스트 컨텍스트 프레임워크 부트스트랩을 설정하는 어노테이션

테스트 클래스에 `@BootstrapWith(CustomTestContextBootstrapper)` 형식으로 적용하여 사용할 수 있다  

스프링 부트에서 제공하는 @SpringBootTest 어노테이션은 `@BootstrapWith(SpringBootTestContextBootstrapper.class)`을 메타 어노테이션으로 선언하고 있다

### @ContextConfiguration

@ContextConfiguration 어노테이션은 통합 테스트에서 테스트 클래스에 적용하여 로드할 빈을 지정하는 등의 ApplicationContext 구성을 설정할 수 있다

각 속성을 통해 로드할 요소를 명시할 수 있다
- locations: 로드할 리소스 명시 (xml 또는 groovy 스크립트)
- classes: 로드할 컴포넌트 명시 (@Configuration, @Component 등)

```java
@ContextConfiguration(classes = SimpleBeanConfig.class)
class ApplicationContextConfigurationTest {
    
    @Test
    void test {
    }
}
```

### @TestPropertySource

통합 테스트 환경에서 프로퍼티 파일 위치 또는 프로퍼티들을 지정하기 위해 테스트 클래스에 적용할 수 있는 어노테이션

#### 프로퍼티 파일 위치 지정

test.properties 파일의 위치를 명시한다

```java
@ContextConfiguration
@TestPropertySource("/test.properties") 
class MyIntegrationTests {
}
```

#### 프로퍼티 지정

timezone과 port 프로퍼티를 명시한다

```java
@ContextConfiguration
@TestPropertySource(properties = { "timezone = GMT", "port: 4242" }) 
class MyIntegrationTests {
}
```

### @TestBean

애플리케이션 컨텍스트에 등록된 스프링 빈을 대체할 테스트용 빈이 필요할 때 사용하는 어노테이션

non-static 필드에 적용하고 테스트용 빈을 생성하는 메서드 이름을 지정한다 (또는 테스트용 빈 생성 메서드 이름을 스프링 빈 이름으로 지정한다)

```java
// enforceOverride를 활성화하면 대체할 빈이 없는 경우 테스트가 실패한다 
@TestBean(enforceOverride = true, methodName = "createFakeProductService")
ProductService productService;
```

#### 테스트용 객체 정의 및 빈 생성 메서드 구현

```java
static ProductService createFakeProductService() {
    return new FakeProductService();
}

static class FakeProductService extends ProductService {

    public FakeProductService() {
        super(null);
    }

    @Override
    public Product create(String name, int quantity, int amount) {
        System.out.println("가짜 ProductService 객체 create 메서드 호출");
        return null;
    }
}
```

### @MockitoBean, @MockitoSpyBean

스프링 빈을 대상으로 mocking/spying할 때 사용하는 어노테이션

적용한 스프링 빈 대신 가짜 객체를 주입하여 스텁/검증/캡처 작업을 수행할 수 있다

[자세한 내용](../../mockito/txt/mockito%20+%20spring%20boot%20test.md)

### @DirtiesContext



### @TestExecutionListeners

테스트 클래스, 하위 클래스, 중첩 클래스에 TestExecutionListener를 적용할 때 사용하는 어노테이션

[스프링에서 기본적으로 등록하는 TestExecutionListner 구현체](./spring%20test%20context%20framework.md#spring-testexecutionlistener)

```java
@TestExecutionListeners(LoggingTestExecutionListener.class)
@ExtendWith(SpringExtension.class)
public class TestExecutionListenerTest {
    
    @Test
    void contextLoads() {
    }
}
```


### @Commit, @Rollback

스프링은 테스트 환경에서 @Transactional을 적용한 경우 기본적으로 테스트 메서드의 실행 후 데이터베이스에 반영된 사항을 모두 롤백한다

테스트 클래스/메서드에 @Commit 또는 @Rollback(false)를 적용하여 변경사항을 실제로 데이터베이스에 반영할 수 있다 

### @BeforeTransaction, @AfterTransaction

@BeforeTransaction: 트랜잭션이 시작되기 전에 실행되어야 하는 void 메서드에 적용하는 어노테이션

@AfterTransaction: 트랜잭션이 끝나고 난 후 실행되어야 하는 void 메서드에 적용하는 어노테이션

### @Sql, @SqlConfig

@Sql: sql 스크립트를 실행하도록 테스트 클래스 또는 테스트 메서드에 적용하는 어노테이션

@SqlConfig: @Sql 어노테이션과 함께 사용되며 sql 스크립트 실행과 parse 설정을 지정하는 어노테이션


## spring junit annotations

### @SpringJUnitConfig, @SpringJUnitWebConfig

@SpringJUnitConfig: @ExtendWith(SpringExtension.class) + @ContextConfiguration

@SpringJUnitWebConfig: @ExtendWith(SpringExtension.class) + @ContextConfiguration + @WebAppConfiguration

### @TestConstructor

@Autowired를 명시하지 않고 생성자를 통해 의존성 주입을 받을 수 있는 어노테이션

```java
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TestConstructorAnnotationTest {

    private final ProductService productService;
    private final OrderService orderService;

    public TestConstructorAnnotationTest(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }
}
```

### @NestedTestConfiguration

중첩 테스트 클래스의 구성을 설정하는 어노테이션

중첩 테스트 클래스에 적용하지 않는 경우 부모 클래스 또는 상위 클래스의 구성을 상속받는다


### @EnabledIf, @DisabledIf

SpEL을 기반으로 테스트 클래스 또는 테스트 메서드의 조건부 실행을 지정하는 어노테이션

@EnabledIf: 참인 경우 테스트 실행

@DisabledIf: 참인 경우 테스트 실행 X

```java
@Test
@DisplayName("맥OS에서만 실행되는 테스트 - 스프링 @EnabledIf")
@EnabledIf(expression = "#{systemProperties['os.name'].toLowerCase().contains('mac')}",
        reason = "Enabled on Mac OS")
void runOnlyOnMacOs() {

}
```

메타 어노테이션으로 선언하여 커스텀 합성 어노테이션을 만들 수도 있다

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@EnabledIf(
	expression = "#{systemProperties['os.name'].toLowerCase().contains('mac')}",
	reason = "Enabled on Mac OS"
)
public @interface EnabledOnMac {}
```



