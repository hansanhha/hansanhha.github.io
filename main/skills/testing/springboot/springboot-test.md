---
layout: default
title:
---

## Spring Context Caching

Spring TestContext Framework는 Test Suite(테스트 클래스)에 대한 ApplicationContext(or WebApplicationContext)를 로드한 뒤 static cache에 저장함

저장된 컨텍스트는 동일한 Test Suite내에서 동일한 Unique Context Configuration을 사용하는 모든 하위 테스트에서 재사용됨

또한 서로 다른 Test Suite들도 ApplicationContext에 대한 Configuration이 동일하다면 재사용됨

**ApplicationContext Unique Identifier**

ApplicationContext를 재사용할 수 있는 이유

ApplicationContext를 static cache에 저장할 때 Test Suite의 Configuration Parameter를 조합하여 생성된 고유 키를 사용함

테스트 클래스마다 Configuration이 다르지 않은 경우 동일한 키를 가지므로 캐시된 컨텍스트를 재사용함

Configuration Parameters
- location (from @ContextConfiguration)
- classes (from @ContextConfiguration)
- contextInitializerClasses (from @ContextConfiguration)
- activeProfiles (from @ActiveProfiles)
- propertySourceProperties (from @TestPropertySource)
- resourceBasePath (from @WebAppConfiguration)
- ...

## @SpringBootTest

springboot의 기능을 더한 spring-test의 @ContextConfiguration 대체 어노테이션

**ApplicationContext**
- 스프링 부트 애플리케이션은 Spring ApplicationContext이므로 일반 Spring Context에서 수행하는 것 외에 특별히 테스트할 필요가 없음
- SpringApplication을 사용해서 테스트에 사용할 ApplicationContext를 생성함

**WebEnvironment 속성**
- 테스트 시 임베디드 서버의 로드 여부를 결정하는 속성
- MOCK(default)
  - Web ApplicationContext 로드, Mock Web Environment 제공(실제 임베디드 서버 실행 X)
  - mock 기반 web application 테스트를 위한 @AutoConfigureMockMvc, @AutoConfigureWebTestClient 선언 가능
  - 테스트 클래스에 선언하면 통합 테스트에서 사용되는 MockMvc, WebTestClient(WebFlux 용)를 필드, 파라미터 등으로 주입받을 수 있음
  - classpath에서 웹 환경을 사용하지 않는 경우 non-web ApplicationContext으로 전이됨
- RANDOM_PORT
  - WebServerApplicationContext 로드, random port로 실제 임베디드 서버 실행
  - `@LocalServerPort`을 통해 실제 서버의 포트를 필드에 주입받아서 확인할 수 있음
- DEFINED_PORT
  - WebServerApplicationContext 로드, application.properties에 정의된 port(또는 기본 8080)로 실제 임베디드 서버 실행
- NONE
  - ApplicationContext 로드, 어떤 웹 환경도 제공하지 않음

**UseMainMethod 속성**

일반적으로 @SpringBootTest가 찾은 Test Configuraiton은 main @SpringBootApplication이 됨

@SpringBootApplication이 선언된 클래스는 main 메서드를 통해 SpringApplication.run()을 호출함

다만 @SpringBootTest는 main 메서드를 호출하지 않고 해당 클래스 자체를 사용하여 ApplicationContext를 생성함

main 메서드를 실행해야 될 경우 속성 값을 변경
- UseMainMethod.NEVER(default)
- UseMainMethod.ALWAYS
- UseMainMethod.WHEN_AVAILABLE

## TestConfiguration

스프링 부트의 `@*Test` 어노테이션은 테스트가 포함된 패키지부터 @SpringApplication 또는 @SpringBootConfiguration 어노테이션이 있는 패키지까지 찾음

따라서 스프링의 `@ContextConfiguraiton(classes=...)` 처럼 @Configuration을 로드하기 위한 설정이 필요하지 않음

별도의 Configuration이 필요한 경우 해당 테스트 클래스에 @TestConfiguration을 선언한 내부 클래스를 생성하여 사용

