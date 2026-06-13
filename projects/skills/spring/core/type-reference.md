---
layout: default
title:
---

## Generics Type Erasure

컴파일 프로그램 실행 단계: 컴파일 - 링킹 - 실행

자바는 컴파일 시점에 컴파일러에 의해 바이트 코드로 변형되면서 소스 코드에 명시한 제네릭 타입이 없어짐

실행 시점엔 제네릭 타입들이 모두 Object 타입으로 대체됨

이걸 **타입 소거(Type Erasure)**라고 하는데, 이 특성에 의해 런타임 시점에 특정 타입 정보를 유지할 수 없음  

따라서 스프링/jackson 라이브러리 등에서 ParameterizedTypeReference, TypeReference 같이 런타임에 제네릭 타입 정보를 유지할 수 있는 도구를 제공함   

## `ParameterizedTypeReference<T>`

런타임에 제네릭 타입 정보를 유지하고 전달할 수 있는 스프링 프레임워크 유틸리티 추상 클래스

제네릭 타입 정보는 자바에서 제공하는 `Type` 인터페이스(`java.lang.reflect` 패키지)를 이용함

Type은 자바 언어에서 모든 타입에 대한 공통 슈퍼 인터페이스로 raw type(non-generic type), parameterized type(generic type), type variables, array type, primitive type 등을 포함함 

```java
public interface Type {
    
}
```

## `TypeReference<T>`