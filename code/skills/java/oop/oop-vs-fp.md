---
layout: default
title:
---

[주요 함수형 프로그래밍 언어](#주요-함수형-프로그래밍-언어)

객체지향과 함수형 프로그래밍 패러다임 비교
- [핵심 철학](#핵심-철학)
- [주요 개념](#주요-개념)
- [상태 관리](#상태-관리)
- [코드 구조](#코드-구조)
- [병렬 처리](#병렬-처리)
- [장단점](#장단점)

## 주요 함수형 프로그래밍 언어

Haskell
- 순수 함수형 프로그래밍 언어
- 강한 타입 시스템, 지연 평가

Scala
- 객체지향, 함수형 프로그래밍 모두 지원
- JVM 생태계, 자바와 호환 가능

Clojure
- Lisp 계얼, JVM 생태계

Erlang/Elixir
- 분산 시스템과 병렬 처리 함수형 언어

OCaml
- 강력한 타입 시스템 함수형 언어
- 시스템 프로그래밍에서도 사용

## 핵심 철학

#### OOP

**객체 중심 설계**

객체는 상태와 행동을 가진 독립적인 단위로

객체 지향은 현실 세계 개체/개념을 프로그램 내의 객체로 모델링하고 객체 간의 협력을 통해 문제를 해결한다 

```java
class Marine {
    
    // 마린 유닛의 상태 (체력, 공격력, 생산 비용)
    private int hp;
    private int power;
    private int cost;
    
    // 마린 유닛의 동작
    public void attack(Unit target) {
        int targetHp = target.getHp();
        target.setHp(target - this.power);
    }
}
```

#### FP

**함수 중심 설계**

프로그램은 입력을 받아 출력을 반환하는 순수 함수의 조합으로 구성된다

상태 변경을 최소화하며 불변성을 유지한다

객체와 달리 상태와 행동을 분리하여 문제 해결 과정을 함수로 표현한다

```scala
// 입력을 받아 출력을 반환하는 함수
def attack(targetHp:Int, attackerPower: Int): Int = targetHp - attackerPower

val marinePower = 10
val zealotHp = 5

val remainingHp = attack(zealotHp, marinePower)
println(remainingHp)
```

## 주요 개념

#### OOP

**상속**: 객체 관계 정의, 필드/메서드 재사용, 기능 확장 및 재정의

```java
abstract class Unit {
    
    protected int hp;
    
    protected abstract void attack();
}

class Marine extends Unit {
    
    @Override
    public void attack() {
    }
}
```

**추상화**: 객체 핵심 로직/행위 표현, 불필요한 세부 구현 감춤

```java
interface UnitFactory {
    Unit generate();
}
```

**캡슐화**: 정보 은닉, 데이터 보호

```java
class Marine extends Unit {
    
    private int power;
    
    private void upgrade() {
        this.power++;
    }
}
```

**다형성**: 기능 확장

```java
interface UnitFactory<Unit> {
    Unit generate();
}

class Nexus implements UnitFactory<Probe> {
    Probe generate() {
    }
}

class Hatchery implements UnitFactory<Drone> {
    Done generate() {
    }
}
```

[자세한 내용](./oop%204%20pillars.md)

#### FP

**순수 함수(Pure Function)**: 같은 입력에 대해 항상 같은 출력을 반환하며, 부작용을 발생시키지 않는 함수

```scala
def square(x: Int): Int = x * x

println(square(4)) // 같은 입력(4)에 대한 같은 출력(16) 
```

**고차 함수(Higher-Order Function)**: 함수를 인자로 전달하거나 반환하는 함수

```scala
def applyFunction(x: Int, f: Int => Int): Int = f(x)

val result = applyFunction(5, sqaure)
println(result) // 출력: 25
```

**불변성(Immutability)**: 데이터는 변경되지 않으며, 새 데이터를 생성함

```scala
val originalList = List(1, 2, 3)

val newList = originalList.map(_ * 2) // 새 리스트 생성, 원본 변경 X

println(newList)       // 출력: List(2,4,6)
println(originalList)  // 원본 리스트 출력: List(1,2,3) 
```

**일급 객체(First-Class Citizens)**: 함수가 변수처럼 전달되거나 반환될 수 있음

```scala
val multiplyByTwo: Int => Int = _ * 2 // 함수를 변수에 할당
val numbers = List(1, 2, 3)
println(numbers.map(multiplyByTwo)) // 함수를 인자로 전달
```

## 상태 관리

#### OOP

객체 내부에 상태를 저장하며, 메서드를 통해 조작한다

프로그램의 상태 변화가 객체 간의 상호작용에 의해 발생한다

```java
// 질럿, 마린 생산
Zealot zealot = gateway.generate();
Marine marine = barrack.generate();

// 공격 메서드를 통해 질럿의 상태 변화 발생
marine.attack(zealot);
```

#### FP

상태 변경을 피하며 불변성을 유지한다

상태를 수정하는 대신 새로운 상태를 반환한다

```scala
case class Counter(value: Int) {
  // 새로운 Conuter 클래스 반환
  def increment: Counter = Counter(value + 1)
  def decrement: Counter = Counter(value - 1)
}

val counter = Counter(0)
val newCounter = counter.increment.increment.decrement

println(counter)           // 출력: 0 (원본 변경 X)
println(newConuter.value) // 출력: 1 (새로운 상태)
```

## 코드 구조

#### OOP

상태와 행동을 캡슐화한 클래스를 사용한다

코드가 객체를 중심으로 구조화되는 특징을 가진다 -> 도메인 모델링에 적합

```java
// 상태와 행동이 한 객체에 존재한다
class Hydralisk {
    
    private int hp;
    private int power;
    private int cost;
    private int speed;
    
    public void upgrade() {
        if (this.power == MAX_POWER) {
            return;
        }
        
        this.power++;
    }
}
```

```java
// 히드라리스크의 upgrade 메서드를 통해 power 필드를 조작한다
Hydralisk hydralisk = new Hydralisk();
hydralisk.upgrade();
```

#### FP

데이터와 함수의 조합으로 문제를 해결한다

함수 체이닝이나 고차 함수를 통해 간결하고 읽기 쉬운 코드를 작성할 수 있다

```scala
val numbers = List(1, 2, 3, 4, 5)

val sumOfSquares = numbers
  .filter(_ % 2 == 0)
  .map(sqaure)
  .sum

println(sumOfSquares) // 출력: 20 (2^2 + 4^2)
```

## 병렬 처리

#### OOP

상태 변경이 빈번하므로 병렬 처리와 동기화가 어렵다

공유 상태를 관리하기 위해 락(Lock)과 같은 동기화 메커니즘이 필요함

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Stream API 사용
int sumOfSquares = numbers.parallelStream()
                          .mapToInt(n -> n * n)
                          .sum();

System.out.println(sumOfSquares) // 출력: 385
```

#### FP

불변성과 순수 함수로 인해 병렬 처리에 유리하다

공유 상태가 없으므로 동기화 문제를 회피할 수 있다

```scala
val numbers = (1 to 10).toList

// 각 숫자를 비동기적으로 제곱한 뒤 합산
val parallelResult = numbers.par.map(sqaure).sum

println(parallelResult) // 출력: 385 (1^2 + 2^2 + ... + 10^2)
```

## 장단점

#### OOP

장점
- 현실 세계 모델링에 적합
- OOP의 특징으로 인해 코드의 모듈성과 재사용성, 유지보수성, 확장성을 증대시킬 수 있음

단점
- 복잡한 상속 구조로 인해 유지보수를 어렵게 만들 수 있음
- 상태 변경이 많아질 수록 디버깅과 유지보수가 어려워짐

#### FP

장점
- 병렬 프로그래밍에 적합
- 코드가 간결하고 재사용성이 높음

단점
- 객체 지향 설계로 구현된 시스템과의 통합이 어려움
- 함수형 사고 방식이 객체지향에 비해 생소함








