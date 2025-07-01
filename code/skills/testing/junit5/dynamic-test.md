---
layout: default
title:
---

[dynamic test](#dynamic-test)

[@TestFactory](#testfactory)

[nested dynamic test](#nested-dynamic-test)


## dynamic test

junit 5의 동적 테스트는 테스트 실행 시점에 동적으로 생성되는 테스트를 의미한다

컴파일 타임이 아니라 런타임에 테스트 케이스를 생성하는 방식

#### static test vs dynamic test

| 유형           | 특징                    | 선언 방식        |
|--------------|-----------------------|--------------|
| static test  | 컴파일 시점에 테스트 케이스가 결정된다 | @Test        |
| dynamic test | 실행 시점에 테스트가 동적으로 생성된다 | @TestFactory |


#### 동적 테스트가 필요한 경우
- 반복 테스트: 동일한 로직을 여러 입력값에 대해 테스트할 때
- 입력 데이터가 외부에서 주어지는 경우: db, 파일, api에서 읽어온 데이터를 기반으로 테스트할 때
- 테스트 케이스가 실행 시점에 결정되는 경우: 예측할 수 없는 데이터에 대해 동적으로 테스트를 구성할 때
- 동적으로 테스트 그룹을 구성할 때: DynamicContainer를 사용하여 테스트를 런타임에 논리적으로 그룹화할 때


## @TestFactory

@TestFactory를 사용하면 동적으로 테스트를 생성할 수 있다

@Testable 어노테이션은 적용된 요소가 junit platform에서 TestEngine에 의해 테스트 또는 테스트 컨테이너로 실행될 수 있음을 나타내는데, @TestFactory 어노테이션은 정적으로 테스트 케이스를 생성하는 @Test 어노테이션과 동일하게 @Testable 어노테이션이 메타 어노테이션으로 적용되어 있기 때문에 테스트를 실행할 수 있는 어노테이션으로 인식된다

테스트 케이스의 반환 값을 `DynamicTest`로 설정하면 실행할 개별 테스트를 동적으로 추가할 수 있다

```java
@TestFactory
Stream<DynamicTest> createTestInRuntime() {
    
    // DynamicTest.stream(동적 입력 값, displayNameGenerator, 테스트 실행 로직)
    return DynamicTest.stream(
            Stream.iterate(0, i -> i <= 10, i -> i + 1), 
            this::getDisplayName, 
            this::doAssertion);
}

private String getDisplayName(int number) {
    return number % 2 == 0
            ? number + "는 짝수이다"
            : number + "는 홀수이다";
}

private void doAssertion(int number) {
    if (number % 2 == 0) {
        assertEquals(0, number % 2);
        return;
    }

    assertEquals(1, number % 2);
}
```


## nested dynamic test

DynamicContainer를 통해 동적 테스트를 계층적(논리적 그룹화)으로 구성할 수 있다

```java
@TestFactory
Stream<DynamicContainer> createGroupedTestInRuntime() {

    // DynamicContainer.dynamicContainer("그룹명", Stream<DynamicTest>)
    return Stream.of(
            DynamicContainer.dynamicContainer("group 1", Stream.of(
                    DynamicTest.dynamicTest("first test", () -> assertEquals(2, 1 + 1)),
                    DynamicTest.dynamicTest("last test", () -> assertTrue("hello".startsWith("h")))
            )),

            DynamicContainer.dynamicContainer("group 2", Stream.of(
                    DynamicTest.dynamicTest("first test", () -> assertFalse(10 < 5)),
                    DynamicTest.dynamicTest("last test", () -> assertEquals("junit", "junit"))
            ))
    );
}
```