참고로 @TestConfiguration은 @Component가 포함된 복합 어노테이션이므로 컴포넌트 스캔의 대상이 됨

만약 컴포넌트 스캔 대상에서 제외하고 싶다면 아래처럼 @Import에 명시하면 됨

```java
@SpringBootTest
@Import(MyTestsConfiguration.class)
class MyTests {

	@Test
	void exampleTest() {
		// ...
	}

}
```

## Mocking, Spying Spring Beans

특정 스프링 빈을 mocking, spying할 수 있음

@MockBean : ApplicationContext Bean에 대한 Mockito Mock 정의
- 필드로 주입받는 경우 : 각 테스트 메서드 실행 이후 자동적으로 reset됨

@SpyBean : ApplicationContext Bean에 대한 Mockito Spy 정의
- spring의 proxy 처리가 된 Bean에 @SpyBean을 선언했을 때 프록시를 제거하고 싶은 경우
- given 또는 when절에 `AopTestUtils.getTargetObject(yourProxiedSpy)` 사용

**참고사항**

Spring은 테스트 간에 application context를 캐시하고, 동일한 configuration을 공유한 경우 재사용함

@MockBean과 @SpyBean은 cache key에 영향을 주기 때문에 context의 수를 늘릴 가능성이 높음

## Test With @Transactional

**@SpringBootTest의 WebEnvironment 속성에 따른 동작 방식 차이** 
- WebEnvironment.MOCK인 경우
  - 각 테스트 메서드 실행 후 트랜잭션 롤백
- WebEnvironment.RANDOM_PORT, WebEnvironment.DEFINED_PORT인 경우
  - 실제 임베디드 서버를 사용하므로 서로 다른 스레드에서 HTTP client와 Server가 실행됨
  - 따라서 이런 경우 트랜잭션이 분리되므로 서버에서 시작된 트랜잭션을 롤백을 할 수 없음

## Auto-configured Tests

특정 부분에 대한 테스트(slice test)만 가능하도록 자동 설정해주는 기능

`spring-boot-test-autoconfigure` 모듈에서 테스트 목적에 따른 어노테이션이 제공됨

각 어노테이션은 `@*Test`로 끝나며 ApplicationContext를 로드하고, 자동 설정을 위한 `@AutoConfigure...` 어노테이션이 하나 이상 포함됨

**참고사항**
- 각 slice에 맞는 컴포넌트와 auto-configuration class만 제한해서 스캔/로딩함
  - 해당 slice 어노테이션에서 제공하는 auto-configuration을 제외하려면 excludeAutoConfiguraiton 속성 또는 @ImportAutoConfiguration의 exclude 속성에 지정
- 여러 개의 slice 어노테이션 선언 불가능 -> 하나의 어노테이션만 선언하고, @AutoConfigure을 추가해야 됨
- slicing을 하지 않지만 auto-configured test bean이 필요한 경우
  - `@AutoConfigure...`을 별도로 선언
  - `@ImportAutoConfiguration({CertainAutoConfiguration.class, ...})` 선언
  - 클래스 이름은 `spring-boot-test-autoconfigure/META-INF/spring` 하위에 기능별로 나뉜 imports 파일에서 확인할 수 있음

## Auto-configured Spring MVC Tests

**@WebMvcTest**
- 스프링 MVC 컨트롤러 테스트 전용 어노테이션

**기능**
- MVC 인프라 자동 구성
- 컴포넌트 스캔 대상
  - @Controller, @ControllerAdvice, @JsonComponent
  - Converter, GenericConverter, Filter, HandlerInterceptor, HandlerMethodArgumentResolver
  - WebMvcConfigurer, WebMvcRegistrations
- 컴포넌트 스캔 제외 대상
  - @Component, @ConfigurationProperties
  - @EnableConfigurationProperties를 통해 @ConfigurationProperties 포함 가능
- Spring Security 의존성이 있는 경우
  - WebSecurityConfigurer 빈도 컴포넌트 스캔 대상에 포함시킴

