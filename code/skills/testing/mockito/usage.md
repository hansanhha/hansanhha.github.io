---
layout: default
title:
---

[configuration](#configuration)

[mocking](#mocking)

[spying](#spying)

[stubbing](#stubbing)

[verification](#verification)

[capturing](#capturing)

[테스트 코드](../src/test/java/hansanhha)


## configuration

mockito에서 제공하는 junit 5의 확장 모델을 사용하여 mockito 기능을 사용할 수 있다

```java
@ExtendWith(MockitoExtension.class)
public class MockingTest
```


## mocking

mocking: 가짜 객체를 만들어 실제 객체처럼 동작하게 하는 기법

#### mock 객체 생성

@Mock 어노테이션 또는 Mockito.mock 정적 메서드를 사용하여 mock 객체를 생성할 수 있다

```java
// mockito로부터 mock 객체 주입받기 
@Mock
ProductRepository productRepository;

// mock 객체 직접 생성
ProductRepository mockProductRepository = Mockito.mock(ProductRepository.class);
```

#### mock 객체 주입

@InjectMocks 어노테이션 또는 직접 의존성 주입을 통해 mock 객체를 의존하는 객체에게 주입할 수 있다

```java
// mockito로부터 의존하는 mock 객체 주입받기
@InjectMocks
ProductService productService;
```


## spying

spying: 실제 객체를 감싸서 일부 메서드는 원본 객체의 동작을 유지하고 일부만 mocking하는 기법

#### spy 객체 생성

```java
// mockito로부터 spy 객체 주입받기
@Spy
ProductRepository productRepository;

// spy 객체 직접 생성
ProductRepository spyProductRepository = Mockito.spy(ProductRepository.class);
```

#### spy 객체 주입

```java
// mockito로부터 의존하는 spy 객체 주입받기
@InjectMocks
ProductService productService;
```


## stubbing

stubbing: mock 또는 spy 객체의 동작(특정 값을 반환, 예외 발생 등)을 설정하는 기법

mock/spy 객체에게 stubbing 하는 방법은 크게 두 가지로 나뉜다

1. `when().thenXXX()`
2. `doXXX().when().someMethod()`

#### when()과 doXXX()의 차이점

두 메서드는 실제 객체의 메서드를 실제로 호출하는지 안하는지의 차이가 있다

when: 실제 객체 메서드 호출

doXXX: 실제 객체 메서드 호출 X

```text
when 설정 -> 테스트 실행 -> 실제 객체의 메서드 호출

doXXX 설정 -> 테스트 실행
```

mock 객체는 실제 객체와 별개인 가짜 객체일 뿐이므로 두 메서드 중 어느 것을 사용해도 상관없지만

spy 객체의 경우 실제 객체를 래핑한 mocking 객체이므로 when 메서드를 사용하여 스텁을 설정하면 실제 객체의 메서드를 호출한 뒤 설정된 동작을 수행한다

만약 실제 객체의 메서드 수행 중 예외가 발생한다면 테스트 실패로 이어질 가능성이 있다

따라서 spy 객체에 대한 스텁을 지정하려면 doXXX 메서드를 사용하는 것이 안전하다


#### when().thenXXX() stubbing

```java
// when().thenReturn() stubbing
Mockito.when(productRepository.save(Mockito.any(Product.class)))
        .thenReturn(new Product(stubbedProductName, 10, 10_000));

// when().thenThrow() stubbing
Mockito.when(productRepository.save(Mockito.any(Product.class)))
        .thenThrow(new RuntimeException("Product 저장 중 예외 발생"));
```

#### doXXX().when().someMethod() stubbing

```java
// doReturn().when().save() stubbing
Mockito.doReturn(new Product(stubbedProductName, 10, 10_000))
        .when(productRepository)
        .save(Mockito.any(Product.class));

// doThrow().when().save() stubbing
Mockito.doThrow(new RuntimeException("Product 저장 중 예외 발생"))
        .when(productRepository)
        .save(Mockito.any(Product.class));
```


## verification

verification: mock/spy 객체의 특정 메서드가 실제로 호출되었는지 몇 번 호출되었는지 검증하는 기법

검증 메서드는 크게 3가지로 나뉜다

#### verify()

```text
// 사용법
verify(mock, VerificationMode).someMethod()
```

verify 메서드는 mock 객체의 특정 메서드에 대해 주어진 조건을 충족하는지 검증한다

두 번째 인자인 VerificationMode 구현체를 통해 특정 조건을 지정할 수 있다

주요 VerificationMode 구현체
- Times: mock 객체의 메서드 호출 횟수를 검증한다
- Only: mock 객체의 메서드가 단 한번만 호출되는지 검증한다
- Never: mock 객체의 메서드가 단 한번도 호출되지 않는지 검증한다
- Timeout: mock 객체의 메서드가 일정 시간안에 수행되는지 검증한다
- AtMost: mock 객체의 메서드 호출 횟수가 지정된 최대 호출 횟수를 넘기지 않는지 검증한다
- AfLeast: mock 객체의 메서드 호출 횟수가 지정된 최소 호출 횟수만큼 넘는지 검증한다
- After: mock 객체의 메서드 검증을 일정 시간 이후 수행한다
- InOrder: mock 객체들의 메서드 호출 순서를 검증한다
- Calls: mock 객체들의 메서드 호출 횟수를 검증한다 (InOrder와 함께 사용해야 된다)

[자세한 테스트 코드](../src/test/java/hansanhha/VerificationTest.java)

```java
// ProductRepository.save() 메서드가 3번 호출되는지 검증한다
Mockito.verify(productRepository, Mockito.times(3)).save(Mockito.any(Product.class));

// 단 한번만 호출되는지 검증한다
Mockito.verify(productRepository, Mockito.only()).save(Mockito.any(Product.class));
```

#### verifyNoInteractions()

주어진 mock 객체와 상호작용하지 않는지 검증한다

```java
Mockito.verifyNoInteractions(productRepository);
```

#### verifyNoMoreInteractions()

verify 검증 후 mock 객체와 더 이상 상호작용하지 않는지 검증한다

```java
productService.create("test product", 10, 10_000);
productService.create("test product", 10, 10_000);

// ProductRepository.save() 메서드를 두 번 호출한 후 더 이상 상호작용을 하지 않는지 검증한다 
// 만약 save() 메서드를 더 호출하거나 다른 메서드와 상호작용하면 검증에 실패한다
Mockito.verify(productRepository, Mockito.times(2)).save(Mockito.any(Product.class));
Mockito.verifyNoMoreInteractions(productRepository);
```


## capturing

capturing: mock/spy 객체의 호출된 메서드에 전달된 인자(argument)를 캡처하여 확인하는 기법

실제 객체가 아닌 mocking된 객체의 메서드 인자를 캡처할 수 있다

#### ArugmentCaptor 생성

```java
// mockito로부터 해당 제네릭 타입에 대한 ArgumentCaptor 주입받기
@Captor
ArgumentCaptor<Product> injectedProductArgumentCaptor;

// Product 클래스 타입으로 ArgumentCaptor 생성
ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

// 제네릭 타입으로 ArgumentCaptor 생성
// captor 메서드에 인자를 전달하지 않아야 한다
ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.captor();
```

#### 메서드 인자 캡처 및 검증

```java
// 메서드 인자 캡처
Mockito.verify(productRepository).save(productArgumentCaptor.capture());

// stub과 함께 메서드 인자 캡처
Mockito.doNothing()
        .when(productRepository)
        .update(Mockito.any(Product.class), productArgumentCaptor.capture());

// 메서드 인자 확인
Product captorValue = productArgumentCaptor.getValue();

// 메서드 인자 검증
Assertions.assertEquals("test product", captorValue.getName());
```

