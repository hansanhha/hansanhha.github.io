---
layout: default
title:
---

[web layer test](#web-layer-test)

[component scan target list](#component-scan-target-list)

[MockMvc](#mockmvc)

[MockMvcTester](#mockmvctester)

[테스트 코드](../src/test/java/hansanhha/slice/WebSliceTest.java)


## web layer test

스프링 부트는 웹 계층만 단위 테스트할 수 있도록 최소한의 컨텍스트 구성과 빈만 로드할 수 있는 @WebMvcTest 어노테이션을 제공한다

@WebMvcTest는 전체 애플리케이션 컨텍스트를 로드하지 않을 뿐만 아니라 [MockMvc](#mockmvc)를 자동 구성하여 실제 서블릿 컨테이너 또한 로드하지 않는다

## component scan target list

@Controller, @ControllerAdvice

WebSecurityConfigurer

Filter, HandlerInterceptor

WebMvcConfigurer, WebMvcRegistrations

@JsonComponent

Converter, GenericConverter

HandlerMethodArgumentResolver

@ConfigurationProperties: @EnableConfigurationProperties를 사용한 경우에만 컴포넌트 스캔 대상에 포함된다

추가적인 @Configuration 클래스들이 필요한 경우: @Import 사용 

## MockMvc

MockMvc는 실제 서블릿 컨테이너 없이도 컨트롤러를 호출하여 검증할 수 있도록 도와주는 api로 @WebMvcTest에 의해 자동 구성된다

MockMvc와 함께 컨트롤러의 동작을 요청하고 검증하는 데 사용되는 요소는 다음과 같다
- MockMvcRequestBuilders: mock http 요청 생성 (get(), post() 등)
- MockMvcResultHandlers: mock http 요청/응답 내용 출력 (print(), log())
- MockMvcResultMatchers: 컨트롤러의 응답 검증 생성
- ResultActions: 응답에 대한 동작 정의(andExpect(), andDo(), andReturn())

이러한 요소들과 함께 주로 다음과 같은 구조로 컨트롤러를 테스트 한다

mockMvc의 perform 메서드는 ResultActions를 반환하여 개발자가 컨트롤러 응답에 대해 동작을 정의할 수 있도록 한다

```text
mockMvc.perform(요청)    - http 요청 생성 및 실행
       .andDo(로깅)      - 요청 및 응답 내용 출력
       .andExpect(검증); - 응답 검증 (상태 코드, 본문 내용 등)
```

### 컨트롤러 요청

```java
// get request
mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))

// post request
mockMvc.perform(post("/products")
.contentType(MediaType.APPLICATION_JSON)
.content("{\"name\": \"test product\", \"quantity\": 10, \"amount\": 10000}"))

// delete
mockMvc.perform(delete("/products/1"))
```

### 요청/응답 출력

```java
// 요청과 응답 내용을 콘솔에 출력
mockMvc.perform(get("/products/1"))
       .andDo(print()); 
```


### 응답 검증

```java
// http 200 검증
mockMvc.perform(get("/products/1"))
       .andExpect(status().isOk()); 

// 응답 본문 검증 (hamcrest)
mockMvc.perform(get("/products/1"))
        .andExpect(content().string(containsString("test product")));

// json 응답 검증 (jsonPath())
mockMvc.perform(get("/products/1"))
        .andExpect(jsonPath("$.name").value("test product"))
        .andExpect(jsonPath("$.quantity").value(10))
        .andExpect(jsonPath("$.amount").value(10000));

// 응답 헤더 검증
mockMvc.perform(get("/products/1"))
        .andExpect(header().string("Content-Type", "application/json"));
```


## MockMvcTester

스프링 6.2부터 도입된 MockMvcTester는 MockMvc와 AssertJ를 함께 사용하여 스프링 mvc 애플리케이션의 요청을 테스트한다

스프링 부트의 @MockMvcTest는 MockMvc 뿐만 아니라 AssertJ를 사용할 수 있는 환경이라면 (기본적으로 스프링 부트 스타터 테스트에 포함된다) MockMvcTester도 자동 구성한다

Assertion 문과 함께 MockMvc를 사용하는 방식으로 동작한다

```java
assertThat(mvc.get().uri("/greeting").accept(MediaType.APPLICATION_JSON_VALUE))
        .hasStatusOk()
        .hasBodyTextEqualTo("hello web layer test by MockMvcTester");
```

### vs MockMvc

MockMvc의 perform 메서드를 수행하다가 해결되지 않은 예외가 발생해도 예외를 발생시키지 않는다

이와 반대로 MockMvcTester의 perform 메서드는 예외가 발생한 경우 예외를 제공한다
