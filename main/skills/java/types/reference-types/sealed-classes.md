---
layout: default
title:
---

[sealed classes](#sealed-classes)

[sealed classes vs traditional inheritance/implementation](#sealed-classes-vs-traditional-inheritanceimplementation)

[keywords](#sealed-class-keywords)

[record classes as permitted subclasses](#record-classes-as-permitted-subclasses)

[narrowing reference conversion and disjoint types]()

[sealed class example](#example-for-sealed-class)

[sealed interface example](#example-for-sealed-interface)


## sealed classes

자바 17에서 정식으로 도입된 sealed class는 클래스와 인터페이스의 상속/구현을 제한하는 기능을 가진다

특정 클래스가 어떤 클래스/인터페이스만 상속받을 수 있는지 명시적으로 지정할 수 있다 


## sealed classes vs traditional inheritance/implementation

기존의 클래스 상속이나 인터페이스 구현은 해당 타입이 final 키워드를 사용하지 않는다면 어느 클래스에서나 가능했다

이 방식은 확장을 완전히 열어두거나 완전히 닫는 극단적인 방식으로 밖에 관리할 수 없었다

코드 작성자가 의도한 대로 동작하려면 일부분의 상속이나 구현 대상을 제한함으로써 코드의 안전성을 확보할 필요가 있다

또한 자바 21 이전에 패턴 매칭(switch)을 사용할 때 항상 default 케이스가 필요했다

아래의 Bird 클래스가 새로 추가된 상황에서 switch 문에 포함시키지 않아도 컴파일러가 서브 클래스가 빠진 것을 체크하지 못해 런타임 예외가 발생할 수 있다 -> default 케이스가 더 이상 유효하지 않을 수 있다

```java
abstract class Animal { }
class Dog extends Animal { }
class Cat extends Animal { }
class Bird extends Animal { }

public class Main {
    public static void main(String[] args) {
        Animal animal = new Dog();
        
        String result = switch (animal) {
            case Dog d -> "Dog";
            case Cat c -> "Cat";
            default -> "Unknown"; // Bird 추가되면 여기를 바꿔야 한다 (바꾸지 않아도 컴파일이 된다 -> 런타임 예외 발생 가능)
        };
    }
}
```

sealed class는 특정 클래스/인터페이스만 상속하도록 제한적으로 허용하여 코드가 예상한 대로 동작하도록 보장하면서 기능을 확장할 수 있게 한다

또한 switch 문에서 컴파일러가 default 문 없이도 모든 경우를 체크할 수 있어서 서브 클래스가 빠지지 않도록 검사하여 런타임 예외를 방지한다

아래의 Zerg 인터페이스를 구현하는 Hydralisk와 Mutalisk가 switch 문에 사용되지 않는 경우 컴파일 오류가 발생한다 

```java
public class PatternMatching {

    public static void main(String[] args) {

        Hydralisk hydralisk = new Hydralisk();
        UpgradedHydralisk upgradedHydralisk = new UpgradedHydralisk();
        Mutalisk mutalisk = new Mutalisk();

        burrowZergUnit(hydralisk);
        burrowZergUnit(upgradedHydralisk);
        burrowZergUnit(mutalisk);

    }

    private static void burrowZergUnit(Zerg zerg) {
        switch (zerg) {
            case UpgradedHydralisk upgradedHydralisk -> upgradedHydralisk.burrow();
            case Hydralisk hydralisk -> hydralisk.burrow();
            case Mutalisk mutalisk -> mutalisk.burrow();
        }
    }
}
```


## sealed class keywords

### sealed, permits keyword

sealed 키워드 자신의 상속/구현을 제한하는 클래스 또는 인터페이스임을 나타내고, 허용할 클래스/인터페이스를 permits 키워드를 통해 지정한다

아래의 Zerg 인터페이스는 Mutalisk와 Mutalisk와 Hydralisk의 클래스에게만 구현을 허용한다 

permits 대상이 되는 Mutalisk, Hydralisk는 반드시 Zerg 인터페이스를 무조건 구현해야 하며 각각 자신의 타입에 대한 상속 제한 범위를 결정해야 컴파일 오류가 발생하지 않는다

```java
public sealed interface Zerg permits Mutalisk, Hydralisk {
    
    void burrow();
    void evolve();
}
```

### sealed, non-sealed, final keyword

sealed 클래스/인터페이스를 상속하는 서브 클래스는 3가지 키워드 중 하나를 반드시 사용해야 한다

final: 더 이상 확장할 수 없음

non-sealed: 제한없이 상속 허용

sealed: 다시 하위 클래스를 제한

아래의 Hydralisk 클래스는 Zerg 인터페이스를 구현하고 다시 sealed 키워드를 사용하여 UpgradedHydralisk 클래스에게 상속을 허용한다 

```java
public sealed class Hydralisk implements Zerg permits UpgradedHydralisk {

    @Override
    public void burrow() {
        System.out.println("hydralisk burrow");
    }

    @Override
    public void evolve() {
        System.out.println("hydralisk evolves into lurker");
    }

}
```

## record classes as permitted subclasses

레코드 클래스는 암묵적으로 final 키워드가 적용되고 extends 키워드를 사용할 수 없는 규칙이 있다

따라서 sealed 인터페이스만 구현할 수 있다

```java
public sealed interface Something permits SealedRecord {

    void doSomething();
}
```

```java
public record SealedRecord() implements Something {

    @Override
    public void doSomething() {
        System.out.println("sealed record");
    }
}
```


## narrowing reference conversion and disjoint types

서로소 타입(disjoint type)이란 공통적인 상위 타입을 공유하지 않아 서로 타입 변환을 할 수 없는 타입을 의미한다

일반적인 캐스팅 방식을 사용하면 컴파일 시점에 컴파일러는 서로소 타입 간의 타입 변환 예외를 잡지 못한다

아래의 Rectangle 클래스는 Polygon 인터페이스를 구현하고, Triangle 클래스는 구현하지 않는다

```java
interface Polygon { }

class Rectangle implements Polygon { }

class Triangle { }
```

`Polygon p1 = rectangle;`는 Triangle 클래스는 Rectangle 인터페이스를 구현했기에 런타임의 타입 변환이 정상적으로 수행된다

반면 `Polygon p2= (Polygon) triangle;`의 경우 Triangle 클래스는 Rectangle 인터페이스를 구현하지 않지만 컴파일러가 오류를 잡지 못한다

따라서 런타임에 캐스팅을 시도하다 ClassCastException 예외가 발생한다

```java
public class NarrowingReferenceConversion {

    public static void main(String[] args) {
        Rectangle rectangle = new Rectangle();
        Triangle triangle = new Triangle();

        Polygon p1 = (Polygon) rectangle;
        
        // 런타임 예외 발생
        Polygon p2= (Polygon) triangle;
    }
}
```

반면 아래와 같이 sealed 관련 키워드인 final 클래스를 사용한 UtahTeapot 클래스로 타입 변환을 시도하면 컴파일러는 UtahTeapot 타입과 Polygon 타입이 서로소 타입인 것을 인지하고 컴파일 시점에 타입 변환 오류를 발생시킨다 

```java
UtahTeapot utahTeapot = new UtahTeapot();

// 컴파일 시점 타입 변환 오류 발생
Polygon p3 = (Polygon) utahTeapot;

final class UtahTeapot { }
```


## example for sealed class

Zerg 추상 클래스를 선언하고 Mutalisk와 Hydralisk 클래스를 상속 허용 클래스로 지정한다

```java
public sealed abstract class Zerg permits Mutalisk, Hydralisk {

    abstract void burrow();
    abstract void evolve();

}
```

Hydralisk 클래스는 Zerg 추상 클래스를 상속하면서 다시 sealed 키워드를 사용하여 UpgradedHydralisk 클래스를 상속 허용 대상으로 지정한다 

```java
public sealed class Hydralisk extends Zerg permits UpgradedHydralisk {

    @Override
    public void burrow() {
        System.out.println("hydralisk burrow");
    }

    @Override
    public void evolve() {
        System.out.println("hydralisk evolves into lurker");
    }

}
```

UpgradedHydralisk 클래스는 Hydralisk 클래스를 상속하며 final 키워드를 사용하여 더 이상 상속을 허용하지 않는다  

```java
public final class UpgradedHydralisk extends Hydralisk {

    @Override
    public void burrow() {
        System.out.println("upgraded hydralisk burrow");
    }
}
```


## example for sealed interface

sealed 인터페이스도 sealed 클래스와 동일한 메커니즘을 사용한다

BeanFactory는 sealed 키워드를 사용하고 ApplicationContext 인터페이스를 구현 대상으로 지정한다

```java
public sealed interface BeanFactory permits ApplicationContext {

    void registerBean();
}
```

ApplicationContext 인터페이스는 BeanFactory를 구현하며 sealed 키워드를 사용하여 AnnotationApplicationContext 클래스에게 구현을 허용한다 

```java
public sealed interface ApplicationContext 
        extends BeanFactory 
        permits AnnotationApplicationContext {
    
    void run();
}
```

AnnotationApplicationContext 클래스는 BeanFactory와 ApplicationContext 인터페이스를 구현하고 더 이상 확장하지 않는 final 키워드를 사용한다 

```java
public final class AnnotationApplicationContext implements ApplicationContext {

    @Override
    public void run() {
        System.out.println("application context started");
    }

    @Override
    public void registerBean() {
        System.out.println("registered bean in container");
    }
}
```

