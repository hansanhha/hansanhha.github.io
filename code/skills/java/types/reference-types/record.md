---
layout: default
title:
---

[record](#record)

[Record](#record-1)

[inheritance, implementation in record](#inheritance-implementation-in-record)

[record vs class](#record-vs-class)

[member/local record](#memberlocal-record)


## record

자바 14 프리뷰, 16 버전에서 정식 기능으로 추가된 클래스 타입으로 보일러플레이트 코드를 제거하고 불변(immutable) 데이터를 저장하기 위해 사용한다

모든 레코드 클래스 타입은 컴파일을 거치면 일반 클래스로 변환되면서 [Record](#record-1) 클래스를 상속하는 코드와 각 레코드 컴포넌트에 대한 필드와 메서드 코드가 추가된다

또한 레코드는 명시적으로 상속절(`extends Record`)을 표현할 수 없다

### record definition

레코드 정의는 타입 매개변수, 레코드 헤더(레코드 컴포넌트-레코드 변수), 구현 정의, 레코드 바디로 이뤄진다

레코드는 암묵적으로 final로 취급하기 때문에 **abstract, sealed, non-sealed** 제어자를 사용할 수 없다

```java
// 아무런 값을 가지지 않는 레코드를 선언할 수도 있다
public record EmptyRecordHeader() {
}

// 일반적으로 사용하는 레코드 정의
public record Car(int speed, int amount) {
}
```

아래와 static 초기화 블록, static/instance 메서드, 멤버 클래스, 생성자를 정의할 수도 있다 (인스턴스 초기화 블록은 정의할 수 없음)

```java
public record FruitBox<F extends Fruit>(
        F fruit,
        int size) {
    
    static {
        System.out.println("FruitBox(record) class loading..");
    }
    
    public static <F extends Fruit> FruitBox<F> of(F fruit, int size) {
        return new FruitBox<>(fruit, size);
    }

    public int getTotalAmount() {
        return fruit().getAmount() * size;
    }
    
    public static class Orange implements Fruit {
        
        private final String name;
        private int amount;

        public Orange(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getAmount() {
            return amount;
        }
        
        public void setAmount(int amount) {
            if (amount < 0) amount = 0;
            this.amount = amount;
        }
        
    }

}
```

### record components

레코드 컴포넌트는 레코드의 데이터를 저장하는 필드를 말한다

이 필드는 암묵적으로 private final로 취급되어 생성자에서 한 번 초기화된 이후 변경할 수 없다

```java
public record Car(
        int speed, // record component
        int amount // record component
        ) {
}
```

자바는 각 레코드 컴포넌트에 대해 레코드 클래스는 같은 이름과 타입을 가진 private final non-static 필드를 정의한다

또한 컴포넌트에 어노테이션이 적용된 경우 해당 필드에도 어노테이션을 적용해준다

그리고 컴포넌트에 대해 암묵적으로 생성된 필드를 반환하는 메서드를 정의하는데 이 메서드는 컴포넌트 이름과 동일하며 파라미터를 받지 않는 특징을 가지며 accessor 메서드라고 한다

```java
public record Apple(String name, int amount) implements Fruit {
}

Apple goldApple = new Apple("gold apple", 10_000);

// Apple 레코드의 각 필드를 반환하는 accessor 메서드가 자동적으로 생성된다
goldApple.name();
goldApple.amount();
```

### canonical constructor

레코드는 일반 클래스와 달리 기본 생성자를 암묵적으로 정의하지 않으며, 대신 모든 필드 값을 받는 생성자(표준 생성자, canonical constructor)를 정의한다

생성자에서 로직을 추가하고 싶은 경우 명시적으로 표준 생성자를 정의할 수 있으며 정의하는 방식은 두 가지가 있다

참고로 어느 생성자 방식으로 정의하든 생성자의 접근 제어자는 레코드를 정의한 접근 제어자보다 더 폐쇄적일 수 없다

#### 1. normal canonical constructor

레코드에 선언된 컴포넌트 순서에 맞는 생성자를 명시하는 방식

아래와 같이 Apple의 각 필드의 이름과 순서에 맞게 생성자 파라미터를 구성하여 명시적으로 선언할 수 있다

```java
public record Apple(String name, int amount) implements Fruit {
    
    public Apple(String name, int amount) {
        if (amount < 0) amount = DEFAULT_APPLE_AMOUNT;
        
        this.name = name;
        this.amount = amount;

        System.out.println("created Apple(record) name: " +  name + ", amount: " + amount);
    }

    public Apple(String name) {
        this(name, DEFAULT_VALUE);
        System.out.println("use default apple amount");
    }
}
```

#### 2. compact canonical constructor

축약형 표준 생성자는 레코드의 표준 생성자를 더욱 간결하게 작성할 수 있도록 제공되는 기능이다

매개변수 목록을 생략할 수 있으며, 암묵적으로 필드 초기화가 포함되어 추가적인 로직(검증, 변환 등)을 간편하게 추가할 수 있다

```java
public record Banana(String name, int amount) implements Fruit {

    private static final int DEFAULT_BANANA_AMOUNT = 500;

    public Banana {
        if (amount < 0) amount = DEFAULT_BANANA_AMOUNT;

        System.out.println("created Banana(record) name: " +  name + ", amount: " + amount);
    }
}
```


## Record

모든 record 타입 클래스는 암묵적으로 (컴파일 단계를 거친 후) 아래와 같은 java.lang.Record 클래스를 상속함으로써 불변성을 보장한다

Record 클래스는 equals, hashCode, toString 메서드 자동 생성을 보장한다 (필요에 따라 오버라이딩 가능)

equals와 hashCode는 필드 기반으로 비교하여 동일한 필드 값을 가지면 같은 객체로 간주한다

```java
public abstract class Record {

    // 직접 호출 불가능
    protected Record() {}

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
```

### 컴파일 후 변환된 레코드의 코드

```java
// 레코드 정의
public record Person(String name, int age) { }

// 컴파일 후 변환된 레코드
public final class Person extends java.lang.Record {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String name() { return name; }
    public int age() { return age; }
}
```


## inheritance, implementation in record

일반 클래스와 달리 레코드는 불변성을 보장해야 하므로 특정 클래스를 상속하는 것을 언어 차원에서 방지한다

extends 키워드를 사용하면 컴파일 오류가 발생한다

반면 인터페이스는 일반 클래스와 동일하게 구현할 수 있다

```java
public class AbstractFruit implements Fruit {

    // 레코드는 다른 클래스를 상속할 수 없다
    // extends 키워드를 사용하면 컴파일 오류가 발생한다
//    record watermelon() extends AbstractFruit {
//        
//    }

// 인터페이스는 일반 클래스와 동일하게 구현할 수 있다 
public record Apple(String name, int amount) implements Fruit {

} 
```


## record vs class

| 비교 항목                                    | 클래스       | 레코드                              |
|------------------------------------------|-----------|----------------------------------|
| 부모 클래스                                   | Object    | Record                           |
| 필드 변경 가능 여부                              | 변경 가능     | 변경 불가 (final 필드)                 |
| equals(), hashCode(), toString() 자동 생성 여부 | 수동 구현 필요  | 자동 생성                            |
| 상속 가능 여부                                 | 가능        | 불가능 (final 로 컴파일됨)               |
| 객체 비교 방식                                 | 참조 비교 (==) | 값 비교 (equals()) - 필요에 따라 오버라이딩 가능 |
| extends 키워드 사용 가능 여부                     | 사용 가능     | 사용 불가능                           |


## member/local record

멤버, 로컬 레벨의 레코드는 모두 암묵적으로 static으로 취급한다

멤버 레코드 클래스 정의 시 static 키워드를 명시적으로 사용할 수 있으나 로컬 레코드 정의에는 사용할 수 없다