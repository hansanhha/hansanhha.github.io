---
layout: default
title: /code/recipes/spring/spring data jpa/JpaRepository
---

JpaRepository는 기본 CRUD 작업을 정의한 ListCrudRepository와 페이징 작업을 정의한 ListPagingAndSortingRepository를 확장한 spring data jpa 인터페이스로, 배치 작업과 지연 로딩 참조 메서드 등을 추가적으로 정의함

스프링 데이터 jpa에서 제공하는 기본 구현체는 SimpleJpaRepository인데 JpaRepository, 내부 설정을 위한 JpaRepositoryConfigurationAware 등을 확장한 JpaRepositoryImplementation 인터페이스를 구현함

즉, 스프링 데이터 jpa에서 기본 CRUD 작업이나 페이징/정렬 기능을 실질적으로 구현한 객체가 SimpleJpaRepository임

간단 상속 관계: SimpleJpaRepository(구현체) -> JpaRepositoryImplementation -> JpaRepository -> ListCrudRepository, ListPagingAndSortingRepository -> Repository

### SimpleJpaRepository 생성 및 동작 과정

1. 리포지토리 인터페이스 정의

```java
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByLastName(String lastName);
}
```

2. 애플리케이션 컨텍스트 초기화 및 리포지토리 인터페이스 스캔 및 프록시 생성

- 스프링 애플리케이션 시작
- 애플리케이션 컨텍스트 초기화
- 리포지토리 인터페이스 스캔 - @EnableJpaRepositories 어노테이션이 설정된 패키지 내의 모든 리포지토리 인터페이스를 찾음
- 각 리포지토리 인터페이스에 대한 프록시 객체 생성 후 스프링 빈 등록

3. 리포지토리 인터페이스 구현체 생성

- JpaRepositoryFactory를 통해 각 리포지토리 인터페이스 구현체를 생성함
- 기본적으로 SimpleJpaRepository 생성

4. 사용

- 리포지토리 인터페이스 타입으로 의존성 주입 - 실제로 주입되는 건 2단계에서 생성된 프록시 객체임
- 메서드 호출 시, 프록시 객체가 받아서 3단계에서 생성된 SimpleJpaRepository 인스턴스에게 위임하여 데이터 작업 수행


