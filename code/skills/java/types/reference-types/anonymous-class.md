---
layout: default
title:
---

[anonymous class](#anonymous-class)

[anonymous class vs lambda expression](#anonymous-class-vs-lambda-expression)


## anonymous class

익명 클래스는 클래스 선언과 동시에 인스턴스를 생성하는 클래스를 말한다

일반적으로 추상 클래스나 인터페이스를 간단하게 구현할 때 사용한다

### 특징

한 번만 사용되는 일회성 클래스로 이름을 가지지 않는다

선언과 동시에 객체를 생성한다

이름이 없으니까 생성자를 직접 정의할 수 없기 때문에 초기화 블록을 사용한다

지역 내부 클래스와 다르게 static 멤버를 가질 수 없다


## 컴파일된 클래스 파일의 이름

```java
interface Greeting {
    void sayHello();
}

public class Main {
    public static void main(String[] args) {
        Greeting greeting = new Greeting() {
            @Override
            public void sayHello() {
                System.out.println("Hello from anonymous class!");
            }
        };

        greeting.sayHello();
    }
}
```

위와 같이 인터페이스를 구현한 익명 클래스를 가진 Main 클래스를 컴파일하면 아래와 같이 `className$number.class` 형식의 클래스 파일이 생성된다

```text
Main$1.class
```


## anonymous class vs lambda expression


| 비교 항목   | 익명 클래스                 | 람다 표현식             |
|---------|------------------------|--------------------|
| 사용 대상   | 클래스(인터페이스 or 추상 클래스)   | 함수형 인터페이스 (단일 메서드 인터페이스) |
| 이름      | 없음 (컴파일 시 $1 같은 이름 생성) | 없음                 |
| this 참조 | 익명 클래스 자신을 가리킴         | 람다를 감싸는 외부 클래스의 this |
| 생성자 사용  | 불가능 (초기화 블록 {} 사용)	    | 생성자가 없음            |                          
| 메모리 사용  | 객체 생성 (클래스 로딩 필요)      | 더 가벼움 (실제 클래스로 생성되지 않음) |                          
| 오버라이딩   | 여러 메서드 오버라이딩 가능        | 오직 하나의 메서드만 구현 가능  |






