---
layout: default
title:
---

[test suite](#test-suite)

[test case](#test-case)

[test fixture](#test-fixture)

[test double](#test-double)
- [mock](#mock)
- [stub](#stub)
- [spy](#spy)
- [fake](#fake)
- [dummy](#dummy)


## test suite

여러 개의 테스트 케이스를 모아놓은 집합을 말한다

기능 테스트, 성능 테스트, 회귀 테스트 등 특정 목적에 따라 그룹화된다


## test case

하나의 특정 기능을 검증하는 단위 케이스를 말한다

@Test, @RepeatedTest 등의 테스트 관련 어노테이션을 사용하여 작성된다

```java
@Test
void testAddition() {
    assertEquals(5, Calculator.add(2, 3));
}
```


## test fixture

테스트를 실행하기 위한 초기 상태(설정, 데이터)를 준비하는 과정을 말한다

테스트 실행 전 필요한 리소스를 설정하거나 객체를 생성하는 작업을 수행한다

```java
@BeforeEach
void setup() {
    userService = new UserService(new UserRepository());
}
```


## test double

실제 객체를 대신하여 테스트에서 사용하는 대체 객체를 말한다

mock, stub, spy, fake, dummy 등의 기법이 포함된다


## mock

실제 객체 대신 가짜/모의(mock) 객체를 사용하는 기법

mockito 같은 라이브러리를 사용하여 목 객체를 생성함으로써 외부 의존성을 제거하고 단위 테스트를 수행할 수 있게 한다

```java
// mockito mock
UserRepostiory mockUserRepository = mock(UserRepository.class);

// 행동 정의
when(mockUserRepository.findById(userId)).thenReturn(new User(userId, username));

// mock 객체 주입
UserService userService = new UserService(mockUserRepository);
User user = userService.getUser(userId);

assertEquals(user.getId(), userId);
```

## stub

미리 정해진 값을 반환하는 가짜 객체

내부 로직 없이 특정 입력에 대해 고정된 응답을 반환하는 역할을 한다

vs mock: mock은 동작을 기록하고 검증할 수 있으나 stub은 단순히 정해진 값을 반환한다 (또는 mock 객체의 동작을 설정하는 것을 stubbing이라고 한다)

```java
class UserRepositoryStub implements UserRepository {

    // findById 호출 시 항상 'stub user'을 반환한다
    @Override
    public User findById(Long id) {
        return new User(id, "stub user");
    }
}
```

## spy

실제 객체를 사용하면서 일부만 mocking하는 객체

```java
UserService userService = new UserService(userRepository);

// mockito spy
UserService spyUserService = spy(userService);
spyUserService.getUser(userId);

// 메서드 호출 여부 검증
verify(spyUserService).getUserId(userId);
```

## fake

실제 객체와 비슷하게 동작하지만 간단한 구현체를 제공하는 대체 객체

stub은 미리 정해진 값을 고정적으로 반환하는 객체라면 fake는 실제 객체처럼 동작하는 가짜 객체다

e.g) 인메모리 데이터베이스 대신 List를 활용한 FakeRepository

```java
class FakeUserRepository implements UserRepository {

    private List<User> users = new ArrayList<>();
    
    @Override
    public User findById(Long id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }
    
    public void save(User user) {
        users.add(user);
    }
}
```


## dummy

테스트에서 단순히 파라미터를 채우기 위한 객체

실제로 사용되지 않으나 코드에서 오류없이 실행되도록 하는 역할을 한다

e.g) 로그 기능이 필요하지만 테스트에서 사용하지 않는 경우

```java
class DummyLogger implements Logger {
    
    @Override
    public void log(String message) {
        // nothing
    }
}
```