**테스트 예시**
```java
@WebMvcTest(UserVehicleController.class)
class MyControllerTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserVehicleService userVehicleService;

  @Test
  void testExample() throws Exception {
    given(this.userVehicleService.getVehicleDetails("sboot"))
            .willReturn(new VehicleDetails("Honda", "Civic"));
    this.mvc.perform(get("/sboot/vehicle").accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("Honda Civic"));
  }

}
```

## MVC Testing With Spring Security

### Spring Security Test 어노테이션 공통사항

**SecurityContext 설정 시점 변경**

setupBefore 속성 기본 값이 TestExecutionEvent.TEST_METHOD이므로 SecurityContext는 TestExecutionListener.beforeTestMethod 이벤트 중에 설정되는데 JUnit의 @Before 이전에 발생함

JUnit의 @Before 이후(동시에 테스트 메서드 실행 전)로 설정해야 된다면 `@WithMockUser(setupBefore = TestExecutionEvent.TEST_EXECUTION)` 지정

### @WithMockUser

- 특정 유저로 가정하여 테스트할 수 있는 어노테이션
- username, password, role, authorities 지정 가능(필수 지정 X)
- SecurityContext에 UsernamePasswordAuthentication 타입의 Authentication이 채워짐
- Authentication의 principal은 Spring Security의 User 객체(기본적으로 username과 password를 가지고 ROLE_USER이라는 Single GrantedAuthority가 있음)

**@WithMockUser 테스트 예시(기본 값)**
```java
@WebMvcTest(UserController.class)
class MySecurityTests {

	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser
	void requestProtectedUrlWithUser() throws Exception {
		this.mvc.perform(get("/"));
	}

}
```

**@WithMockUser 테스트 예시(값 지정)**
```java
@Test
@WithMockUser(username="spring man", roles={"USER", "ADMIN"})
void requestProtectedUrlWithUser() throws Exception {
  this.mvc.perform(get("/"));
}
```

**@WithMockUser Class에 선언**

모든 테스트 메서드마다 지정된 유저를 사용함

JUnit5 @Nested 클래스를 감싼 상위 클래스에 선언하면 해당 테스트 클래스의 모든 하위 테스트 메서드에도 적용됨
```java
@WebMvcTest
@WithMockUser(username="admin",roles={"USER","ADMIN"})
public class WithMockUserTests {

  @Nested
  public class TestSuite1 {
    // ... all test methods use admin user
  }
}
```

### @WithAnonymousUser

Anonymous User로 가정하는 어노테이션

클래스에 @WithMockUser가 선언되어 있는 경우 메서드의 @WithAnonymousUser가 오버라이딩됨

```java
@WebMvcTest
@WithMockUser
public class WithUserClassLevelAuthenticationTests {

	@Test
	public void withMockUser() {
	}

	@Test
	@WithAnonymousUser
	public void anonymous() throws Exception {
		// override default to run as anonymous user
	}
}
```

### @WithUserDetails

커스텀 UserDetailsService(커스텀 타입과 UserDetails를 구현한 객체 반환)을 통해 테스트 유저를 생성할 때 사용하는 어노테이션

아래는 빈으로 등록된 UserDetailsService이 반환하는 user의 username을 prinpal로 갖는 UsernamePasswordAuthentcationToken 타입의 Authentication을 사용함
```java
@Test
@WithUserDetails
public void getMessageWithUserDetails() {
  String message = messageService.getMessage();
	...
}
```

지정된 username을 지정하여 UserDetailsService에서 조회할 수 있음
```java
@Test
@WithUserDetails("customUsername")
public void getMessageWithUserDetailsCustomUsername() {
	String message = messageService.getMessage();
	...
}
```

특정 UserDetailsService 빈을 조회하여 지정된 username을 조회할 수 있음
```java
@Test
@WithUserDetails(value="customUsername", userDetailsServiceBeanName="myUserDetailsService")
public void getMessageWithUserDetailsServiceBeanName() {
	String message = messageService.getMessage();
	...
}
```

**vs @WithMockUser**
- 테스트 클래스에 적용할 경우 @WithMockUser와 동일하게 동작
- @WithMockUser와 달리 실제로 user가 존재해야 됨

