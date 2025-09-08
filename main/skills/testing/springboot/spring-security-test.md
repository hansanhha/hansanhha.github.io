---
layout: default
title:
---

[@WebMvcTest + spring security test](#webmvctest--spring-security-test)

[testing method security](#testing-method-security)


## @WebMvcTest + spring security test

@WebMvcTest: 웹 계층 테스트 명시 

@Import: Security 관련 @Configuration 또는 @TestConfiguration 설정 (@WebMvcTest는 @Bean을 컴포넌트 대상에 포함시키지 않기 때문에 별도로 import 해야 한다)

mockMvc 설정: SecurityMockMvcConfigurers.springSecurity()를 통해 스프링 시큐리티의 FilterChainProxy를 필터로 등록

MockMvcTester 설정: 생성된 mockMvc를 기반으로 MockMvcTest 생성

```java
@WebMvcTest(AuthController.class)
@Import(SecurityTestConfig.class)
public class SpringSecuritySliceTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private MockMvcTester mvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        mvc = MockMvcTester.create(mockMvc);
    }
}
```


## testing method security

### @WithMockUser, @WithAnonymousUser

@WithMockUser
- 인증된 가짜 User 객체를 제공하는 어노테이션
- SecurityContext의 Authentication에 UsernamePasswordAuthenticationToken을 설정한다
- 기본 속성값: username="user", password="password", roles={"USER"}
- 테스트 클래스 또는 테스트 메서드에 적용할 수 있으며 테스트 클래스에 선언한 경우 @Nested 클래스에도 적용된다
- 사용자 객체를 필요로 하지 않기 때문에 어노테이션만으로도 인증된 사용자가 있다고 가정할 수 있다

```java
@Test
@WithMockUser(roles = "admin")
void mockAdminUserShouldAccessProtectedResource() {

    assertThat(mvc.get().uri("/auth"))
            .hasStatusOk();
}
```

@WithAnonymousUser
- 인증되지 않은 가짜 User 객체를 제공하는 어노테이션
- - 사용자 객체를 필요로 하지 않기 때문에 어노테이션만으로도 인증된 사용자가 있다고 가정할 수 있다

```java
@Test
@WithAnonymousUser
void normalUserCannotAccessProtectedResource() {

    assertThat(mvc.get().uri("/auth"))
            .hasStatus(HttpServletResponse.SC_FORBIDDEN);
}
```

### @WithUserDetails

스프링 시큐리티에서 제공해주는 타입이 아닌 커스텀 UserDetailService와 UserDetail 구현체를 통해 Authentication principal을 생성해야 되는 경우 사용하는 어노테이션

@WithMockDetails와 @WithSecurityContext 어노테이션은 @WithMockUser와 @WithAnonymousUser 어노테이션과 달리 실제 사용자 객체가 필요하다

@WithMockDetails는 속성값을 통해 username과 스프링 빈으로 등록된 UserDetailsService를 지정한다

설정된 username 값을 빈으로 등록된 UserDetailsService에게 전달하여 반환된 UserDetails 구현체를 SecurityContext Authentication에 설정한다

아래의 테스트 코드는 "test username" 이라는 username 값을 가진 사용자 객체를 simpleUserDetailsService라는 이름으로 등록된 UserDetailsService를 사용한다

```java
@Test
@WithUserDetails(value = "test username", userDetailsServiceBeanName = "simpleUserDetailsService")
@DisplayName("사용자 정보를 가진 사용자는 보호된 리소스에 접근할 수 있다")
void userDetailsUserShouldAccessProtectedResource() {

    assertThat(mvc.get().uri("/auth"))
            .hasStatusOk()
            .bodyText().contains("hello");
}
```


### @WithSecurityContext

SecurityContext를 직접 설정할 수 있는 어노테이션으로 @WithUserDetails보다 더 유연하다

이 어노테이션을 사용하기 위해선 총 3가지를 구현해야 한다
- SecurityContext 등록을 트리거하는 커스텀 어노테이션
- WithSecurityContextFactory 구현체
- 커스텀 Authentication (프로덕션 코드에서 사용하는 실제 Authentication 또는 테스트용)

[구현 코드](../src/test/java/hansanhha/slice/SecurityTestConfig.java)

```java
@Test
// 커스텀 어노테이션
@WithMockCustomUser(username = "test username", role = "admin")
@DisplayName("사용자 정보를 가진 사용자는 보호된 리소스에 접근할 수 있다")
void customSecurityContextUserShouldAccessProtectedResource() {

    assertThat(mvc.get().uri("/auth"))
            .hasStatusOk()
            .bodyText().contains("hello");
}
```

## testing oauth 2.0