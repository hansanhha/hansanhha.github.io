---
layout: default
title:
---

[type bounds: covariance, contravariance, invariance](#type-bounds-covariance-contravariance-invariance)

[invariance](#invariance)

[covariance (upper bound)](#contravariance-lower-bound)

[contravariance (lower bound)](#contravariance-lower-bound)


## type bounds: covariance, contravariance, invariance

공변(Covariance), 반공변(Contravariance), 불공변(Invariance)은 변성(variance), 상속 관계와 타입 대체 가능성에 대한 개념으로 자바는 제네릭에 이 개념을 적용한다

주로 제네릭의 타입 매개변수가 상위 타입 관계(subtyping)를 어떻게 유지하는지에 따라 공변, 반공변, 불공변으로 나눈다


## invariance

불공변은 상속 관계가 유지되지 않는 관계를 말한다

A가 B의 하위 타입이어도 `Foo<A>`는 `Foo<B>`는 서로 아무런 상속 관계를 가지지 않는다

자바의 제네릭은 기본적으로 불공변 특징을 갖는다

따라서 서로 다른 제네릭 타입 간에는 상속 관계와 상관없이 서로 다른 것으로 판단한다 

아래의 세 개의 리스트는 모두 자바의 불공변으로 인해 컴파일 오류가 발생한다

```java
List<Number> list1 = new ArrayList<Integer>();
List<Number> list2 = new ArrayList<Long>();
List<Number> list3 = new ArrayList<Double>();
```

불공변이라는 특성으로 인해 타입 안정성을 높일 수 있지만 유연성이 너무나 떨어지는 단점이 있다

그래서 자바의 제네릭은 공변과 반공변이라는 개념을 지원하여 타입 매개변수와 그와 상속 관계에 놓인 다른 타입까지 허용하게끔 동작한다  


## covariance (upper bound)

공변은 같은 방향으로 유지되는 상속 관계(자식에서 부모로)를 말한다

A가 B의 하위 타입이면(A가 B를 상속, 확장한 경우) `Foo<A>`는 `Foo<B>`를 허용하는 것이 공변이다

자바에서의 공변은 **upper bound 와일드카드/타입 매개변수**를 사용해서 적용된다

### upper bound 와일드카드/타입 매개변수

upper bound 와일드카드: `? extends upperBound`

upper bound 타입 매개변수: `T extends upperBound`

upperBound에 타입을 지정하면 해당 타입과 그 하위 타입을 모두 허용한다

```java
// <? extends Number>를 upper bound로 지정하면, Number와 자식 타입인 Interger, Long 등의 타입을 허용한다
List<? extends Number> list = new ArrayList<Number>();
List<? extends Number> list = new ArrayList<Integer>();
List<? extends Number> list = new ArrayList<Long>();
```

### producer extends

extends 키워드는 자식 타입을 허용하는 것 이외에도 추가적인 제약이 따른다

자바 컬렉션, Map에 extends 키워드를 적용한 경우 **읽기(조회)는 허용하지만 새 데이터를 추가(삽입)할 수 없다**

반대로 설명하면 어떤 데이터들을 가지고 있는 컬렉션이 있는데, 이 컬렉션은 자신이 가지고 있는 데이터를 다른 객체에게 전달할 수 있지만 새로운 데이터를 자신의 컬렉션에 넣을 수 없게 제한한다

왜일까?

만약 서로 다른 타입끼리 컬렉션에 데이터를 삽입한다면 런타임에 여러 타입이 뒤죽박죽 섞여서 ClassCastException이 발생하고 말 것이다

이러한 이유로 extends 키워드를 사용하면 해당 컬렉션에 새 데이터 삽입을 제한한다 (설령 upper bound으로 지정한 타입으로 삽입하려고 한다 하더라도)

따라서 **producer(데이터를 보유하고 제공(반환)해주는 객체, 일반적으로 컬렉션이나 Map)**는 extends 키워드를 사용하여 자신의 타입 안정성을 유지할 수 있다

추가적으로 **upper bound가 아닌 하위 타입으로 데이터를 조회하는 경우 해당 타입으로 명시적인 타입 캐스팅**이 필요하다


```java
// extends 키워드를 사용하여 upper bound와 그 하위 타입으로 타입 매개변수를 제한한다
List<? extends Number> upperBoundList = new ArrayList<>();

// 아래의 삽입 코드는 모두 컴파일 에러가 발생한다
upperBoundList.add(1);
upperBoundList.add(1L);
upperBoundList.add(1F);

// 대신 데이터를 조회할 수 있다
Number n = upperBoundList.getFirst();

// 하위 타입으로 조회하는 경우 명시적인 타입 캐스팅이 필요하다
Integer i = (Integer) upperBoundList.getFirst();
```


## contravariance (lower bound)

반공변은 반대 방향으로 유지되는 상속 관계(부모에서 자식으로)를 말한다

A가 B의 하위 타입이면(A가 B를 상속, 확장한 경우) `Foo<B>`는 `Foo<A>`을 허용한다

자바에서의 공변은 **lower bound 와일드카드/타입 매개변수**를 사용해서 적용된다

### lower bound 와일드카드/타입 매개변수

lower bound 와일드카드: `? extends lowerBound`

lower bound 타입 매개변수: `T extends lowerBound`

lowerBound에 타입을 지정하면 해당 타입과 그 상위 타입을 모두 허용한다

```java
// <? extends Integer>를 lower bound로 지정하면, Interger와 상위 타입인 Number, Object 타입을 허용한다
List<? extends Integer> list = new ArrayList<Integer>();
List<? extends Integer> list = new ArrayList<Number>();
List<? extends Integer> list = new ArrayList<Object>();
```

### consumer super

extends 키워드와 비슷하게 단순히 super 키워드는 상위 타입을 허용하는 것 이외에도 추가적인 제약이 따른다  

자바 컬렉션, Map에 super 키워드를 적용한 경우 **추가(삽입)은 허용하지만 읽기(조회)를 Object 타입으로만 제한한다**

반대로 설명하면 어떤 데이터들을 가지고 있는 컬렉션이 있는데, 이 컬렉션에 새로운 데이터를 추가할 수 있지만 자신이 가지고 있는 데이터를 다른 객체에게 전달할 수 없다

왜일까?

super 키워드는 제한된 제네릭 타입 매개변수 중 최하위 타입을 지정한다

extends의 경우 최상위 타입(upper bound)을 지정하고 그 아래로는 열려 있어 런타임에 어떤 타입이 들어올지 모르지만, super의 경우 최하위(lower bound)와 최상위(Object)가 결정되어 있으므로 안전하게 데이터를 삽입할 수 있다

하지만 어떤 타입이 삽입됐는지 알 수 없기 때문에 extends와 다르게 데이터를 Object 타입으로만 조회할 수 있다 (명시적인 타입 캐스팅을 적용하면 조회할 수 있다)

따라서 **consumer(다른 곳으로부터 데이터를 전달받는 객체)**는 super 키워드를 사용하여 안전하게 데이터를 삽입할 수 있다

```java
// super 키워드를 사용하여 lower bound와 그 상위 타입으로 타입 매개변수를 제한한다
List<? super Integer> lowerBoundList = new ArrayList<Number>();

// lower bound는 데이터를 추가할 수 있다(consumer)
lowerBoundList.add(10);
lowerBoundList.add(20);
lowerBoundList.add(30);

// 대신 데이터에 접근할 수 없다
// Number n = lowerBoundList.get(0);
// Integer i = lowerBoundList.get(0);

// 명시적인 타입 캐스팅으로 특정 타입으로 접근할 수 있다
Number n = (Number) lowerBoundList.getFirst();
Integer i = (Integer) lowerBoundList.get(1);
```


## 공변 vs 반공변 vs 불공변

|     | java 키워드                  | 읽기| 쓰기| 기타                                                    |
|-----|---------------------------|--------------|--------------|-------------------------------------------------------|
| 공변  | `? extends ReferenceType` | 가능           | 불가능          | 데이터 생산(producer): 상위 타입으로 읽을 수 있으나 쓸 수 없다 - 타입 안전한 읽기 |
| 반공변 | `? super ReferenceType`   | 제한적(Object)  | 가능           | 데이터 소비(consumer): 하위 타입으로 쓸 수 있으나 읽을 수 없다 - 타입 안전한 쓰기 |
| 불공변 | `ReferenceType`            | 가능           | 가능           | 타입 고정(정확히 동일한 타입만 사용): 상속 관계를 고려하지 않는다 - 타입 고정        |

