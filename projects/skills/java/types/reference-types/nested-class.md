---
layout: default
title:
---

[nested class](#nested-class)

[static nested class](#static-nested-class)

[non-static nested class](#non-static-inner-class)

[shadowing](#shadowing)


## nested class

클래스 내부에 또 다른 클래스를 정의한 클래스를 중첩 클래스라고 한다

논리적으로 특정 클래스에 종속적인 클래스를 그룹화하고 외부 클래스와 관계를 유지하는 클래스의 접근성을 제한할 수 있다

다만 중첩 클래스는 외부 클래스와 상속 관계를 가지지 않는다

중첩 클래스 유형
- 정적 중첩 클래스: static 키워드를 사용하여 선언한 클래스로 외부 클래스의 인스턴스 없이 사용할 수 있다
- 비정적 중첩 클래스: static 키워드 없이 선언한 클래스로 외부 클래스의 인스턴스를 통해 사용할 수 있다

중첩 클래스는 컴파일 시 `Outer.class` `Outer$Inner.class` 형태로 각각 독립적인 .class 파일로 분리된다

![nested class file](./images/nested%20class%20file.png)


## static nested class

외부 클래스의 인스턴스 없이 독립적으로 사용 가능한 중첩 클래스

static 키워드를 사용하여 클래스를 정의하며 외부 클래스의 static 멤버에만 접근할 수 있다

```java
public class NestedClass {
    static String outerClassStaticField = "outer class static field";
    String outerClassInstanceField = "outer class instance field";

    static class StaticNested {
        void display() {
            System.out.println("static nested class");
            System.out.println(outerClassStaticField + " can access");

            // static이 아닌 멤버에는 접근 불가
//            System.out.println(outerClassInstanceField + " can't access");
        }
    }
}
```

### 정적 중첩 클래스의 jvm 내 메모리 구조

```text
[method area]
  ├── NestedClass.class (클래스 메타데이터)
  ├── NestedClass$StaticNested.class (완전히 독립적인 클래스)

[heap area]
  ├── StaticNested 객체 (외부 인스턴스 참조 없음)
```

외부 클래스(NestedClass.class)와 정적 중첩 클래스(NestedClass$StaticNested.class)가 각각 독립적인 클래스로 메서드 영역에 로드된다

StaticNested 클래스는 외부 클래스의 인스턴스 없이 객체를 생성할 수 있으며 heap 영역에 StaticNested 객체가 생성된다


## non-static inner class

외부 클래스의 인스턴스를 필요로 하는 중첩 클래스

static 키워드 없이 클래스를 정의하며 외부 클래스의 모든 멤버에 접근할 수 있다

```java
public class NestedClass {

    static String outerClassStaticField = "outer class static field";
    String outerClassInstanceField = "outer class instance field";

    class Inner {
        void display() {
            System.out.println("inner nested class");
            System.out.println(outerClassInstanceField + " can access");
            System.out.println(outerClassInstanceField + " can access");
        }
    }
}
```

### 비정적 중첩 클래스의 jvm 내 메모리 구조

```text
[method area]
  ├── NestedClass.class (클래스 메타데이터)
  ├── NestedClass$Inner.class (외부 클래스와 강한 연결)

[heap area]
  ├── NestedClass 객체
        ├── NestedClass field (외부 클래스 필드)
        ├── Inner 객체 (this$0 = 외부 클래스 객체 참조)
```

외부 클래스가 메서드 영역에 로드된다

정적 중첩 클래스는 외부 클래스에 종속적으로 로드되며 중첩 클래스 객체 내부에 외부 클래스의 참조 정보(`this$0`)가 포함된다


## shadowing

외부 클래스와 중첩 클래스의 필드/파라미터 이름이 동일한 경우 외부 클래스에서 정의한게 가려지는 것을 말한다

[자세한 내용](https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html)