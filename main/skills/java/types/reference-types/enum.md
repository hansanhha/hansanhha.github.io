---
layout: default
title:
---

[enum](#enum)

[enum constant](#enum-constant)

[Enum<E>](#enume)


## enum

enum는 단순한 상수가 아니라 이름이 지정된 클래스 private static final 인스턴스를 지정하는 제한된 클래스 타입이다

enum은 최상위 클래스, 멤버 클래스 또는 지역 클래스로 정의할 수 있으며 abstract, final, sealed, non-sealed 키워드를 사용할 수 없다

다만 다음과 같은 경우 암묵적으로 final 또는 sealed 키워드를 취급한다
- enum 클래스 바디에 이넘 상수가 하나도 포함되지 않은 경우 final로 취급된다
- enum 클래스 바디에 이넘 상수가 적어도 하나가 포함된 경우 sealed 키워드를 취급하며 허용된 자식 클래스(permitted direct subclasses)는 이넘 상수에 대한 암묵적인 익명 클래스가 된다

또한 enum 클래스는 record 클래스과 동일하게 extends 절을 가질 수 없고, implements 절만 선언할 수 있다

중첩된 이넘 클래스(지역 이넘 클래스 포함)는 모두 암묵적으로 static이 된다

### enum definition

이넘 클래스 바디에는 이넘 상수, 생성자, 멤버, 초기화 블록(인스턴스/정적)을 정의할 수 있으며 인터페이스를 구현할 수 있다

모든 요소가 static final로 관리되므로 같은 값은 항상 같은 인스턴스를 참조한다

클래스와 마찬가지로 생성자를 정의하지 않으면 파라미터가 없는 기본 생성자가 자동으로 적용된다

```java
public enum Coin {

    PENNY(1), 
    NICKEL(5), 
    DIME(10), 
    QUARTER(25);

    {
        System.out.println("coin enum class initiating...");
    }

    static {
        System.out.println("coin enum class loading...");
    }
    
    // 생성자는 public, protected 접근 제어자를 가질 수 없으며
    // 자동으로 private 접근 제어자 범위를 가진다
    Coin(int value) { 
        this.value = value; 
    }

    private final int value;
    
    public int value() { 
        return value; 
    }
    
}
```

아래와 같이 이넘 클래스에 적용된 static 필드에 대해 생성자, 인스턴스 초기화 블록에 대해 접근할 수 없다

primitive 타입의 정적 변수에만 접근할 수 있다

```java
public enum Color {

    RED, GREEN, BLUE;

    static final Map<String,Color> colorMap = new HashMap<>();

    // 생성자에서 참조 타입의 static 멤버 필드에 접근할 수 없다
//    Color() {
//        colorMap.put(toString(), this);
//    }

    static {
        for (Color c : Color.values())
            colorMap.put(c.toString(), c);
    }
}
```

위의 Color 이넘 클래스의 바이트 코드는 간단히 아래와 같이 변환된다

Color라는 클래스와 각 이넘 상수의 이름을 가진 private static final Color 인스턴스가 생성되고 ENUM$VALUES 필드를 통해 인스턴스들을 관리한다

각 인스턴스에 대한 순서(ordinal)는 자동으로 할당된다

```java
public final class Color extends Enum<Color> {
    public static final Color RED = new Color("RED", 0);
    public static final Color GREEN = new Color("GREEN", 1);
    public static final Color BLUE = new Color("BLUE", 2);

    private static final Color[] ENUM$VALUES = {RED, GREEN, BLUE};
    static final Map<String,Color> colorMap = new HashMap<>();

    static {
        for (Color c : Color.values())
            colorMap.put(c.toString(), c);
    }
    
    private Color(String name, int ordinal) {
        super(name, ordinal);
    }

    public static Color[] values() {
        return ENUM$VALUES.clone();
    }

    public static Color valueOf(String name) {
        return Enum.valueOf(Color.class, name);
    }
}
```


## enum constant

이넘 클래스 바디에 정의되는 이넘 상수는 이넘 클래스의 인스턴스로, 각 이넘 상수에 대해 단 하나의 인스턴스만 생성되어 `==` 연산자로 비교하는 게 가능하다

이넘 클래스는 이넘 상수 이외에 어떠한 인스턴스를 가질 수 없으며 명시적으로 이넘 클래스에 대한 초기화를 할 수 없다

이넘 상수의 클래스 바디는 enum 클래스에 대한 자식 익명 클래스(final)로 선언되어 익명 클래스의 규칙을 따른다 -> 생성자를 포함할 수 없으며 접근 가능한 인스턴스 메서드를 재정의하는 경우에만 외부에서 접근할 수 있다

아래의 Greeting 이넘 클래스에서 greet 추상 메서드를 선언하고 각 이넘 상수에서 클래스 바디(`{}`)에서 구현하고 있다

```java
public enum Greeting {

    KOREAN {
        @Override
        public String greet() {
            return "안녕";
        }
    },

    ENGLISH {
        @Override
        public String greet() {
            return "hello";
        }
    },

    JAPANESE {
        @Override
        public String greet() {
            return "こんにちは";
        }
    };

    abstract public String greet();
}
```


## `Enum<E>`

모든 enum 타입의 클래스는 자동으로 `Enum<E>` 추상 클래스를 상속한다

이 추상 클래스에서 제공하는 메서드는 다음과 같다

### ordinal()

각 요소의 순서를 반환한다 (0부터 시작)

```java
System.out.println(Color.RED.ordinal());  // 0
System.out.println(Color.GREEN.ordinal()); // 1
System.out.println(Color.BLUE.ordinal()); // 2
```

### name()

열거형의 이름을 문자열로 반환한다

```java
System.out.println(Color.RED.name()); // "RED"
```

### valueOf()

문자열을 enum 요소로 변환한다

```java
Color c = Color.valueOf("BLUE");
System.out.println(c); // BLUE
```

### values()

모든 enum 요소를 배열로 반환한다

```java
for (Color c : Color.values()) {
    System.out.println(c);
}
```




