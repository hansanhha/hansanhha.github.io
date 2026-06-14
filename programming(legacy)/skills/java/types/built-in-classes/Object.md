---
layout: default
title:
---

#### index
- [Object](#object)
- [equals, hashCode](#equals-hashcode)
- [toString](#tostring)
- [getClass](#getclass)
- [clone](#clone)
- [wait, notify, notifyAll](#wait-notify-notifyall)
- [finalize](#finalize)


## Object

최상위 클래스 상속 관계를 가진 타입으로 자바의 모든 클래스는 암묵적으로 Object를 상속한다

모든 타입을 다룰 수 있는 공통 타입으로 다형성을 활용할 수 있으며 객체로서 기본적으로 가져야 할 메서드를 제공한다 

자바의 객체는 heap 영역에 저장되며 Object 클래스의 메서드는 method area에 메타데이터로 저장된다


## equals, hashCode

객체 비교와 해시 기반 컬렉션(HashMap, HashSet)에서 사용하는 메서드로 두 객체가 동등한지 판단하기 위해 사용된다  

### identity

동일성이란 객체의 참조가 같은지를 의미하는 것으로 `==` 연산자를 사용하여 비교한다

`==` 연산자를 객체 간에 사용하는 경우 참조 변수가 가리키는 메모리 주소를 기준으로 비교하므로 동일한 객체를 가리킬 경우에만 true를 반환한다

```java
@Test
void objectIdentityTest() {
    Object o1 = new Object();
    Object o2 = new Object();

    Object o1_other = o1;

    // false: identity of two different objects
    assertThat(o1 == o2).isFalse();

    // true: identity of two variable that has same reference
    assertThat(o1 == o1_other).isTrue();
}
```

### equality

동등성은 객체의 상태(값)가 같은지를 의미하는 것으로 데이터를 기준으로 비교하여 같은 데이터를 가진 두 객체는 동등하다고 논리적으로 판단한다

같은 데이터를 가진 두 객체를 동등하다고 판단하려면 비교하려는 객체에서 Object의 equals 메서드를 오버라이딩해야 한다

자바에서 equals와 hashCode 메서드는 밀접한 관계를 가지는데, 이는 해시 기반 자료구조(HashMap, HashSet, HashTable 등)에서는 요소를 비교하기 위해 두 메서드를 함께 사용하기 때문이다

해시 기반 자료구조의 객체 비교 로직은 일반적으로 hashCode 값이 다르면 서로 다른 객체라고 판단하고, 같다면 equals 메서드로 객체가 논리적으로 같은지 확인한다

```java
@Test
void objectEqualityTest() {
    Person p1 = new Person("hansanhha", "developer", 10);
    Person p2 = new Person("hansanhha", "developer", 10);

    // false: identity of two different objects
    assertThat(p1 == p2).isFalse();

    // true: equality of two different objects that has same state
    assertThat(p1.equals(p2)).isTrue();
}
```

### equals

Object 클래스의 equals 메서드는 기본적으로 두 참조 변수의 메모리 주소를 비교한다

```java
public boolean equals(Object obj) {
    return (this == obj);
} 
```

### hashCode

Object 클래스의 hashCode 메서드는 native 라이브러리를 호출하여 객체의 메모리 주소를 해싱한 값(고유 식별 번호)을 반환한다

```java
@IntrinsicCandidate
public native int hashCode();
```

### equals, hashCode overriding

아래의 Person 클래스의 상태(필드)를 기준으로 동등한지 판단하기 위해 equals와 hashCode를 재정의한다

```java
public class Person {

    String name;
    String job;
    int age;

    public Person(String name, String job, int age) {
        this.name = name;
        this.job = job;
        this.age = age;
    }

    // equals overriding for equality comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Person person = (Person) obj; // down casting
        return Objects.equals(name, person.name)
                && Objects.equals(job, person.job)
                && age == person.age;
    }

    // hashCode overriding according to equals overriding
    @Override
    public int hashCode() {
        return Objects.hash(name, job, age);
    }

}
```

자바에는 equals와 hashCode 메서드가 반드시 지켜야 하는 규칙이 있다

사용자가 각 메서드를 오버라이딩하더라도 객체 비교와 해시 기반 자료구조에서 정상적으로 동작하도록 하기 위함이다

#### equals 오버라이딩 규약

- 반사성(reflexive): x.equals(x)는 항상 true를 반환해야 한다
- 대칭성(symmetric): x.equals(y)가 true라면 y.equals(x)도 true를 반환해야 한다
- 추이성(transitive): x.equals(x)가 true이고 y.equals(z)가 true이면 x.equals(z)도 true를 반환해야 한다
- 일관성(consistent): 객체 상태가 변하지 않는 한 x.equals(y)는 동일한 결과를 반환해야 한다
- null과의 비교: x.equals(null)은 항상 false를 반환해야 한다

#### hashCode 오버라이딩 규약

- 두 객체의 equals()가 true라면 반드시 동일한 hashCode()를 반환해야 한다
- 두 객체의 equals()가 false라면 동일한 hashCode를 반환할 수도 있고 아닐 수도 있다 (오버라이딩하기 나름이지만 이 경우에 충돌 가능성이 존재한다)
- 동일한 객체의 hashCode()는 항상 같은 값을 반환해야 한다

equals의 결과 값이 참인 객체는 같은 해시코드를 가져야 하는 규칙에 따라 equals 메서드를 오버라이딩할 때 hashCode도 반드시 오버라이딩해줘야 한다

만약 둘 중 하나만 오버라이딩한 경우 객체 비교 동작 과정에서 오류가 발생할 수 있다
- equals만 재정의한 경우: 해시 기반 컬렉션에서 객체를 제대로 찾지 못한다
- hashCode만 재정의한 경우: 논리적으로 동일한 객체로 간주될 수 없다

또한 equals에서 비교하는 모든 필드는 반드시 hashCode에도 포함되어야 한다

해시 기반 컬렉션은 객체의 해시코드를 먼저 비교한 후 equals를 호출하여 동등성을 확인하기 때문에 equals 메서드에 사용된 필드가 hashCode 메서드에도 포함되지 않으면 동등한 객체를 찾지 못해서 해시 기반 컬렉션에서 객체를 찾지 못하는 문제가 생길 수 있다


## toString

toString 메서드의 기본 구현은 클래스명과 hashCode 값을 반환한다

자바는 모든 클래스에서 toString 메서드를 사람이 읽을 수 있는 형태로 간단히 객체를 표현할 수 있는 문자열 값을 반환하도록 오버라이딩할 것을 권장한다

```java
public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
}
```

자바의 toString 메서드는 객체를 문자열로 변환할 필요가 있을 때 암묵적으로 호출되는 규칙을 가진다
- 문자열과 객체의 `+` 연산
- System.out.println
- String.format, StringBuilder.append, StringBuffer.append

```java
Person p1 = new Person("hansanhha", "developer", 10);

// hansanhha.classes.built_in.object.Person@a413a2b5
System.out.println(p1);
```


## getClass

getClass 메서드는 객체의 런타임 클래스를 나타내는 Class 객체를 반환한다

리플렉션에서 클래스 정보에 접근하기 위해 많이 사용된다

```java
@IntrinsicCandidate
public final native Class<?> getClass();
```

```java
Person p1 = new Person("hansanhha", "developer", 10);

assertThat(p1.getClass().getName()).isEqualTo("hansanhha.classes.built_in.object.Person");
assertThat(p1.getClass().getSimpleName()).isEqualTo("Person");
```


## clone

clone은 현재 인스턴스를 복제하여 새로운 Object 타입의 객체를 반환한다

new 키워드를 이용하여 모든 필드를 직접 복사하는 코드를 작성할 필요없이 원본 객체의 상태를 유지하면서 새로운 객체를 생성할 수 있다

그러나, 이 메서드는 다음과 같은 제약이 있다
- 복제될 클래스가 Cloneable 인터페이스를 구현해야한다 -> 구현하지 않고 호출할 시 CloneNotSupportedException 예외가 발생한다
- 얕은 복사(shallow copy)를 수행한다
- 반환 타입이 Object이므로 명시적 형변환이 필요하다
- protected 접근 제어자를 가지기 때문에 private 또는 서브 클래스에서만 호출할 수 있다

```java
@IntrinsicCandidate
protected native Object clone() throws CloneNotSupportedException;
```

```java
@Test
void cloneMethod() {
    Person person = new Person("hansanhha", "developer", 10);
    Person clone = person.clone();

    assertThat(person).isEqualTo(clone);
}
```

### shallow copy vs deep copy

shallow copy는 clone 메서드의 기본 동작으로 객체의 primitive type 필드 값은 새로 복제되지만 reference type은 새 객체를 생성하지 않고 기존 객체의 메모리 주소만 복제한다

따라서 원본 객체와 복제 객체가 동일한 객체를 참조하게 되어 한 쪽에서 해당 객체를 수정하면 다른 객체에 영향을 준다 

```java
@Test
void shallowCopyReferenceTypeTest() {
    ShallowAddress seoul = new ShallowAddress("seoul");
    ShallowOrder order = new ShallowOrder("12345", seoul);
    ShallowOrder cloneOrder = order.doClone();

    // Order 클래스는 Object.toString을 호출하는데, 이 메서드는 객체의 참조 주소값을 사용한다
    // 복제된 클래스는 기존 객체와 다른 메모리 주소를 가지기 때문에 동일성을 만족하지 못한다
    assertThat(order).isNotEqualTo(cloneOrder);

    // clone 메서드는 shallow copy로 동작하여 참조 타입의 필드에 대해 메모리 주소만 복제한다
    // 원본 객체와 복제 객체 모두 객체를 공유하기 때문에 해당 참조의 상태를 변경하면 다른 객체에 모두 영향을 준다
    cloneOrder.getAddress().setCity("busan");
    assertThat(cloneOrder.getAddress().getCity()).isEqualTo(order.getAddress().getCity());
}
```

deep copy는 참조 타입 필드까지 새롭게 복제하는 방식으로 원본 객체와 동일한 객체를 공유하지 않는다

다만 참조 타입의 객체가 많거나 각 객체의 생성 비용이 클수록 복제 비용이 커질 수 있다

자바에서 deep copy는 아래와 같이 직접 구현해야 한다

DeepAddress는 DeepOrder에서 참조하는 객체로 Cloneable 인터페이스를 구현하고 자신의 상태를 기반으로 새로운 인스턴스를 생성하는 동작으로 clone 메서드를 재정의한다

DeepOrder도 마찬가지로 Cloneable 인터페이스를 구현한 뒤 clone 메서드에서 복제 DeepOrder 인스턴스의 deepAddress 필드에 DeepAddress의 clone 메서드를 통해 새로운 참조 타입의 객체를 할당한다  

```java
public class DeepAddress implements Cloneable {

    String city;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new DeepAddress(city);
    }
}
```

```java
public class DeepOrder implements Cloneable {

    DeepAddress deepAddress;

    @Override
    public DeepOrder clone() {
        try {
            DeepOrder cloned = (DeepOrder) super.clone();
            cloned.deepAddress = (DeepAddress) deepAddress.clone();
            return cloned;
        } catch (
                CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }
}
```

위의 DeepAddress와 DeepOrder에 대한 deep copy 테스트 코드는 아래와 같다

```java
@Test
void deepCopyReferenceTypeTest() {
    DeepAddress seoul = new DeepAddress("seoul");
    DeepOrder order = new DeepOrder("12345", seoul);
    DeepOrder cloneOrder = order.doClone();

    // deep copy는 참조 타입 필드까지 새로운 객체를 생성하기 때문에 원본 객체와 복제 객체가 동일한 객체를 공유하지 않는다
    cloneOrder.getDeepAddress().setCity("busan");
    assertThat(cloneOrder.getDeepAddress().getCity()).isNotEqualTo(order.getDeepAddress().getCity());
}
```

## wait, notify, notifyAll

wait(), notify(), notifyAll() 메서드는 스레드 간 동기화 메커니즘에서 사용된다

멀티스레딩 환경에서 여러 스레드가 공유 자원에 접근하고자 할 때 스레드 간 모니터 락을 위해 사용되며 synchronized 블록 안에서만 호출할 수 있다  

wait: 무기한 대기 또는 주어진 파라미터 값에 따라 일정 시간만 대기하는 메서드

notify: 같은 모니터를 사용하는 대기 중인 스레드 중 랜덤으로 하나를 깨운다

notifyAll: 같은 모니터를 사용하는 대기 중인 모든 스레드를 깨운다 (다만 모든 스레드가 실행되지 않고 순차적으로 락을 획득한 스레드부터 실행될 수 있음)

아래의 SharedResource는 공유 자원 필드인 data에 대한 동기화를 위해 wait와 notify 메서드를 사용한다 

```java
public class SharedResource {

    private String data;
    private boolean lock;

    public synchronized void produce(String value) throws InterruptedException {
        // 다른 스레드가 락을 가져갔다면 대기한다
        while (lock) {
            wait();
        }
        data = value;
        lock = true;
        System.out.println("produced: " + data);
        
        // 락을 가진 스레드가 작업을 끝내고 대기 중인 스레드를 깨운다
        notify();
    }

    public synchronized String consume() throws InterruptedException {
        while (!lock) {
            wait();
        }
        lock = false;
        System.out.println("consumed: " + data);
        notify();
        return data;
    }
}
```


## finalize

```java
@Deprecated(since="9", forRemoval=true)
protected void finalize() throws Throwable { }
```

객체가 가비지 컬렉터에 의해 소멸되기 직전에 호출되어 직접 객체를 정리할 수 있는 메서드로 protected 접근 제어자를 가진다

이 메서드는 자바 9부터 @Deprecated 되어 사용이 권장되지 않는데 그 이유는 다음과 같다

예측 불가능한 실행 지점: gc가 실행될 때만 finalize 메서드가 호출되므로, 객체가 언제 정리될지 예측할 수 없다 (즉시 필요한 리소스 해제 작업을 맡기기 부적절함)

성능 저하: finalize를 구현한 객체는 gc가 객체를 바로 수거하지 않고 "finalization queue"에 넣어 비동기 처리한다 (성능 저하 및 메모리 누수 가능성 증가)

부족한 신뢰성: finalize 내에서 예외가 발생하면 무시되거나 예상하지 못한 오류가 발생할 수 있다

위와 같은 이유로 deprecated 되었고 대신에 try-with-resources 문 또는 명시적인 close 메서드를 사용하여 리소스를 정리하는 것이 안전하다  