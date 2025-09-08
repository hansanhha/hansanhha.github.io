---
layout: default
title:
---

[mockito](#mockito)

[주요 기능](#주요-기능)

[내부 동작](#내부-동작)

[주의점](#주의점)


## mockito

mockito는 자바 단위 테스트에서 mock 객체를 생성하고 다룰 수 있도록 도와주는 프레임워크로 주로 junit과 spring boot와 함께 사용된다

보통 단위 테스트에서는 특정 클래스나 메서드를 테스트할 때 협력하는 클래스나 외부 시스템 요청(db, api 등)에 대한 의존성을 최소화할 필요가 있는데 

mockito를 활용하여 **테스트 코드에서 의존성을 제거**하여 순수한 단위 테스트를 가능하게 하거나 **mock 객체의 특정 동작을 시뮬레이션**할 수 있다


## 주요 기능

mocking (mock 객체 생성)
- 가짜 객체를 만들어 실제 객체처럼 동작하도록 한다
- `mock(targetClass)` 또는 `@Mock`

stubbing (동작 정의)
- 특정 메서드가 호출될 때 반환 값을 지정하거나 예외를 발생시키도록 mock 객체의 동작을 설정한다
- `when(mock.method()).thenReturn(value)`

verification (메서드 호출 검증) 
- 특정 메서드가 몇 번 호출되었는지, 올바른 인자로 호출되었는지 확인한다
- `verify(mock).method()`

spy 객체 활용
- 실제 객체를 감싸면서 일부 메서드만 mocking한다
- `spy(targetClass)` 또는 `@Spy`

argument captor (메서드 인자 확인)
- 특정 메서드 호출 시 전달된 인자를 캡처하여 검증한다
- `ArgumentCatpor.forClass(targetClass)` `captor.capture()` 


## 내부 동작

mockito는 자바의 동적 프록시(dynamic proxy) 또는 cglib을 이용하여 mocking 기능을 수행한다

mock 객체 생성: 자바 reflection api와 cglib을 사용하여 mock 객체 생성

stubbing: mock 객체의 메서드가 호출될 때 이를 가로채고 원하는 동작을 수행하도록 한다
- 내부적으로 MethodInterceptor를 사용하여 메서드 호출을 후킹(hooking)한다
- 호출된 메서드가 stubbing 되어 있다면 지정된 값을 반환한다
- 그렇지 않으면 기본값을 반환한다 (int: 0, String: null)


## 주의점

### 과도한 mocking 피하기

핵심적인 의존성만 mocking하여 불필요한 mocking 남발을 하지 않아야 한다

### verify() 남용하지 않기

단순한 getter/setter 호출 검증을 하지 않고 메서드 호출 검증이 필요한 경우에만 사용한다

### when().thenReturn() 보다 doReturn().when() 사용

spy 객체를 사용할 때 when().thenReturn()은 내부적으로 실제 메서드를 호출할 가능성이 있기 때문에 안전하게 doReturn().when()을 사용한다



