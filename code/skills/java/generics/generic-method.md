---
layout: default
title:
---

[generic method](#generic-method)

## generic method

일반적인 메서드는 다음과 같이 클래스의 파라미터화된 타입을 사용한다

```java
public class GenericMethod<T> {

    T t;
    
    public T identity() {
        return t;
    }
}
```

메서드 선언부에 별도의 제네릭 타입 매개변수를 추가하여 제네릭 메서드를 선언할 수 있다

제네릭 메서드는 클래스의 타입 매개변수와 별개로 독자적인 타입 매개변수를 가질 수 있다

제네릭 클래스가 인스턴스화되면 결정된 타입 매개변수로만 동작할 수 있으나 제네릭 메서드는 하나의 인스턴스에서도 여러 타입으로 동작할 수 있는 장점이 있다

```java
public class GenericMethod<T> {

    T t;

    // 클래스의 타입 매개변수 T와 별개로 동작한다
    public static <T> T identity(T value) {
        return value;
    }

}
```