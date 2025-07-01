---
layout: default
title:
---

[Inheritance](#inheritance)

[Abstraction](#abstraction)

[Encapsulation](#encapsulation)

[Polymorphism](#polymorphism)

[Summary](#summary)

## Inheritance

객체지향에서 데이터 모델은 객체로 표현한다

객체의 속성(필드)는 모델의 데이터를 나타내고, 객체의 메서드(행위)는 책임을 나타낸다

상속은 기존 클래스(부모, 슈퍼 클래스)의 속성과 메서드를 새로운 클래스가 물려받아 재사용하거나 확장하는 것을 의미한다

또한 상속을 통해 클래스 간의 계층 구조를 만들어서 객체 간의 관계를 정의할 수 있다

`Unit` -> `TerranUnit` -> `Marine` 과 같은 계층 구조

#### 상속의 동작 방식

1. 부모/슈퍼 클래스의 private을 제외한 속성과 메서드는 자식 클래스에게 상속된다
2. 자식 클래스는 부모/슈퍼 클래스의 필드를 재정의할 수 있다
3. 자식 클래스는 이를 직접 사용하거나 부모/슈퍼 클래스의 메서드를 재정의(오버라이딩)하여 커스텀할 수 있다

## 상속의 이점

### 코드 재사용

#### 부모 클래스의 공통 속성 정의

유닛이라는 최상위 부모 클래스에 유닛에게 필요한 공통 속성을 정의하고 각 유닛 타입에 따라 하위 클래스를 만들어서 부모의 속성을 이용한다 

```java
class Unit {

    // 유닛에게 필요한 공통 속성
    protected int hp;
    protected int power;
    protected int cost;
    
    // getter, setter
}

class Zergling extends Unit {
    
    // 자식 클래스의 추가 속성 정의
    private boolean isAdrenaline;
    
    Zergling() {
        // 부모 클래스 생성자 호출
        super(50, 10);
    }
}
class Marine extends Unit {
    
    Marine() {
        super(50, 12);
    }
}
```

#### 부모 클래스의 공통 메서드 정의

```java
class Unit {
    
    protected int hp;
    protected int power;
    protected int cost;
    
    // 부모 타입으로 데미지 계산
    void attack(Unit target) {
        int targetHp = target.getHp();
        target.setHp(this.power - targetHp);
    }
}
```

#### 자식 클래스에서 부모 클래스에서 정의한 공통 필드와 메서드 사용

```java
Zergling zergling = new Zergling();
Marine marine = new Marine();

marine.attack(zergling);

int marinHp = marine.getHp();
int zerglingHp = zergling.getHp();
```

### 기능 확장

자식 클래스에서 부모 클래스로부터 물려받은 메서드를 다양한 방식으로 확장/수정할 수 있다

#### 기능 추가

자식 클래스에서 추가적인 메서드를 정의한다

```java
class Marine extends Unit {

    // 자식 클래스의 추가 메서드 정의
    stimpak() {
        setHp(hp - 10);
        setPower(power + 2);
    }
}
```

#### 오버라이딩

메딕 클래스는 부모 클래스의 attack 메서드를 오버라이딩해서 공격하는 대신, 대상 유닛의 체력을 올려주는 heal 메서드를 수행한다

```java
class Medic extends Unit {
    
    @Override
    void attack(Unit target) {
        heal(target);
    }
    
    private void heal(Unit target) {
        int targetHp = target.getHp();
        target.setHp(targetHp);
    }
}
```

#### 부모 클래스의 메서드를 확장하면서 기존 로직 호출

마린 클래스는 부모 클래스의 attack 메서드 로직을 호출한 후 추가 작업(재장전)을 수행한다

```java
class Marine extends Unit {
    
    @Override
    void attack(Unit target) {
        super.attack();
        reload();
    }
    
    private void reload() {
        this.magazineCapaticy = 100;
    }
}
```

### 상속 주의점

#### 강한 결합

상속을 이용하면 자식 클래스의 필드와 메서드를 부모 클래스에게 의존하기 때문에 부모 클래스에서 변경이 발생하면 자식 클래스가 영향을 받는다 -> 강한 결합

#### 복잡성

상속 계층이 깊어질수록 클래스 간의 관계가 복잡해지면서 코드를 한 눈에 파악하기 어렵다

책임을 적절히 분배하지 못하면 잘못된 객체 관계를 정의하게 되고, 부모 클래스의 변경이 발생했을 때 자식 클래스의 로직을 모두 변경해야 할 가능성도 있다

-> 유지보수성 부족

#### 단일 상속

자바는 상속으로 인한 모호성 문제를 방지하기 위해서 단 하나의 클래스만 상속할 수 있게 제한한다 -> 유연성 부족

대신 인터페이스 구현을 통해 다중 상속과 비슷한 효과를 누릴 수 있다 

### 상속의 대안: has-a

객체 관계는 크게 두 가지 유형으로 분류할 수 있다

#### is-a 관계 

어떤 객체가 다른 객체의 서브 타입 관계인 경우

강아지는 동물이다 (a dog is an animal)

상속은 is-a 관계에 해당됨

#### has-a 관계

어떤 객체가 다른 객체를 사용하거나 포함하는 경우

구성(composition) 또는 집합(aggregation) 관계가 has-a 관계에 해당함

상속의 is-a 관계를 선택하는 대신 has-a 관계를 활용하면 상속이 가지는 몇 가지 문제를 해결할 수 있음

#### 예시 코드

위의 예시에서 사용한 Unit 부모 클래스는 필드와 메서드를 모두 가지고 있다

로직을 중앙화하기엔 좋지만 한 클래스에서 많은 책임을 가지면 로직이 변경되었을 때 모든 자식 클래스에게 영향을 끼치게 된다

객체 관계를 has-a 관계로 변경하기 위해 유닛 클래스의 필드와 메서드를 각 행위에 따른 책임을 잘게 나눠 인터페이스로 분리한다 

유닛 인터페이스는 유닛의 정보를 나타내고, 유닛 커맨드 인터페이스는 유닛의 명령 작업을 수행한다

```java
interface Unit {
    
    int getHp();
    int getPower();
    int getCost();
}

interface UnitCommand {

    void move(Coordinate coordinate);
    void attack(Unit unit);
}
```

스타크래프트의 유닛 타입은 소형, 중형, 대형으로 나뉘므로 그에 따른 유닛 커맨드 구현체를 만든다 

```java
class SmallUnitCommand implements UnitCommand {

    @Override
    void move(Coordinate coordinate) {
        // 소형 유닛 이동 로직
    }

    @Override
    void attack(Unit unit) {
        // 소형 유닛 공격 로직
    }
}

class MediumUnitCommand implements UnitCommand {}
class LargeUnitCommand implements UnitCommand {}
```

마린 클래스는 유닛 커맨드를 주입받아 이동과 공격 행위를 위임한다 (has-a)

유닛 커맨드의 구현체가 어떤 것으로 변경되어도 마린 클래스의 코드를 변경할 필요가 없게 되었다 

```java
class Marine implements Unit {
    
    private UnitCommand unitCommand;
    
    Marine(UnitCommand unitCommand) {
        this.unitCommand = unitCommand;
    }
    
    void move(Coordinate coordinate) {
        unitCommand.move(coordinate);
    }
    
    void attack(Unit unit) {
        unitCommand.attack(unit);
    }
    
    // getHp(), getPower(), getCost()
}
```

## Abstraction

OOP에서 추상화는 **객체가 수행해야 하는 작업에 초점을 맞춰서 불필요한 세부사항은 제외하고 중요한 부분을 표현하는 것**을 말한다

객체가 "무엇을 해야 하는지"를 **드러내고** "어떻게 구현되었는지"는 **감춘다**

#### 상속과 추상화

상속은 객체 간의 계층 구조를 정의하며 자식 클래스가 부모 클래스의 속성과 메서드를 물려받고 기능을 확장하거나 재정의하는 것이라면

추상화는 다형성과 캡슐화에 대한 기틀을 다지는 것으로 어떤 객체의 핵심적인 속성과 동작만을 정의하고, 세부 구현 사항은 숨기는 작업이다

추상 클래스와 인터페이스를 활용하여 객체의 속성이나 메서드를 추상화할 수 있다

### 추상 클래스

구현체가 공통된 동작을 수행해야 될 경우 추상 클래스를 사용하면 유용하다

UnitFactory 추상 클래스는 유닛을 생성하는 책임을 맡은 객체로, 유닛을 생성하는 핵심 로직은 추상 메서드로 정의하여 구현체에게 맡기고 유닛 생성 비용에 따른 게임 자원 관리는 부모 클래스에서 ResourcesManager 필드를 통해 수행한다

```java
abstract class UnitFactory {

    private final ResourcesManager resourcesManager;

    protected UnitFactory(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    <T extends Unit> T generate(String unitName) {
        // 추상 메서드 호출
        Unit unit = generateUnit(unitName);
        
        // 유닛 생성에 따른 게임 자원 감소
        resourcesManager.decrease(unit.getCost());
    }

    // 추상 메서드 정의
    abstract <T extends Unit> T generateUnit(String unitName);
}
```

```java
class Barrack extends UnitFactory {
    
    Barrack(ResourcesManager resourcesManager) {
        super(resourcesManager);
    }
    
    @Override
    <T extends Unit> TgenerateUnit(String unitName) {
        if (unitName.equals("marine")) {
            return new Marine();
        }
        else if (unitName.equals("firebat")) {
            return new Firebat();
        }
    }
}
```

### 인터페이스

다양한 구현체가 필요한 경우 인터페이스를 활용한다

인터페이스는 추상 클래스처럼 필드를 구현체에게 물려줄 수 없고 오로지 메서드만 정의할 수 있다

추상 클래스를 사용했을 때와 다르게, 인터페이스 구현체가 유닛을 생성하고 ResourcesManager 객체에게 자원 관리 작업을 위임하는 로직을 수행한다  

```java
interface UnitFactory {

    <T extends Unit> T generate(String unitName);
}
```

```java
class Barrack implements UnitFactory {

    private final ResourcesManager resourcesManager;
    
    Barrack(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    @Override
    <T extends Unit> T generateUnit(String unitName) {
        Unit unit;
        
        if (unitName.equals("marine")) {
            unit = new Marine();
        }
        else if (unitName.equals("firebat")) {
            unit = new Firebat();
        }
        
        resourcesManager.decrease(unit.getCost());
        
        return unit;
    }
}
```

다른 유닛을 생성하고 싶으면 인터페이스나 추상 클래스의 변경 없이 다른 구현체로만 변경하면 된다

추상화를 사용하면 다양한 상황에서 객체를 재사용할 수 있다

```java
UnitFactory barrack = new Barrack(resourcesManager);
UnitFactory gateway = new Gateway(resourcesManager); 

Marine marine1 = barrack.generate("marine");
Marine firebat = barrack.generate("firebat");

Zealot zealot = gateway.generate("zealot");
Zealot dragoon = gateway.generate("dragoon");
```

### 인터페이스와 추상클래스 조합

다양한 구현체가 공통된 동작을 수행하는 경우 인터페이스와 추상 클래스를 모두 활용할 수 있다

유닛 팩토리 인터페이스를 정의하고, 유닛 생성에 따른 자원 관리는 AbstractUnitFactory 추상 클래스에서 수행한다

```java
interface UnitFactory {
    
    <T extends Unit> T generate(String unitName);
}
```

```java
abstract class AbstractUnitFactory implements UnitFactory {

    private final ResourcesManager resourcesManager;

    protected UnitFactory(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    <T extends Unit> T generate(String unitName) {
        // 추상 메서드 호출
        Unit unit = generateUnit(unitName);

        // 유닛 생성에 따른 게임 자원 감소
        resourcesManager.decrease(unit.getCost());
    }

    // 추상 메서드 정의
    abstract <T extends Unit> T generateUnit(String unitName);
} 
```

이러한 인터페이스, 추상 클래스의 조합은 스프링 코드에서 심심치 않게 확인할 수 있다

스프링 애플리케이션을 구성하는 역할을 수행하는 ApplicationContext 인터페이스를 정의하고, 구현체가 공통적으로 처리해야 할 작업은 AbstractApplicationContext가 담당한다

## Encapsulation

캡슐화는 **외부에서 객체의 내부 구조를 직접 접근하지 못하도록 정보를 숨기고(정보 은닉), 필요한 부분만 공개하여 객체의 안정성과 일관성을 유지**하는 기법이다

객체가 자신의 상태를 스스로 관리할 수 있게 **데이터(필드, 메서드)를 안전하게 보호**하면서 객체 간의 상호작용을 제어한다

데이터를 감춤으로써 내/외적으로 얻을 수 있는 이점은 다음과 같다
- 외부에서 데이터 조작을 할 수 없으므로 엉뚱한 값이 설정되는 것을 방지한다
- 내부 구현을 캡슐화하여 외부 코드에 영향을 미치지 않으면서 내부 구현을 변경할 수 있다
- 민감한 데이터에 대한 접근을 제한할 수 있다

캡슐화는 접근 제어자, getter/setter, 추상화 등을 통해 구현할 수 있다

#### 접근 제어자를 통해 노출 범위를 결정

public, protected, package(default), private

#### getter/setter 메서드

getter/setter는 필드에 직접 접근하지 않고, 메서드를 통해 간접적으로 접근할 수 있도록 하는 메서드이다

```java
class Unit {
    
    // 외부 노출 X, 클래스 내부에서만 접근 가능
    private int hp;
    private int power;
    private int cost;
    
    // power 값 반환
    public int power() {
        return power;
    }
    
    // 검증 로직을 거치고 power 필드를 조작하는 setter 메서드
    public upgrade() {
        if (this.power == MAX_POWER) {
            return;
        }
        this.power++;
    }
}
```

```java
Marine marine = new Marine();

// marine.power = 10000; 컴파일 에러, private 필드에 직접 접근할 수 없음
marine.upgrade(); // 올바른 접근
```

## Polymorphism

다형성은 **추상화된 동작을 여러 형태로 수행**할 수 있는 기법이다

동일한 이름의 메서드나 인터페이스를 호출해도, 런타임 시 실제 객체의 타입에 따라 다른 동작을 수행하도록 구현할 수 있다

클라이언트는 인터페이스 타입에 의존하면 어느 구현체가 오더라도 클라이언트는 알 필요가 없으며 구현체가 변경되더라도 영향을 받지 않는다 -> 코드의 유연성/재사용성을 높임

### 다형성 종류

다형성은 크게 컴파일타임 다형성과 런타임 다형성으로 나뉜다

#### 컴파일타임 다형성, 오버로딩(Overloading)

컴파일타임 다형성은 메서드 오버로딩을 통해 구현한다

오버로딩은 같은 이름의 메서드를 매개변수의 개수나 타입에 따라 여러 번 정의하는 기술이다 

메서드에 전달하는 인자에 따라 컴파일 시점에 호출할 메서드를 결정한다 -> **정적 바인딩**

메서드 오버로딩의 조건
- 동일한 메서드 이름
- 매개변수의 개수, 타입 또는 순서가 달라야 함
- 반환 타입만 다르면 오버로딩이 되지 않음

UnitFactory 클래스는 주어진 종족에 따른 종족 유닛을 생성하고, 모든 종족의 유닛을 생성하기도 한다

```java
class UnitFactory {
    
    ProtossUnit generate(Protoss protoss) {
    }
    
    TerranUnit generate(Terran terran) {
    }
    
    ZergUnit generate(Zerg zerg) {
    }

    List<Unit> generateAll(Protoss protoss, Terran terran, Zerg zerg) {
    }
}
```

#### 런타임 다형성, 오버라이딩(Overriding)

런타임 타형성은 메서드 오버라이딩과 다형적 참조를 통해 구현한다

다형적 참조란 부모 클래스 타입의 참조 변수가 여러 자식 클래스 객체를 참조하는 것을 말한다 

이를 통해 한 가지 인터페이스로 다양한 동작을 수행할 수 있으며 호출 시점(실행 시점)에 참조 변수가 참조하는 객체의 실제 타입에 맞는 메서드를 실행한다 -> **동적 바인딩**

UnitFactory 인터페이스에 정의한 유닛 생성 메서드를 각 종족에 따라 재정의하여 종족 유닛을 생성한다 

```java
interface UnitFactory<T> {
    
    T generate();
}
```

```java
class ProtossUnitFactory<ProtossUnit> {

    @Override
    ProtossUnit generate() {
    }
}

class TerranUnitFactory<TerranUnit> {
    
    @Override
    TerranUnit generate() {
    }
}

class ZergUnitFactory<ZergUnit> {

    @Override
    ZergUnit generate() {
    }
}
```

## Summary

상속: 객체 관계 정의, 필드/메서드 재사용, 기능 확장 및 재정의

추상화: 객체 핵심 로직/행위 표현, 불필요한 세부 구현 감춤

캡슐화: 정보 은닉, 데이터 보호

다형성: 기능 확장

