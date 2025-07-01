---
layout: default
title:
---

[given-when-then pattern](#given-when-then-pattern)

[bdd](#bdd)

[tdd](#tdd)


## given-when-then pattern

주로 단위 테스트에서 사용되는 패턴으로 테스트를 **초기 상태 설정(given), 테스트 실행(when), 결과 검증(then)**으로 구조화한다

bdd(behavior-driven development) 스타일과 밀접한 관련이 있다

```java
@Test
void shouldReturnUserName() {
    
    // given (테스트 데이터 및 mock 설정)
    UserRepository mockRepository = mock(UserRepository.class);
    when(mockRepository.findById(1L)).thenReturn(new User(1L, "hansanhha"));
    UserService userService = new UserService(mockRepository);

    // when (테스트 실행)
    String username = userService.getUserName(1L);

    // then (검증)
    assertEquals("hansanhha", username);
}
```


## bdd

bdd(behavior-driven development, 행동 주도 개발)는 소프트웨어 기능을 사용자의 행동 관점에서 기술하고 테스트하는 개발 방식이다

tdd에서 발전한 개념으로 **기능의 동작(behavior)을 중심으로 테스트를 작성**하며 단위 테스트(mockito) 뿐만 아니라 통합 테스트, e2e 테스트(cucumber)에도 적용할 수 있다

#### bdd 스타일의 테스트 코드 작성 시 참고할 점
- 사용자의 행동이나 시나리오를 중심으로 테스트를 작성한다
- **어떤 상황에서 어떤 행동을 했을 때 어떤 결과가 나와야 하는가?** 라는 방식으로 작성한다
- 테스트 이름을 sholud...given...when...then 같이 자연어 스타일로 작성하여 가독성을 높인다
- mocking 프레임워크와 함께 사용되어 비즈니스 로직을 독립적으로 검증할 수도 있다 

```java
// given...when...then 자연어 스타일로 메서드 이름을 지어 가독성을 높인다
@Test
void givenTwoNumbers_whenAdding_thenShouldReturnSum() {

    // bdd 스타일, given-when-then 패턴 사용
    // '두 개의 숫자가 주어졌을 때, 이를 더하면 합이 반환되어야 한다'라는 사용자 행동 중심의 테스트 코드 작성한다
    
    // given (초기 상태 설정)
    Calculator calculator = new Calculator();

    // when (테스트할 동작 수행)
    int result = calculator.add(2, 3);

    // then (결과 검증)
    assertEquals(5, result);
}
```


## tdd

tdd(test-driven development, 테스트 주도 개발)는 테스트를 먼저 작성한 후, 이를 통과하는 최소한의 코드를 구현하는 개발 방식이다

테스트가 실패하는 것을 먼저 확인하고 점진적으로 기능을 구현해나가는 메커니즘을 갖는다

### tdd 핵심 원칙

tdd는 red-green-refactor 라는 3단계를 반복한다

red (실패하는 테스트 작성)
- 우선 테스트를 작성하지만 아직 기능이 구현되지 않았기 때문에 테스트가 실패한다
- 테스트가 실패하는 것을 확인하면서 올바른 테스트인지 검증한다

green (코드 작성 및 테스트 통과)
- 테스트가 통과할 수 있도록 최소한의 코드를 작성한다
- 빠르게 테스트를 통과하는 것이 목표이므로 일단 단순한 코드라도 작성한다

refactor (코드 리팩토링)
- 중복 제거, 가독성 개선, 성능 최적화 등의 리팩토링을 수행한다
- 리팩토링 후에도 여전히 테스트가 통과하는지 확인한다

이 과정을 반복하면서 점진적으로 코드를 개선함으로써 신뢰할 수 있는 테스트를 기반으로 개발하게 된다

### tdd 예시

#### 1. Calculator에 add 메서드를 추가한다

실패하는 테스트를 작성한다 (red)

```java
class CalculatorTest {
    
    // add 메서드가 구현되지 않았기 때문에 컴파일 에러가 발생한다
    @Test
    void shouldReturnSumOfTwoNumbers() {
        Calculator calculator = new Calcualtor();
        int result = calculator.add(1, 2);
        assertEquals(5, result);
    }
}
```

#### 2. add 메서드 구현

최소한의 코드를 작성한다 (green)

```java
public class Calculator {
    
    // 테스트가 통과되도록 add 메서드를 구현한다
    int add(int a, int b) {
        return a + b;
    }
}
```

#### 3. 코드 리팩토링

구현한 코드를 개선한다 (refactor)

```java
class Calculator {
    
    // 로깅 추가
    // 리팩토링을 하더라도 테스트가 여전히 통과되는지 확인해야 한다
    int add(int a, int b) {
        log.info("Adding: {} + {}", a, b);
        return a + b;
    }
}
```

### tdd의 장단점

장점
- 버그 예방: 테스트를 사전에 작성하므로 사전에 문제를 발견할 가능성이 높다
- 쉬운 리팩토링: 기존 기능을 변경해도 테스트가 기능의 정상 수행을 자동으로 보장해준다
- 유지보수성: 코드의 기능과 동작이 테스트로 문서화된다
- 좋은 설계 유도: 테스트를 먼저 작성하기 때문에 과도한 결합을 방지하고 좋은 인터페이스 설계를 고민하게 된다

단점
- 느린 초기 개발 속도: 기능보다 테스트를 먼저 작성해야 하므로 초반에는 개발 속도가 느릴 수 있다
- 테스트 유지보수 필요: 기능이 변경될 때 테스트도 함께 수정해야 될 수 있다
- 모든 상황 커버 부담: 비즈니스 로직이 복잡해질수록 모든 예외 케이스를 사전에 테스트하기 어려울 수 있다

### tdd와 bdd

tdd: 코드를 구현하기 전 테스트를 먼저 작성하는 개발 방법론

bdd: 사용자의 행동을 중심으로 테스트를 작성하는 스타일

tdd와 bdd는 상충되는 개념이 아니라 서로 보완적인 관계로 tdd 방식으로 개발하면서 bdd 스타일로 테스트 코드를 작성할 수 있다

red-green-refactor 프로세스를 따르면서 bdd 스타일의 given-when-then 패턴으로 코드의 가독성을 높이는 방식

