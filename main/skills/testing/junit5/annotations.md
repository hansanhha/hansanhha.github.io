---
layout: default
title:
---

[basic annotations](#basic-annotations)

[test life annotations](#test-lifecycle-annotations)

[conditional annotations](#conditional-annotations)

[@RepeatedTest](#repeatedtest)

[@Tag](#tag)

[@Nested](#nested)

[전체 테스트 코드](../src/test/java/hansanhha)


junit 5에서 제공하는 어노테이션들은 크게 **테스트 실행, 라이프사이클 관리, 조건부 실행, 반복 실행, 태깅, 예외 테스트, 시간 제한** 등으로 구성된다

## basic annotations

### @Test

테스트를 수행할 메서드에 적용하는 어노테이션

junit 5는 @Test 어노테이션이 선언된 메서드를 감지하고 테스트를 수행한다

```java
class OrderTest {

    @Test
    void orderTest() {
        // ...
    }
}
```

### @Order

테스트 필드, 메서드, 클래스 간의 실행 순서를 지정하는 어노테이션

junit 5에서는 기본적으로 테스트 실행 순서가 보장되지 않기 때문에 @Order 어노테이션으로 명시적으로 지정해야 한다

작은 값 -> 큰 값 순으로 테스트 실행 순서가 결정된다

```java
@Test
@Order(Integer.MIN_VALUE)
void firstTest() {
    System.out.println("order test: first");
}

@Test
@Order(Integer.MAX_VALUE)
void lastTest() {
    System.out.println("order test: last");
}
```

### @Timeout

제한 시간안에 테스트를 실행하는지 검증하는 어노테이션

해당 시간이 초과되면 테스트가 실패한다

```java
@Test
// @Timeout(초)
@Timeout(1)
void timeoutTest() {
}
```



### @Disabled

비활성화할 테스트 메서드 또는 클래스에 적용하는 어노테이션

```java
@Test
@Disabled("실행 제외 테스트")
void disabledTest() {
    System.out.println("@Disabled에 의해 실행되지 않는 테스트");
}
```


## test lifecycle annotations

### @BeforeEach, @AfterEach

각 테스트 메서드 실행 전/후에 호출할 메서드에 적용하는 어노테이션

테스트 메서드 전/후처리 작업을 수행하거나 메서드 간 독립성을 보장하기 위해 사용한다

@BeforeEach: setUp, @AfterEach: tearDown 메서드명을 사용하는 것이 일반적이다  

```java
public class OrderTest {

    Product product;

    // 각 테스트 메서드 실행 전 호출되는 메서드
    @BeforeEach
    void productSetup() {
        Product product = productService.create("test product", 10, 10_000);
    }

    // 각 테스트 메서드 실행 후 호출되는 메서드
    @AfterEach
    void tearDown() {
        productService.delete(product.id());
    }

    @Test
    void orderTest() {
        // ...
    }
}
```

### @BeforeAll, @AfterAll

테스트 클래스의 모든 테스트 메서드를 실행 하기 전/후로 한 번만 호출할 메서드에 적용하는 어노테이션

항상 static 메서드로 선언해야 한다

@BeforeAll: init, @AfterAll: cleanUp 메서드명을 사용하는 것이 일반적이다

```java
@BeforeAll
    static void init() {
        System.out.println("orders test started");
    }
    
@AfterAll
static void cleanUp() {
    System.out.println("orders test completed");
}
```


## conditional annotations

### @EnabledOnOs, @DisableOnOs

운영체제에 따라 테스트 실행 여부를 결정하는 어노테이션

```java
@Test
@EnabledOnOs(value = OS.MAC, disabledReason = "맥os에서만 실행하는 테스트")
void runOnlyOnMacOS() {
    System.out.println("맥os에서만 실행되는 테스트");
}

@Test
@DisabledOnOs(OS.MAC)
void runExceptMacOS() {
    System.out.println("맥os만 빼고 실행되는 테스트");
}
```

### @EnabledOnJre, @DisabledOnJre

jre (java runtime environment) 버전에 따라 실행을 제한할 수 있는 어노테이션

```java
@Test
@EnabledOnJre(JRE.JAVA_21)
void runOnlyJava21() {
    System.out.println("자바 21에서만 실행되는 테스트");
}

@Test
@DisabledOnJre(JRE.JAVA_8)
void runExceptJava8() {
    System.out.println("자바 8만 빼고 실행되는 테스트");
}
```

### @EnabledIfSystemProperty, @DisabledIfSystemProperty, @EnabledIfEnvironmentVariable, @DisabledIfEnvironmentVariable

시스템 속성을 기반으로 테스트 실행 여부를 결정하는 어노테이션


```java
@Test
@EnabledIfSystemProperty(named = "os.name", matches = ".*Windows.*")
void runOnlyWindowsByProperty() {
    System.out.println("os.name의 프로퍼티에 윈도우가 포함된 경우에만 실행되는 테스트");
}

@Test
@DisabledIfSystemProperty(named = "os.name", matches = ".*Windows.*")
void runExceptWindowsByProperty() {
    System.out.println("os.name의 프로퍼티에 윈도우가 포함되지 않은 경우에만 실행되는 테스트");
}
```


## @RepeatedTest

### @RepeatedTest

동일한 테스트 메서드를 여러 번 실행할 때 사용하는 어노테이션

반복 테스트 메서드 간의 실행은 모두 독립적이다

```java
@RepeatedTest(value = 5, name = CURRENT_REPETITION_PLACEHOLDER + "번째 테스트")
@DisplayName("반복 테스트")
void repeatedTest() {
    System.out.println("5번 반복하는 테스트");
}
```


## @Tag

테스트를 그룹화할 때 사용하는 어노테이션

테스트 클래스의 메서드끼리 그룹화하거나 테스트 클래스끼리 그룹화할 수 있다

그룹화된 테스트는 `./gradlew test -Dgroups=${tag name}` 옵션 또는 [gradle test task 설정](./architectures,%20configurations.md#gradle-test-task-추가-옵션-설정)을 통해 선별해서 실행할 수 있다  

```java
@Test
@Tag("group a")
void a1() {
    System.out.println("그룹 a 테스트 실행");
}

@Test
@Tag("group b")
void b1() {
    System.out.println("그룹 b 테스트 실행");
}
```


## @Nested

테스트 클래스에 내부 테스트 클래스를 정의하는 어노테이션

테스트를 논리적으로 그룹화하고 계층적인 구조로 구성할 수 있도록 한다

@Nested 특징
- @Nested 클래스는 반드시 인스턴스 내부 클래스여야 한다
- @Nested 클래스는 부모 클래스의 @BeforeEach, @AfterEach 설정을 상속할 수 있다 (부모의 @BeforeEach를 실행하면서 자식의 @BeforeEach를 실행한다)
- @Nested 클래스 내의 테스트들은 각각 독립적인 인스턴스에서 실행된다

```java
class NestedTest {

    @Nested
    class DepositTests {
        @Test
        void depositShouldIncreaseBalance() {
            account.deposit(50);
            Assertions.assertEquals(150, account.getBalance());
        }
    }
}
```