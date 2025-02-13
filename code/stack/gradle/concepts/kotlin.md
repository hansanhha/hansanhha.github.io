특징
- Jetbrains에서 개발한 JVM 언어
- 정적 타입 언어임
- 세미콜론 선택사항
- 파일 확장자 : `.kts`(스크립트 용 - 컴파일 없이 실행), `.kt`(소스 파일 용 - 실행 전 컴파일)

## 변수

val : read-only 변수
```kotlin
val number = 5
```

var : 재할당 가능 변수
```kotlin
var number = 5
number = 10
```

타입 선언

타입을 선언하면 이후에 초기화 가능
```kotlin
var number:Int
number = 10
```

## 함수

자바 메서드 == 코틀린 함수

```kotlin
fun sum(a: Int, b:Int): Int {
    return a + b
}
```

## 스크립트

코틀린 코드는 스크립트로 실행할 수 있음(build.gradle.kts)

코틀린 스크립트 코드
```kotlin
println("Hello World")
```

자바 코드
```java
package org.example;

public class Greeting {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
```

## 람다식

람다식 : 코드 블록을 변수처럼 취급하는 기능으로 나중에 실행할 수 있음

코틀린 람다식

`{}` 로 람다식을 정의함
```kotlin
val greeting = {
    println("Hello World")
}

greeting()
```

자바 람다식
```java
package org.example;

public class Greeting {
    public static void main(String[] args) { 
        Runnable callMeLater = () -> System.out.println("Hello World");
        callMeLater.run(); 
    }
}

```

코틀린은 마지막 파라미터로 함수를 호출하는 경우, 위 코드처럼 함수를 전달할 수 있음
```kotlin
// 함수 정의
fun greeting(name: String, pre: () -> Unit) {
    pre()
    println("Hello" + name)
}

// 람다식 전달
greeting("Kotlin") {
    println("outside the bracket")
}
```

람다식을 사용한 gradle task 정의

register 함수는 String과 람다(doLast)를 파라미터로 받는 함수로 추측 가능
```kotlin
tasks.register("greeting") {
    doLast {
        println("Hello World")
    }
}
```