### @WithSecurityContext

테스트 시 Spring Security Test 대신 직접 SecurityContext를 설정하는 경우 사용하는 어노테이션

1. @WithSecurityContext를 사용한 커스텀 어노테이션 생성

@WithMockCustomUser는 @WithSecurityContext를 가지고 있는 메타 어노테이션임

@WithSecurityContext의 factory 속성 값에 SecurityCotext를 설정하는 클래스 지정
```java
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

  String username() default "rob";

  String name() default "Rob Winch";
}
```

2. SecurityContext 설정 클래스 구현

@Autowired을 통해 UserDetailsService를 주입받을 수도 있음

```java
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    
  private UserDetailsService userDetailsService;
  
  @Autowired
  public WithMockCustomUserSecurityContextFactory(UserDetailsService userDetailsService) {
      this.userDetailsService = userDetailsService;
  }
    
  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
  
    CustomUserDetails principal =
            new CustomUserDetails(customUser.name(), customUser.username());
    Authentication auth =
            UsernamePasswordAuthenticationToken.authenticated(principal, "password", principal.getAuthorities());
    context.setAuthentication(auth);
    return context;
  }
}
```

## Auto-configured Data JPA Tests

**@DataJpaTest**
- JPA 테스트 전용 어노테이션

**기능**
- Spring Data JPA Repository 구성
- classpath에서 임베디드 DB를 사용할 수 있는 경우 DB도 구성 
- 컴포넌트 스캔 대상
  - @Entity
- 컴포넌트 스캔 제외 대상
  - @Component, @ConfigurationProperties
  - @EnableConfigurationProperties를 통해 @ConfigurationProperties 포함 가능

**트랜잭션**
- 기본적으로 data JPA 테스트는 각 테스트 실행 후 롤백

**엔티티 매니저**
- Data JPA 테스트 시 표준 JPA EntityManager의 테스트용 TestEntityManager를 주입받을 수 있음
```java
@DataJpaTest
class MyRepositoryTests {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository repository;

	@Test
	void testExample() {
		this.entityManager.persist(new User("sboot", "1234"));
		User user = this.repository.findByUsername("sboot");
		assertThat(user.getUsername()).isEqualTo("sboot");
		assertThat(user.getEmployeeNumber()).isEqualTo("1234");
	}

}
```

**DB 설정**

`@AutoConfigureTestDatabase` 어노테이션 사용
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MyRepositoryTests {
	// ...
}
```

## Auto-configured REST Clients

**@RestClientTest**
- Rest Client 테스트 전용 어노테이션

**기능**
- Jackson, GSON, Jsonb 자동 구성
- RestTemplateBuilder, RestClient.Builder 구성
- MockRestServiceServer 추가
- 컴포넌트 스캔 제외 대상
  - @Component, @ConfigurationProperties
  - @EnableConfigurationProperties를 통해 @ConfigurationProperties 포함 가능

**빈 지정**
- @RestClientTest의 value 또는 components 속성에 지정

테스트에 사용되는 빈에서 RestClient.Builder를 사용하거나 rootUri 호출없이 RestTemplateBuilder를 사용하는 경우 전체 URI를 MockRestServiceServer의 expectations에 사용해야 됨

```java
@RestClientTest(RemoteVehicleDetailsService.class)
class MyRestClientServiceTests {

	@Autowired
	private RemoteVehicleDetailsService service;

	@Autowired
	private MockRestServiceServer server;

	@Test
	void getVehicleDetailsWhenResultIsSuccessShouldReturnDetails() {
		this.server.expect(requestTo("https://example.com/greet/details"))
			.andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
		String greeting = this.service.callRestService();
		assertThat(greeting).isEqualTo("hello");
	}

}
```

참고

[spring boot test docs](https://docs.spring.io/spring-boot/reference/testing/index.html)

[spring test context caching](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html)

[testing with spring security](https://docs.spring.io/spring-boot/how-to/testing.html#howto.testing.with-spring-security)