---
layout: default
title:
---

[generics](#generics)

[제네릭 장점](#제네릭-장점)


## Generics

도입: java se 5 (2004)

제네릭은 타입을 매개변수화하는 것으로 클래스/인터페이스, 메서드에 적용할 수 있으며 적용된 클래스를 제네릭 타입이라고 일컫는다

**타입을 매개변수화**한다는 게 무슨 말일까?

일반적으로 매개변수라 하면 메서드의 파라미터를 생각하게 된다

메서드 호출자가 파라미터에 전달하는 값은 특정 타입(int, String, User 등)으로 국한된다

어떤 로직을 수행하는 클래스나 메서드가 있을 때 여러 타입을 받고 싶으면 어떻게 해야될까?

제네릭을 사용하지 않으면 각 타입에 맞는 구현 로직을 작성해야 한다

다음 DollarBank와 PoundBank 클래스를 살펴보자

```java
public static class DollarBank {
    private final List<Dollar> dollars = new ArrayList<>();

    public void save(Dollar dollar) {
        dollars.add(dollar);
    }

    public String getBalance() {
        return "balance: " + dollars.stream().mapToInt(Dollar::value).sum();
    }
}

public static class PoundBank {
    private final List<Pound> pounds = new ArrayList<>();

    public void save(Pound pound) {
        pounds.add(pound);
    }

    public String getBalance() {
        return "balance: " + pounds.stream().mapToInt(Pound::value).sum();
    }
}

public record Dollar(int value) {}
public record Pound(int value) {}
```

위의 두 클래스는 돈을 넣고, 계좌 잔액을 조회하는 로직을 동일하게 갖고 있지만 Pound 타입과 Dollar 타입을 한꺼번에 지원할 수 없기 때문에 각각 구현됐다 

만약 새로운 로직을 추가하거나 기존 로직을 변경해야 한다면 다른 클래스에도 변경사항을 반영해야 한다

여기서 다른 타입까지 지원해야 한다면 타입이 추가될 때마다 코드의 유지보수성이 떨어질 수 밖에 없다

이번엔 제네릭을 사용하는 코드를 살펴보자

```java
public static class Bank<M extends Money> {
    private final List<M> monies = new ArrayList<>();

    public void save(M money) {
        monies.add(money);
    }

    public String getBalance() {
        return "balance: " + monies.stream().mapToInt(M::value).sum();
    }
}

public interface Money {
    int value();
}

public record Dollar(int value) implements Money {}
public record Pound(int value) implements Money {}
```

Bank 클래스는 `<M extends Money>` 라는 제한된 타입 매개변수(bounded type parameters)를 선언하여 자신이 처리할 타입을 Money 인터페이스 구현체로 지정한다

앞으로 어떤 타입이 추가되든 Money 인터페이스의 구현체라면 모두 Bank 클래스가 일관되게 처리할 수 있다

제네릭은 타입을 매개변수화할 수 있을 뿐만 아니라 매개변수로 사용할 수 있는 타입 자체를 제한하여 타입 안정성과 유지보수성을 높인다


## 제네릭 장점

#### 타입 안정성

자바는 컴파일 시점에 타입 검사를 통해 제네릭 클래스나 제네릭 메서드에서 다룰 타입을 명확히 지정한다

만약 제네릭 규칙에 위반되는 코드가 있다면 컴파일 오류를 발생시켜서 런타임에 발생할 수 있는 `ClassCastException` 오류를 방지한다

#### 재사용성

위의 Bank 클래스 예시에서 볼 수 있듯이 제네릭을 사용하면 여러 데이터 타입에 대한 동일한 로직을 구성할 수 있다

#### 가독성 향상

컴파일 시점의 타입 검사를 통해 컴파일러는 이미 타입을 알고 있기 때문에 개발자 대신 컴파일러가 타입 캐스팅 코드를 자동으로 적용한다

따라서 코드 작성 시 타입 캐스팅을 줄일 수 있어 가독성을 높일 수 있고 자바는 자동 박싱/언박싱을 지원하기 때문에 기본 타입과 래퍼 객체 간의 변환도 간소화된다

반면 제네릭을 사용하지 않으면 런타임에 어느 타입이 들어갔는지 확실히 알 수 없기 때문에 명시적인 타입 변환 코드를 추가해야 하고, 타입 변환이 부정확한 경우 런타임에 ClassCastException 예외가 발생할 수도 있다

```java
// 제네릭을 사용하지 않은 경우
public class RowTypeCasting {

  public static void main(String[] args) {

    // 타입 매개변수 지정 X
    List rawList = new ArrayList();

    // String, Integer 타입 등 서로 다른 타입의 객체 혼합 가능
    rawList.add("Hello");
    rawList.add(1234);

    // List에 어느 타입이 들어간지 런타임에 알 수 없기 때문에 요소를 꺼낼 때 타입 캐스팅이 명시적으로 필요하다
    String first = (String) rawList.get(0);
    System.out.println(first);

    // 두 번째 넣은 요소는 Integer 타입이기 때문에 런타임에 ClassCastException 발생한다
    String second = (String)rawList.get(1);
  }
}
```

```java
// 제네릭을 사용한 경우
public class GenericTypeCasting {

  public static void main(String[] args) {

    // String 타입 매개변수 지정
    List<String> genericsList = new ArrayList<>();
    genericsList.add("Hello");

    // 타입 매개변수로 String을 지정했으므로 다른 타입을 허용하지 않음
//        genericsList.add(1234); // 컴파일 오류 발생

    // 제네릭으로 인해 컴파일러가 타입을 알고 있기 때문에
    // 자동 타입 캐스팅이 지원됨
    String first = genericsList.get(0);

    // 컴파일러의 타입 캐스팅 코드 자동 삽입
    // String first = (String) genericsList.get(0);
  }
}
```
