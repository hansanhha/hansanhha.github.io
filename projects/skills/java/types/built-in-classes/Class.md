---
layout: default
title:
---

#### index
- [Class](#class)
- [getting `Class<T>` object](#getting-classt-object)
- [getting class info](#getting-class-info)
- [method dynamic invocation](#method-dynamic-invocation)
- [instance dynamic creation](#instance-dynamic-creation)


#### related-concepts
- [jvm classloader](../../jvm/architectures#classloader)
- [jvm memory structure](../../jvm/architectures#memory-space)
- [reflection](../../java.lang.reflect)


## Class

Class 객체는 클래스에 대한 정보(메타데이터)를 제공하며 런타임에 클래스의 메서드, 필드, 생성자 등의 정보를 동적으로 조회하고 조작할 수 있어서 주로 리플렉션 기능에서 사용된다  

자바의 모든 클래스(.class)는 jvm에서 클래스 로딩 과정을 거친 후 `java.lang.Class<T>` 타입의 객체로 표현된다 

Class 객체는 모든 인스턴스가 공유하는 전역 객체로 method area에 저장된다


## getting `Class<T>` object

`Class<T>` 객체를 얻을 수 있는 방법은 다음과 같다

```java
Class<String> clazz1 = String.class;

Class<?> clazz2 = Class.forName("java.lang.String");

String str = "hello";
Class<? extends String> clazz3 = str.getClass();
```

`String.class`: 컴파일 시점에 Class 객체를 참조하는 방식 (인스턴스를 생성하지 않아도 됨)

`Class.forName("java.lang.String")`: 런타임에 문자열을 통해 Class 객체를 찾는 방식 (동적 로딩, 인스턴스를 생성하지 않아도 됨)

`str.getClass()`: 이미 생성된 인스턴스에서 Class 객체를 획득하는 방식


## getting class info

클래스의 기본적인 정보(이름, 패키지, 필드, 부모 클래스 등)와 제네릭 및 어노테이션 적용 관련 정보, 유형(인터페이스, 레코드, 이넘, 어노테이션)에 대한 정보를 조회할 수 있다

이 기본 메타데이터들이 jvm의 method area 저장되므로 조회 메서드는 메타데이터를 참조하는 역할을 한다

[테스트 코드 확인](https://github.com/hansanhha/hansanhha.github.io/blob/default/code/stack/java/types/source%20code/src/test/java/hansanhha/classes/BuiltInClass_ClassTests.java)

`Class<T>`의 필드, 메서드, 이넘 상수, 어노테이션에 대한 데이터는 다음과 같은 객체에 매핑된다
- 필드: Field
- 메서드: Method
- 이넘 상수: T
- 어노테이션: Annotation

그리고 필드, 어노테이션 등은 private 조회 여부에 따라 두 개의 조회 메서드를 지원한다
- getFields(): public 필드만 조회하는 메서드
- getDeclaredFields(): private 필드를 포함한 모든 필드를 조회하는 메서드


## method dynamic invocation

`Class<Marine>` 객체로부터 Marine 클래스에 정의되어 있는 attack 이라는 이름의 메서드에 대한 Method 객체를 조회한 뒤 Marine 인스턴스를 invoke 메서드의 인자로 전달하면, 해당 인스턴스의 attack 메서드를 호출할 수 있다  

```java
Class<Marine> marineClass = Marine.class;

Method attack = marineClass.getDeclaredMethod("attack");
Marine marine = new Marine();
attack.invoke(marine);
```


## instance dynamic creation

메서드 동적 호출과 유사한 방식으로 `Class<Marine>` 객체로부터 생성자 정보를 가진 `Constructor<Marine>` 객체를 조회한다

이 객체의 newInstance() 메서드를 통해 다음과 같이 동적으로 Marine 인스턴스를 생성할 수 있다 

```java
Class<Marine> marineClass = Marine.class;

Constructor<Marine> constructor = marineClass.getDeclaredConstructor();
Marine marine = constructor.newInstance();
marine.attack();
```