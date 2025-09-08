---
layout: default
title:
---

[generics type](#generics-type)

[type variables (type parameter)](#type-variables-type-parameter)

[parameterized type](#parameterized-type)

[bounded type parameter](#bounded-type-parameter)

[wildcard](#wildcard)

[type erasure](#type-erasure)


## generics type

제네릭 선언된 타입(클래스, 인터페이스)을 **제네릭 타입(Generics Type)**이라고 한다

`Box<T>` `Function<T, R>` `BiFunction<T, U, R>` 등


## type variables (type parameter)

제네릭 타입의 타입 매개변수를 **타입 변수(Type Variables)**라고 하며 객체 타입만이 지정될 수 있다

타입 매개변수는 클래스, 메서드, 인터페이스 정의에 사용되며 컴파일 시 구체적인 타입으로 대체되는 플레이스 홀더 역할을 한다

관용적인 기호 표현
- `T`: Type
- `S` `U` `V`: 2nd, 3rd, 4th types
- `E`: Element
- `K`: Key
- `V`: Value
- `N`: Number
- `R`: Return


## parameterized type

제네릭 타입을 특정 타입으로 구체화한 것을 **매개변수화된 타입(ParameterizedType)**이라고 한다

`FruiteBox<Apple>` `FruiteBox<Orange>` `Function<Integer, Boolean>` `BiFunction<String, String, Integer>` 등


## bounded type parameter

타입 매개변수의 타입을 특정 범위로 제한하는 방식이다

상위 제한 (upper bounded type): `<T extends Number>` -> Number 및 그 하위 클래스(Integer, Double)만 가능

하위 제한 (lower bounded type): `<T super Integer>` -> Integer 및 그 상위 클래스(Number, Object)만 가능

다중 제한 (multiple bounds): `<T extends Number & Comparable<T>>` -> Number 또는 Number 하위 클래스이면서 `Comparable<T>` 구현 클래스만 가능


## wildcard

와일드카드는 제네릭 타입을 유연하게 사용할 수 있도록 도와준다

`?`: 제한 없음. 모든 타입 허용 

`? extends T`: T의 하위 타입만 허용 `List<? extends Number>` -> Number 또는 그 하위 클래스(Integer, Double) 가능

`? super T`: T의 상위 타입만 허용 `List<? super Integer>` -> Integer 또는 그 상위 클래스(Number, Object) 가능


## type erasure

컴파일 시점에 제네릭이 제거되고 실제 타입이 Object로 변환되는 과정

런타임 시점에는 제네릭 정보가 제거된다

```java
// 컴파일 전
class Box<T> { 
    private T value;
    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

// 컴파일 후 런타임 시점에 타입이 소거된다
class Box {
    private Object value;
    public void set(Object value) { this.value = value; }
    public Object get() { return value; }
}

```

