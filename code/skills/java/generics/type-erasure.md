---
layout: default
title:
---

[type erasure](#type-erasure)

[타입 안정성 검사 및 타입 소거 동작](#타입-안정성-검사-및-타입-소거-동작)

[타입 소거를 하는 이유](#타입-소거를-하는-이유)


## type erasure

자바의 제네릭은 컴파일 시점에 타입 안정성을 검사하고 컴파일 이후에 타입 정보를 제거한다 (자동 타입 캐스팅 제외)

즉, 제네릭이 제공하는 타입 안정성은 컴파일러 레벨에서만 존재하고 **타입 소거**에 의해 런타임에는 제네릭 정보가 사라진다


## 타입 안정성 검사 및 타입 소거 동작

### 컴파일 시점

1. 제네릭 코드에서 타입 검사를 수행한다
2. 제네릭 타입을 실제 타입 또는 Object로 대체한다
3. 명시적인 타입 캐스팅 코드를 삽입한다 (컴파일러의 자동 타입 캐스팅 코드 삽입)

### 런타임 시점

1. 컴파일된 바이트 코드에는 제네릭 타입 정보가 없다
2. 모든 제네릭 타입은 Object 또는 타입 바운드(`T extends Number`)로 처리한다
3. 타입 캐스팅을 수행한다

```java
// 제네릭 사용 코드
public class TypeErasure {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Hello");
        String first = list.get(0); // 타입 캐스팅 불필요(컴파일러의 자동 타입 캐스팅 지원)
        System.out.println(fisrt);
    }
}
```

```java
// 컴파일 후 바이트로 변환된 코드(타입 소거 결과)
public class TypeErasure {
    public static void main(String[] args) {
        List list = new ArrayList(); // 제네릭 정보 제거
        list.add("Hello");
        String value = (String) list.get(0); // 컴파일러의 타입 캐스팅 코드 삽입
        System.out.println(value);
    }
}
```

## 타입 소거를 하는 이유

1. 제네릭 도입 이전 코드와의 호환성 유지를 위해 런타임에 타입 정보 제거
2. 추가적인 타입 정보가 없으므로 바이트코드 크기를 줄일 수 있음
3. 타입 정보가 런타임에 없으면, 동일한 바이트코드로 다양한 타입의 객체 처리 가능(유연한 동작 지원)