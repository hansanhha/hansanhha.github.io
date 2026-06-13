---
layout: default
title:
---

[mockito + spring boot test](#mockito--spring-boot-test)

[spring bean mocking: @Mock, @MockBean, @MockitoBean](#spring-bean-mocking-mock-mockbean-mockitobean)

[spring bean spying: @Spy, @SpyBean, @MockitoSpyBean](#spring-bean-spying-spy-spybean-mockitospybean)

[테스트 코드](./src/test/java/hansanhha/SpringMockitoTest.java)


## mockito + spring boot test

서비스 로직 단위 테스트, 컨트롤러 단위 테스트 등 스프링 컨텍스트를 사용하면서 mocking이 필요할 때 주로 사용되는 조합이다

스프링 빈을 목 빈과 스파이 빈으로 만드는 부분만 제외하고 나머지는 [일반적인 mockito 사용](./usage.md)과 동일하다 


## spring bean mocking: @Mock, @MockBean, @MockitoBean

각 어노테이션의 차이는 다음과 같다

#### @Mock

mockito에서 제공하는 어노테이션, 스프링 컨텍스트와 무관하게 특정 객체를 mocking한다

#### @MockBean

스프링 부트 테스트에서 제공하는 스프링 빈 mocking 어노테이션

스프링 부트 3.4 부터 deprecated 처리되었으며 3.6부터 삭제 예정이다

#### @MockitoBean

스프링 6.2부터 제공하는 스프링 빈 mocking 어노테이션

@MockBean 대신 @MockitoBean 사용을 권장한다

```java
@MockitoBean
private SpringProductRepository productRepository;

@Autowired
private SpringProductService productService;
```


## spring bean spying: @Spy, @SpyBean, @MockitoSpyBean

각 어노테이션의 차이는 다음과 같다

#### @Spy

mockito에서 제공하는 어노테이션, 스프링 컨텍스트와 무관하게 특정 객체를 덮어쓴 스파이 객체를 생성한다

#### @SpyBean

스프링 부트 테스트에서 제공하는 스프링 빈 스파이 어노테이션

스프링 부트 3.4 부터 deprecated 처리되었으며 3.6부터 삭제 예정이다

#### @MockitoSpyBean

스프링 6.2부터 제공하는 스프링 빈 스파이 어노테이션

@MockBean 대신 @MockitoBean 사용을 권장한다


```java
@MockitoSpyBean
private SpringProductRepository productRepository;

@Autowired
private SpringProductService productService;
```



