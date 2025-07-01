---
layout: default
title:
---

[Dependency](#dependency)

[Association](#association)

[Aggregation](#aggregation)

[Composition](#composition)

[Inhertiance](#inheritance)

[Realization](#realization)

## Dependency

**의존 관계**: 한 객체가 다른 객체를 사용(호출)하는 관계, 일시적인 관계

객체 간의 결합이 약하며, 의존 대상 객체가 없으면 동작할 수 없음

```java
class Nexus {
    
     private Zealot zealot;
     
     public void attack(Unit unit) {
         zealot.attack(unit);
     }
}
```

넥서스 클래스는 질럿 클래스에 의존하여 공격 작업을 수행한다

## Association

**연관 관계**: 한 객체가 다른 객체와 연결된 관계, 지속적인 관계

객체 간의 관계가 명시적이며, 단방향/양방향 연관 관계로 구분된다

단방향(Unidirectional): 한 객체만 다른 객체를 참조

양방향(Bidirectional): 두 객체가 서로 참조

```java
class Nexus {
    private List<Zealot> zealots;
}
```

```java
class Zealot {
    private Nexus nexus;
} 
```

넥서스와 질럿 클래스는 양방향 연결 관계에 속한다

## Aggregation

**집합 관계**: 객체 간의 약한 소유 관계, 한 객체가 다른 객체를 포함하지만 포함된 객체의 생명 주기는 독립적이다

has-a 관계에 해당하며, 포함된 객체는 소유 객체가 삭제되어도 독립적으로 존재할 수 있다

```java
class Shuttle {
    private List<Unit> units;
}
```

```java
class Zealot implements Unit {
}

class Dragoon implements Unit {
} 
```

셔틀 클래스는 유닛 구현체를 포함하며, 셔틀 클래스가 삭제되어도 질럿/드라군 클래스는 존재할 수 있다


## Composition

**합성 관계**: 객체 간의 강한 소유 관계, 한 객체가 다른 객체를 포함하며 포함된 객체의 생명 주기를 관리한다

part-of 관계에 해당되며, 소유 객체가 삭제되면 포함된 객체도 삭제된다

```java
class Nexus {

    private List<Probe> probes;
    
    public generate() {
        probes.add(new Probe());
    }
    
    @BeforeDestroy
    public void close() {
        probes = null;
    }
}
```

넥서스 클래스는 프로브 클래스를 포함하며, 프로브 클래스의 생명주기를 관리한다

## Inheritance

**상속 관계**: 한 클래스가 다른 클래스의 속성과 메서드를 물려받아 사용하는 관계

is-a 관계에 해당하며, 클래스 간의 계층 구조를 형성한다

```java
abstract class Unit {
    
    protected int hp;
    protected int power;
    protected int cost;
    
    public void attack(Unit target) {
        int targetHp = target.getHp();
        target.setHp(targetHp - this.power);
    } 
}
```

```java
class Zealot extends Unit {
    
    public void move() {
        
    }
}
```

질럿 클래스는 유닛 추상 클래스를 상속받아 기능을 확장한다

## Realization

**실현 관계**: 인터페이스와 구현체의 관계

다형성을 활용하여 유연한 설계/구현을 할 수 있다

```java
interface TribalBase<Worker> {
    
    Worker generate();
    
}
```

```java
class CommandCenter implements TribalBase<SCV> {
    
    SCV generate() {
        return new SCV();
    }
}

class Nexus implements TribalBase<Probe> {
    
    Probe generate() {
        return new Probe();
    }
}

class Hatchery implements TribalBase<Drone> {
    
    Drone generate() {
        return new Drone();
    }
}
```

일꾼을 생산하는 TribalBase 인터페이스를 정의하고, 각 종족에 따라 구현체 클래스를 만든다
