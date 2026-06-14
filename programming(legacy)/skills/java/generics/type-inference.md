---
layout: default
title:
---

[type inference](#type-inference)

[diamond operator](#diamond-operator)

[generic method type inference](#generic-method-type-inference)

[target type](#target-type)

[lambda expression type inference](#lambda-expression-type-inference)


## type Inference

제네릭 타입 추론은 자바 컴파일러가 코드 문맥(context)과 사용된 메서드 호출 등을 기반으로 타입 매개변수를 자동으로 결정하는 기능이다

명시적으로 타입 매개변수를 지정하지 않아도 컴파일러가 적절한 타입을 유추하기 때문에 간결하고 가독성 높은 코드를 작성할 수 있다

메서드 호출 시 전달된 인자 값, 반환 타입, 변수 선언 등을 기반으로 제네릭 타입 매개변수 결정한다

또한 다이아몬드 연산자와 타겟 타입을 이용하여 타입 추론을 수행한다



## diamond operator

객체 생성 시 제네릭 타입을 다이아몬드 연산자를 사용하여 생략할 수 있다

컴파일러는 좌변(참조변수)의 타입 선언을 통해 타입 매개변수를 추론한다

```java
// 좌변의 List<String>을 기반으로 타입 추론에 의해 타입 매개변수 <String> 결정
List<String> list = new ArrayList<>(); 
```

## generic method type inference

제네릭 메서드의 타입 매개변수는 메서드 호출 시 전달된 인자 값, 반환 타입 등을 기반으로 추론된다

```java
// <T> : 제네릭 메서드 타입 매개변수
public static <T> T genericMethodTypeInference(T a) {
      return a;
}


// 타입 추론: 타입 매개변수 T를 String으로 결정
String str = genericMethodTypeInference("hello");

// 타입 추론: 타입 매개변수 T를 Integer로 결정
Integer integer = genericMethodTypeInference(1); 

// 타입 추론: 타입 매개변수 T를 Boolean으로 결정
boolean bool = genericMethodTypeInference(false); 
```

## target type

타겟 타입은 표현식의 결과가 사용되는 문맥을 기반으로 컴파일러가 기대하는 타입을 추론하는 기능으로 표현식이 사용될 위치의 타입(contextual type)으로 정의된다

컴파일러는 특정 위치에 사용된 타입이 적절한 타입인지 검사하기 위해 타겟 타입을 참조한다

### 타겟 타입 적용 예시

#### 변수 할당
    
컴파일러는 참조 변수의 제네릭 타입 매개변수를 타겟 타입으로 설정해서 타입을 추론한다

```java
// 참조 변수 Comparator<String>을 통해 람다 표현식 (s1, s2)의 매개변수 타입을 String으로 추론한다
Comparator<String> stringComparator = (s1, s2) -> s1.length - s2.length();
```

#### 메서드 호출 인자 (제네릭 메서드 X)

메서드의 파라미터 타입이 제네릭이거나, 함수형 인터페이스를 사용하는 경우 메서드의 파라미터 타입을 타겟 타입으로 설정해서 인자의 타입을 추론한다

```java
// List<String>의 sort 메서드는 Comparator<String>을 요구하므로, (s1, s2)의 타입은 String으로 추론한다
List<String> list = Arrays.asList("one", "two", "three");
list.sort((s1, s2) -> s1.compareToIgnoreCase(s2));
```

#### 반환 타입

제네릭 메서드에서 반환 타입이 명확하지 않을 때 메서드 호출 시점에서 전달된 반환 타입을 기반으로 적절한 타입을 추론한다

```java
// Stream.of() 메서드는 Stream<T>를 반환하는 메서드이다
// 반환 타입이 Stream<String>이므로 컴파일러는 T를 String으로 추론한다
Stream<String> stream = Stream.of("A", "B", "C");

public static <T> Stream<T> of(T... values) {
    return Arrays.stream(values);
}
```

#### 삼항 연산자

```java
Integer value = true ? 1 : 2;
```

## lambda expression type inference

람다와 스트림 체인에서 타겟 타입을 통해 컴파일러가 자동으로 타입을 추론한다

동작 방식
- 함수형 인터페이스의 함수 디스크립터(메서드 시그니처)를 확인한다
- 람다 표현식의 매개변수 타입과 리턴 타입을 타겟 타입에 맞게 추론한다

```java
// Runnable: 파라미터가 없고 반환 타입이 void인 함수형 인터페이스
// 람다 표현식과 시그니처 일치하므로 컴파일러가 타입을 추론할 수 있다
Runnable run = () -> System.out.println("Runnable Interface");
```

```java
// Predicate: 하나의 T 파라미터를 받고, boolean을 리턴하는 함수형 인터페이스
// 람다 표현식과 시그니처 일치하므로 컴파일러가 타입을 추론할 수 있다
Predicate<Integer> p = i -> i < 100;
```