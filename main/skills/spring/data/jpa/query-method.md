---
layout: default
title:
---

[Query Method](#query-method)

[NamedQuery](#namedquery)

[메서드명 기반 JPQL 쿼리](#메서드명-기반-jpql-쿼리query-derivation-query-creation)

[@Query](#query)

[스프링 데이터의 쿼리 생성 전략 및 사용](#스프링-데이터의-쿼리-생성-전략-및-사용)

## Query Method

쿼리 메서드는 리포지토리 인터페이스에 정의된 메서드를 통해 자동으로 JPQL을 생성하거나 데이터베이스 연산을 수행할 수 있도록 지원하는 기능으로

스프링 데이터 JPA가 메서드 이름을 기반으로 JPQL 쿼리를 자동으로 생성하는 방식(Query Derivation)과 @Query 어노테이션을 적용해서 개발자가 직접 JPQL을 생성하는 방식이 있음

## NamedQuery

엔티티 클래스에 @NamedQuery 어노테이션을 사용해서 정적 쿼리를 선언하는 방식으로 JPA 표준임

정적 쿼리로 미리 준비된 쿼리를 사용하기 때문에 성능 최적화와 재사용성을 높일 수 있고

한 번 정의된 쿼리는 여러 곳에서 재사용할 수 있으며, 런타임에 동적으로 생성하는 쿼리보다 성능 면에서 유리할 수 있음

## NamedQuery 예시

### NamedQuery 정의

```java
@Entity
@NamedQuery(
        name = "User.findByFirstName",
        query = "SELECT u FROM User WHERE u.firstName = :firstName"
)
public class User {
  @Id
  private Long id;
  private String firstName;
}
```

### NamedQuery 사용

```java
public interface UserRepository extends JpaRepository<User, Long> {

  // @Query 부분을 주석 처리해도 동작함
  @Query(name = "User.findByFirstName")
  List<User> findByFirstName(@Param("firstName") String firstName);
}
```

## 메서드명 기반 JPQL 쿼리(Query Derivation, Query Creation)

메서드 이름으로부터 JPQL을 자동 생성하는 방식은 메서드 이름에 포함된 키워드를 인식하여 각 키워드에 따라 JPQL 쿼리를 생성함

보통 `find...by...` 형식으로 메서드 이름을 선언함

### 쿼리 키워드

쿼리 빌더(스프링 데이터 JPA 내장)는 메서드 이름을 각각 subject와 predicate로 구분함
- subject: 쿼리가 수행하는 데이터베이스 연산을 지정하는 부분으로 메서드명의 첫부분 (`find...By` 등)에 해당됨
- predicate: 연산의 상세 사항(where 절 등)을 지정하는 부분으로 `By` 이후의 부분에 해당됨

subject와 predicate 사이의 값은 고유 플래그를 명시하지 않는 이상 쿼리를 설명하는 것으로 간주되어 쿼리 생성에 영향을 끼치지 않음

[쿼리 subject 키워드 목록](https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html#appendix.query.method.subject)
- `find...By` `read...By` `get...By` `query...By` `search...By` `stream...By`: 조회 연산 (엔티티, 컬렉션, Streamable 하위 타입 등으로 반환 가능)
- `exist...By`: 존재 여부 확인 (boolean 반환)
- `count...By`: 카운트 쿼리 (숫자형 반환)
- `delete...By` `remove...By`: 삭제 연산 (void 또는 삭제 개수 반환)
- `...First<number>...` `...Top<number>`: `find`와 `By` 사이에 명시하면 쿼리 개수를 제한할 수 있음
- `...Distinct...`: `find`와 `By`사이에 명시하면 중복을 제거할 수 있음

[쿼리 predicate 키워드 목록](https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html#appendix.query.method.predicate)
- `And` `Or` `Between`
- `LessThan` `GreaterThan`
- `Like`
- `In`
- `True` `False`
- `IgnoreCase`
- `OrderBy`

### 메서드명 기반 쿼리 사용 예시

```java
public interface UserRepository extends Repository<User, Long> {

  // SELECT u FROM User u WHERE u.emailAddress = ?1 and u.lastname = ?2 쿼리로 변환됨
  List<User> findEmailAddressAndLastname(String emailAddress, String lastname);

  List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);

  // 중복 제거
  List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);
  List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname);

  // 대소문자 구분 X
  List<Person> findByLastnameIgnoreCase(String lastname);
  // 모든 속성에 대한 대소문자 구분 X
  List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

  // 정렬 조건 지정
  List<Person> findByLastnameOrderByFirstnameAsc(String lastname);
  List<Person> findByLastnameOrderByFirstnameDesc(String lastname);
}
```

## @Query

@Query 어노테이션은 개발자가 명시적으로 메서드에 JPQL 쿼리 또는 네티이브 SQL을 정의하는 방식으로 복잡한 조건, 조인이나 서브 쿼리 등이 필요한 상황에서 사용함

메서드의 파라미터를 쿼리에 바인딩할 수 있으며 위치 기반 바인딩(`?1`)과 이름 기반 바인딩(`:paramName`) 모두 지원함

### @Query 사용 예시

```java
public interface UserRepository extends JpaRepository<User, Long> {

  // JPQL 쿼리 정의, 이름 기반 바인딩 사용
  @Query("SELECT u FROM User u WHERE u.firstName = :firstName AND u.age > :age")
  List<User> findByFirstNameAndAgeGreaterThan(@Param("firstName") String firstName, @Param("age") int age);
}
```

```java
public interface UserRepository extends JpaRepository<User, Long> {

  // 네이티브 SQL 쿼리 정의
  @Query(value = "SELECT * FROM users WHERE first_name = ?1 AND age > ?2", nativeQuery = true)
  List<User> findByFirstNameAndAgeGreaterThanNative(String firstName, int age);
}
```

## 스프링 데이터의 쿼리 생성 전략 및 사용

1. 애플리케이션 로드 시점에 @NamedQuery 어노테이션에 정의된 정적 쿼리를 파싱하여 EntityManager 내부 캐시에 저장함
2. 리포지토리 인터페이스를 스캔해서 @Query 어노테이션이 정의된 경우 해당 메서드에 정의된 JPQL을 매핑
3. 선언된 쿼리가 없다면 메서드 이름을 기반으로 쿼리를 동적으로 생성함(메서드 호출될 때 JPQL 생성)