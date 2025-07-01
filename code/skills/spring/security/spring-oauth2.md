---
layout: default
title:
---

[Spring Security OAuth2 Authentication Workflow](#spring-security-oauth2-authentication-workflow)
- [Code Grant](#code-grant)

[OAuth2UserService.loadUser()부터 그 이후까지의 코드 흐름](#oauth2userserviceloaduser부터-그-이후까지의-코드-흐름)

[Spring Security OAuth2 Authentication Objects](#spring-security-oauth2-authentication-objects)

[인증 상태 저장/관리와 주의점](#인증-상태-저장관리와-주의점)

[OAuth2 로그인 과정에서 개발자가 개입하는 부분](#oauth2-로그인-과정에서-개발자가-개입하는-부분)

## Spring Security OAuth2 Authentication Workflow

### Code Grant

간략 요약
1. OAuth2AuthorizationRequestRedirectFilter: oauth2 provider 로그인 페이지 리다이렉트
2. OAuth2LoginAuthenticationFilter: oauth2 authorization code <-> access/refresh token 교환
3. AuthenticationSuccessHandler: oauth2 로그인 성공 후처리

상세 과정
1. `/oauth2/authorization/*` 요청 수신
2. OAuth2AuthorizationRequestRedirectFilter: oauth2 provider 로그인 페이지 리다이렉트
3. 사용자 로그인
4. oauth2 provider에서 code, state값을 담아 리다이렉트 `/login/oauth2/code/*`
4. `AbstractAuthenticationProcessingFilter.doFilter()` 리다이렉트 수신: `attemptAuthentication()` 템플릿 메서드 실행
5. `OAuth2LoginAuthenticationFilter.attemptAuthentication()`: authorization code <-> oauth2 provider access/refresh token 교환
   1. `AuthenticationManager(ProviderManager).authenticate()` 위임
   2. `OAuth2LoginAuthenticationProvider.authenticate()` 실행
      1. authorization code <-> oauth2 provider access/refresh token 교환
         - 수행: `OAuth2AuthorizationCodeAuthenticationProvider.authenticate()` 
         - OAuth2AccessTokenResponseClient (DefaultAuthorizationCodeTokenResponseClient) - state 값 검증, RestTemple 사용
      2. 교환한 access token을 기반으로 oauth2 provider resource server에게 사용자 정보 요청
         - `OAuth2UserService.loadUser()`
      3. access/refresh token과 사용자 정보를 담은 Authentication 객체 (OAuth2LoginAuthenticationToken) 반환
6. `OAuth2LoginAuthenticationFilter.attemptAuthentication()`: oauth2 로그인 성공 후처리
   1. OAuth2AuthorizedClient 저장 `OAuth2AuthorizedClientRepository.saveAuthorizedClient()`
   2. OAuth2LoginAuthentiationProvider에서 반환한 OAuth2LoginAuthenticationToken -> OAuth2AuthenticationToken으로 변환 후 반환 
7. `AbstractAuthenticationProcessingFilter.doFilter()`: 로그인 성공 후처리
   1. Authentication (OAuth2AuthenticationToken) 객체를 담아 인증 성공 핸들러 호출 `AuthenticationSuccessHandler.onAuthenticationSuccess()`

## OAuth2UserService.loadUser()부터 그 이후까지의 코드 흐름

`OAuth2LoginAuthenticationProvider`가 `OAuth2UserService.loadUser`를 호출하는 시점부터 

`OAuth2LoginAuthenticationFilter`가 `OAuth2AuthenticationToken`을 반환하기까지의 코드 흐름 

### Part 1. OAuth2UserService.loadUser()

`OAuth2LoginAuthenticationProvider` 객체는 `OAuth2AuthorizatoinCodeAuthenticationProvider`를 통해 authorization code와 access/refresh token 교환을 마치면

`OAuth2UserService`에게 사용자 정보를 가져오도록 위임함

OAuth2UserService는 access token을 통해 oauth2 resource server에게 end-user(resource user)의 정보를 요청함

스프링 시큐리티 oauth2 client는 이를 수행하는 DefaultOAuth2UserService를 제공하며 핵심 로직은 다음과 같음

```java
// OAuth2LoginAuthenticationProvider에서 OAuth2UserService 호출
// oauth2 provider 정보(ClientRegistration), access token을 담은 OAuth2UserRequest DTO 전달
OAuth2User oauth2User = this.userService.loadUser(new OAuth2UserRequest(
        loginAuthenticationToken.getClientRegistration(), accessToken, additionalParameters));

// DefaultOAuth2UserService.loadUser
@Override
public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    
    // ClientRegistration를 등록할 때 지정한 username 속성 이름 추출 (e.g user-name-attribute: id)
    String userNameAttributeName = getUserNameAttributeName(userRequest);
    
    // end-user 정보 조회 (RestTemple 사용)
    RequestEntity<?> request = this.requestEntityConverter.convert(userRequest);
    ResponseEntity<Map<String, Object>> response = getResponse(userRequest, request);

    // 세 가지 정보를 추출하여 DefalutOAuth2User 객체 반환
    // 1. oauth2 provider에서 제공한 access token에 담긴 권한: Collection<GrantedAuthority> authorities: 
    // 2. end-user 정보: Map<String, Object> attributes 
    // 3. username 속성 이름
    OAuth2AccessToken token = userRequest.getAccessToken();
    Map<String, Object> attributes = this.attributesConverter.convert(userRequest).convert(response.getBody());
    Collection<GrantedAuthority> authorities = getAuthorities(token, attributes);
    return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);
}
```

### Part 2. OAuth2LoginAuthenticationProvider

사용자 정보 요청이 성공하면 `OAuth2LoginAuthenticationProvider`는 반환된 `DefaultOAuth2User` 객체를 기반으로 `OAuth2LoginAuthenticationToken`을 반환함

```java
/* ---------------------------------------------------------------------- 
OAuth2UserService.loadUser() 이후의 OAuth2LoginAuthenticationProvider 로직 
   ---------------------------------------------------------------------- */

// Part 1에서 진행한 OAuth2UserService.loadUser()
OAuth2User oauth2User = this.userService.loadUser(new OAuth2UserRequest(
        loginAuthenticationToken.getClientRegistration(), accessToken, additionalParameters));

// end-user 권한 정보 추출
Collection<? extends GrantedAuthority> mappedAuthorities = this.authoritiesMapper
        .mapAuthorities(oauth2User.getAuthorities());

// 아래의 정보를 포함하여 OAuth2LoginAuthenticationToken 생성
// oauth2 provider
// authorization code 요청 및 결과 정보(code, state, error): loginAuthenticationToken.getAuthorizationExchange() 
// oauth2User: OAuth2UserService로부터 가져온 end-user 정보
// mappedAuthorities: end-user 권한
// accessToken, refresh token: oauth2 provider로부터 제공받은 access/refresh token
OAuth2LoginAuthenticationToken authenticationResult = new OAuth2LoginAuthenticationToken(
        loginAuthenticationToken.getClientRegistration(), loginAuthenticationToken.getAuthorizationExchange(),
        oauth2User, mappedAuthorities, accessToken, authorizationCodeAuthenticationToken.getRefreshToken());
authenticationResult.setDetails(loginAuthenticationToken.getDetails());

return authenticationResult;
```

### Part 3. OAuth2LoginAuthenticationFilter

`ProviderManager.authenticate()` `OAuth2LoginAuthenticationProvider` `OAuth2AuthorizationCodeAuthenticationProvider` `OAuth2UserService` 객체들이

모두 작업을 마치면 그 시작점이었던 `OAuth2LoginAuthenticationFilter`로 되돌아와서 나머지 로직을 수행함

1. oauth2 provider로부터 받은 access/refresh token, end-user prinipal(DefaultOAuth2User.getName()) 저장
- authorizedClientRepository를 통해 기본적으로 메모리에 저장함
- authorizedClientRepository 기본 주입 구현체: `AuthenticatedPrincipalOAuth2AuthorizedClientRepository`
- `AuthenticatedPrincipalOAuth2AuthorizedClientRepository` 기본 주입 구현체: `InMemoryOAuth2AuthorizedClientService`

2. OAuth2LoginAuthenticationToken -> OAuth2AuthenticationToken 변환 및 반환 

```java
/* -------------------------------------------------------------------------------------- 
OAuth2LoginAuthenticationProvider.authenticate() 이후의 OAuth2LoginAuthenticationFilter 로직 
   -------------------------------------------------------------------------------------- */

// Part 2에서 진행한 OAuth2LoginAuthenticationProvider.authenticate()
OAuth2LoginAuthenticationToken authenticationResult = (OAuth2LoginAuthenticationToken) this
			.getAuthenticationManager()
			.authenticate(authenticationRequest);

// authenticate()의 반환값인 OAuth2LoginAuthenticationToken을 OAuth2AuthenticationToken 변환
OAuth2AuthenticationToken oauth2Authentication = this.authenticationResultConverter
        .convert(authenticationResult);

oauth2Authentication.setDetails(authenticationDetails);

// OAuth2 로그인 성공 정보를 보관하는 OAuth2AuthorizedClient 객체 생성
// oauth2 provider, oauth2 provider로부터 제공받은 access/refresh token, end-user principal(DefaultOAuth2User.getName())
OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
        authenticationResult.getClientRegistration(), oauth2Authentication.getName(),
        authenticationResult.getAccessToken(), authenticationResult.getRefreshToken());

// OAuth2AuthorizedClient, OAuth2AuthenticationToken, filter의 request/response 저장
// authorizedClientRepository 기본 구현체: AuthenticatedPrincipalOAuth2AuthorizedClientRepository, InMemoryOAuth2AuthorizedClientService
this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, oauth2Authentication, request, response);

return oauth2Authentication;

// OAuth2LoginAuthenticationProvider에서 반환한 OAuth2LoginAuthenticationToken을 OAuth2AuthenticationToken으로 변환
// OAuth2User (DefaultOAuth2User): authenticationResult.getPrincipal(), end-user 정보
// end-user 권한: authenticationResult.getAuthorities()
// oauth2 provider id: authenticationResult.getClientRegistration().getRegistrationId(), e.g. kakao, naver 
private OAuth2AuthenticationToken createAuthenticationResult(OAuth2LoginAuthenticationToken authenticationResult) {
    return new OAuth2AuthenticationToken(authenticationResult.getPrincipal(), authenticationResult.getAuthorities(),
            authenticationResult.getClientRegistration().getRegistrationId());
}
```

## Spring Security OAuth2 Authentication Objects

### DefaultOAuth2User

oauth2 provider로부터 제공받은 end-user 정보를 보관하는 객체

`OAuth2LoginAuthentcationProvider`에게 호출된 `DefaultOAuth2UserService.loadUser()`에서 생성됨

이후 `OAuth2LoginAuthenticationProvider`는 `OAuth2LoginAuthenticationToken`에 DefaultOAuth2User를 담아서 `OAuth2LoginAuthenticationFilter`에게 반환함

```java
public class DefaultOAuth2User implements OAuth2User, Serializable {
    
    // oauth2 provider에서 제공한 access token에 담긴 권한 (end-user의 권한)
    private final Set<GrantedAuthority> authorities;

    // end-user에 대한 정보
    private final Map<String, Object> attributes;

    // end-user의 principal 키 이름
    private final String nameAttributeKey;

    
    // nameAttributeKey를 통해 end-user의 username을 반환함
    @Override
    public String getName() {
        return this.getAttribute(this.nameAttributeKey).toString();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}
```

DefaultOAuth2User의 상속관계는 다음과 같음

oauth2 provider마다 username 속성 이름값이 다르기 때문에 별도의 nameAttributeKey 필드를 가지며 

최상위 인터페이스인 AuthenticatedPrincipal의 `getName` 메서드에서 이 필드를 사용해 end-user의 principal을 반환함

```text
AuthenticatedPrincipal
-\ OAuth2AuthenticatedPrincipal
--\ OAuth2User
---\ DefaultOAuth2User
```

### OAuth2AuthenticationToken

OAuth2 인증 토큰을 나타내는 Authentication 객체

OAuth2 로그인 과정에서 사용되는 객체: `OAuth2LoginAuthenticationToken`

OAuth2 로그인 성공 시 사용되는 객체: `OAuth2AuthenticationToken`

OAuth2AuthenticationToken은 로그인에 성공했을 때 `OAuth2LoginAuthenticationFliter`에 의해 생성되며, 생성자에서 `setAuthenticated(true)`를 호출함

#### principal

OAuth2AuthenticationToken의 principal은 OAuth2User 타입으로, 기본적으로 주입되는 구현체는 위에서 알아본 [DefaultOAuth2User](#defaultoauth2user)임

#### client registration id

이 객체에서 oauth2 provider로부터 받은 access/refresh token을 저장하지 않고, OAuth2AuthorizedClient에서 별도로 저장함

OAuth2 principal(OAuth2User)과 OAuth2AuthorizedClient의 식별자를 연결시키기 위해 client registration id를 보관함

추후에 end-user의 보호된 리소스에 접근하고자 할 때 OAuth2AuthenticationToken(client registration id) -> OAuth2AuthorizedClient -> UserEndpoint(protected resources) 경로를 이용함

```java
public class OAuth2AuthenticationToken extends AbstractAuthenticationToken {
    
    private final OAuth2User principal;
    
    private String authorizedClientRegistrationId;
    
    public OAuth2AuthenticationToken(OAuth2User principal, Collection<? extends GrantedAuthority> authorities,
                                     String authorizedClientRegistrationId) {
        super(authorities);
        
        this.principal = principal;
        this.authorizedClientRegistrationId = authorizedClientRegistrationId;
        
        // 생성자에서 인증된 것을 마크함
        this.setAuthenticated(true);
    }


    // OAuth2User 반환(기본값 DefaultOAuth2User)
    @Override
    public OAuth2User getPrincipal() {
        return this.principal;
    }

    // OAuth2 사용자는 credentials를 노출하지 않음
    @Override
    public Object getCredentials() {
        return "";
    }

    // OAuth2User - OAuth2AuthorizedClient 연결
    public String getAuthorizedClientRegistrationId() {
        return this.authorizedClientRegistrationId;
    }
} 
```

OAuth2AuthenticationToken의 상속 관계는 다음과 같음

```text
Principal
-\ Authentication
--\ AbstractAuthenticationToken
---\ OAuth2AuthenticationToken
```

#### getPrincipal(), getName()

Authentication 인터페이스의 `getPrincipal()` 메서드는 인증되고 있거나 인증이 완료된 principal을 반환함

OAuth2AuthenticationToken은 이 메서드를 OAuth2User을 반환하는 것으로 오버라이딩함

Principal 인터페이스의 `getName()` 메서드는 해당 인증 객체의 식별자를 반환하는 메서드로 부모 클래스인 AbstractAuthenticationToken에서 처리하는데,

아래와 같이 `OAuth2User.getName()`의 메서드를 호출하여 OAuth2User의 식별자를 반환함

```java
@Override
public String getName() {
    if (this.getPrincipal() instanceof UserDetails userDetails) {
        return userDetails.getUsername();
    }
    
    // OAuth2User
    if (this.getPrincipal() instanceof AuthenticatedPrincipal authenticatedPrincipal) {
        return authenticatedPrincipal.getName();
    }
    
    if (this.getPrincipal() instanceof Principal principal) {
        return principal.getName();
    }
    return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
}
```

### OAuth2AuthorizedClient

OAuth2AuthorizedClient는 oauth2 클라이언트의 인증 상태를 관리하기 위한 객체임

**클라이언트**: end-user의 oauth2 provider로부터 oauth2 로그인을 요청하는 애플리케이션

end-user(resource owner)가 보호된 리소스 요청 접근할 수 있는 권한을 클라이언트에게 부여하면 인증된 것으로 간주하여 인증된 클라이언트라고 함

즉, authorization code 획득 및 authorization code <-> access/refresh token 교환 과정이 이루어지면 end-user의 접근 권한 부여가 이뤄진 것으로 봄

OAuth2AuthorizedClient는 oauth2 provider 및 oauth2 provider에서 제공한 access/refresh token과 principalName을 보관함

principalName값(`OAuth2User.getName()`) 과 OAuth2AuthenticationToken에서 보관 중인 client registration id를 통해 end-user 인증 상태와 클라이언트 인증 상태를 연결함

```java
public class OAuth2AuthorizedClient implements Serializable {
    
    private final ClientRegistration clientRegistration;

    private final String principalName;

    private final OAuth2AccessToken accessToken;

    private final OAuth2RefreshToken refreshToken;
}
```

## 인증 상태 저장/관리와 주의점

`OAuth2LoginAuthenticationFilter`는 OAuth2 로그인에 성공하면 end-user와 클라이언트의 인증 상태를 유지함

end-user 인증 상태: `OAuth2AuthenticationToken` (`OAuth2User`)

클라이언트 인증 상태: `OAuth2AuthorizedClient`

인증 상태 관리 담당: `OAuth2AuthorizedClientRepository`

`OAuth2LoginAuthenticationFilter`에게 주입되는 `OAuth2AuthorizedClientRepository` 구현체는 구성 역할을 하는 `OAuth2LoginConfigurer`에 의해 결정됨
- 사용자가 별도의 설정을 하지 않는 이상 `AuthenticatedPrincipalOAuth2AuthorizedClientRepository` 구현체를 주입함
- `AuthenticatedPrincipalOAuth2AuthorizedClientRepository`는 내부적으로 `OAuth2AuthorizedClientService`에게 인증 상태 로직을 위임함
- 스프링에서 제공하는 `OAuth2AuthorizedClientService` 구현체는 `InMemoryOAuth2AuthorizedClientService`와 `JdbcOAuth2AuthorizedClientService`이며, 기본적으로 `InMemoryOAuth2AuthorizedClientService`을 사용함

결과적으로 `OAuth2LoginAuthenticationFilter`는 OAuth2 로그인 인증 상태 객체들을 `InMemoryOAuth2AuthorizedClientService` 구현체를 통해 메모리에서 관리함

### 주의점

기본값으로 제공되는 `InMemoryOAuth2AuthorizedClientService` 구현체를 통해 인증 상태를 메모리에서 관리하면 다음과 같은 단점이 있음

#### 메모리 부담

당연하게도 많은 인증 상태 객체를 메모리에서 계속 유지해야 한다면 메모리에 부담이 되며, 애플리케이션 성능에 영향을 끼치게 됨

#### 애플리케이션 리부팅 시 인증 상태 삭제

애플리케이션을 리부팅하면 메모리에서 유지되고 있는 **인증 상태 객체들이 모조리 삭제됨**

따라서 인증 상태 정보를 통해 oauth2 resource server와 작업을 수행해야 하는 상황에서 애플리케이션 리부팅을 한다면 작업을 수행할 수 없게 됨

예를 들어 사용자가 oauth2 로그인을 하고 서비스를 이용하다가 로그아웃을 하려고 하는데 그 사이에 애플리케이션이 리부팅됐다고 가정해보면

메모리에서 관리하는 사용자의 인증 상태 객체들이 전부 삭제되서 resource server에게 클라이언트의 토큰을 삭제해달라는 요청이 정상적으로 수행되지 못하는 것으로 인해

서비스 애플리케이션 로그아웃 기능이 동작하지 않을 수 있음

#### 인증 상태 동기화 불균형

메모리에서 인증 상태를 관리하는 것은 마치 서버의 세션 관리와 동일하다고 볼 수 있음

메모리에서 관리되는 정보를 여러 애플리케이션 서버 간에 이용하려면 다음과 같은 선택지가 있음
- 클러스터링
  - 세션 복제 또는 sticky 세션 방법을 사용해서 애플리케이션 간 인증 상태 유지
  - 네트워크 지연 또는 성능 저하가 발생하거나, 특정 서버가 다운되면 인증 상태 손실이 발생할 수 있음
- jwt
  - 인증 상태를 jwt에 담아서 서버 간 동기화를 하지 않고도 여러 애플리케이션 간 이용
  - 인증 상태의 크기만큼 토큰이 커져서 네트워크 비용이 증가함
  - end-user와 authorized client 정보를 담은 jwt가 탈취되는 경우 보안 문제가 발생함

애플리케이션 메모리에서 인증 상태를 유지하는 경우 리소스 부담, 인증 상태 삭제, 동기화 불균형 등의 문제가 발생하게 됨

외부 저장소에서 인증 상태를 저장한다면 메모리 유지 방식의 문제를 해결할 수 있음 

## OAuth2 로그인 과정에서 개발자가 개입하는 부분

### ClientRegistration 등록

스프링 시큐리티가 oauth2 로그인 과정을 수행할 수 있도록 oauth2 provider에 대한 정보를 스프링 환경에 등록해야 됨 

```yml
spring:
  security:
    oauth2:
      client:
        registration:
          #oauth2-provider:
            client-id: 
            client-secret: 
            client-name: 
            redirect-uri: 
            client-authentication-method: 
            authorization-grant-type: 
            scope: 
        provider:
          #oauth2-provider:
            authorization-uri: 
            token-uri: 
            user-info-uri: 
            user-name-attribute: 
```

### DefaultOAuth2User attribute 추출

DefaultOAuth2UserService로 가져온 end-user 정보는 DefaultOAuth2User 객체의 attribute 필드에 담김

oauth2 provider마다 서로 다른 구조로 사용자 정보를 제공하므로 각 구조에 맞게 정보를 추출하는 역할을 가진 객체를 구현할 필요가 있음  

### OAuth2AuthorizedClientService 구현체 변경

`OAuth2LoginAuthenticationFilter`에 주입되는 `AuthenticatedPrincipalOAuth2AuthorizedClientRepository`는 내부적으로 `OAuth2AuthorizedClientService`에게 인증 상태 관리 로직을 위임함 

[메모리](#주의점)대신 외부 저장소에서 인증 상태를 유지하려면 `AuthenticatedPrincipalOAuth2AuthorizedClientRepository`에게 주입되는 `OAuth2AuthorizedClientService` 구현체를 변경해야 됨

#### 선택지 1. JdbcOAuth2AuthorizedClientService

스프링이 제공하는 `JdbcOAuth2AuthorizedClientService` 구현체는 [`OAuth2AuthorizizedClient`](#oauth2authorizedclient)의 필드 정보를 JdbcTemplate를 활용해 관리함

[DefaultOAuth2User](#defaultoauth2user)의 getName() 메서드 반환값과 `OAuth2AuthorizedClient`를 연관시켜 클라이언트의 인증 상태를 관리함 (end-user 인증 상태는 서비스 애플리케이션 차원에서 관리)

DB에 ClientRegistration를 저장할 때 client registration id만 저장하는데, 클라이언트의 인증 상태를 조회하여 `OAuth2AuthorizedClient` 객체로 매핑할 때 `JdbcOAuth2AuthorizedClientService` 구현체는 이 값을 기반으로 `ClientRegistrationRepository`를 통해 ClientRegistration을 동적으로 로드함

`ClientRegistrationRepository`의 기본 구현체는 `InMemoryClientRegistrationRepository`와 마찬가지로 메모리에서 oauth2 provider 정보를 유지함

하지만 oauth2 provider 정보는 인증 상태 메모리 방식과 달리 스프링 환경에 의해 고정적으로 관리되는 정보이기 때문에 애플리케이션 리부트에 의해 정보가 소실되지 않고, 동기화할 필요도 없으며 메모리 사용량이 매우 적기에 메모리에서 관리해도 무방함 

JdbcOAuth2AuthorizedClientService가 DB에서 관리하는 정보
- client registration id
- principal name
- access token type (Bearer)
- access token value
- access token issued_at
- access token expires_at
- access token scopes
- refresh token value
- refresh token issued_at

#### 선택지 2. Redis 기반 저장소 구현 

스프링 데이터 Redis를 활용하여 인메모리 데이터베이스에 OAuth2AuthorizedClient를 저장할 수 있음

RedisOAuth2AuthorizedClientService, RedisOAuth2AuthorizedClientRepository, AuthorizedClient 엔티티 구현

스프링 시큐리티 설정 및 레디스 설정

[Redis 기반 저장소 구현 코드]()

### OAuth2 Security 설정

스프링 시큐리티 `SecurityFilterChain` 빈을 등록할 때 OAuth2 로그인 및 클라이언트를 설정할 수 있음

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            
      // 사용자가 oauth2 provider를 통해 로그인하는 동작 정의
      .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer
              .loginProcessingUrl()    // oauth2 로그인 요청 엔드포인트, 기본값: /login/oauth2/code/*
              .failureUrl()            // 로그인 실패 시 리다이렉션할 url, 기본값: /login?error
              .failureHandler()        // 로그인 실패 시 호출할 핸들러
              .successHandler()        // 로그인 성공 시 호출할 핸들러
              .authorizationEndpoint(authorizationEndpointCustomizer -> authorizationEndpointCustomizer
                      // 인증 요청(authorization code 요청) 관련 설정
                      .baseUri() // 인증 요청 엔드포인트, 기본값: /oauth2/authorization/*
              ) 
              .tokenEndpoint(tokenEndpointCustomizer -> tokenEndpointCustomizer
                      // 토큰 교환 (authorization code <-> access token) 관련 설정
              )          
              .userInfoEndpoint(userInfoEndpointCustomizer -> userInfoEndpointCustomizer
                      // 사용자 정보 요청 관련 설정
                      .userService() // 사용자 정보 요청 객체
              )       
              .redirectionEndpoint(redirectionEndpointCustomizer -> redirectionEndpointCustomizer
                      // 인증 후 리다이렉션할 url, 기본값: /login/oauth2/code/*
                      .baseUri()
              )   
      )
      
      // oauth2 클라이언트로서 동작할 때 필요한 설정 구성
      .oauth2Client(oauth2ClientConfigurer -> oauth2ClientConfigurer
              .authorizationCodeGrant() // Authorization Code Grant 방식 설정 구성
              .clientRegistrationRepository() // ClientRegistrationRepository 설정, 기본값: InMemoryClientRegistrationRepository
              .authorizedClientService() // OAuth2AuthorizedClient 관리 서비스 설정
              .authorizedClientRepository() // OAuth2AuthorizedClient 관리 리포지토리 설정, authorizedClientService와 둘 중 하나만 설정하면 됨
      )
}
```