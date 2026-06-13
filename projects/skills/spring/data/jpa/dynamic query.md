---
layout: default
title:
---

[동적 쿼리](#동적-쿼리)

[Specification](#specification)

[Criteria API](#criteria-api)

[QueryDSL](#querydsl)

[QBE vs Specification vs @Query vs QueryDSL 비교](#qbe-vs-specification-vs-query-vs-querydsl-비교)

## 동적 쿼리

## Specification

Specification은 복잡한 쿼리나 런타임에 쿼리 조건이 결정되는 동적 쿼리를 JPA의 Criteria API를 활용하여 생성할 수 있도록 도와주는 인터페이스임

### 특징

동적 쿼리 생성
- 애플리케이션 런타임에 조건을 추가하거나 제거할 수 있음
- NOT, WHERE, AND, OR 같은 조건 연산자 결합

타입 세이프
- JPA Criteria API를 사용하므로, 쿼리 작성 시 타입 안전성 보장
- 쿼리의 각 부분이 메타모델을 사용하여 정의됨 - 컴파일 시점에 오류 감지 가능

### 추상 메서드

주어진 root(기본 엔티티 타입)와 CriteriaQuery에 대해 Predicate 형식으로 엔티티 쿼리의 WHERE 절을 만드는 메서드임

```java
@Nullable
Predicate toPredicate(Root<T> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);
```

### 예시

#### 엔티티 정의

```java
@Entity
public class User {
  @Id
  @GeneratedValue
  private Long id;
  private String firstName;
  private String lastName;
  private int age;

  // Getters and Setters
}
```

#### Specification 정의 및 사용

동적 쿼리 조건 정의

hasLastName 메서드는 주어진 string 값을 가진 엔티티가 있는지 확인하는 동적 쿼리를 반환하고

hasAgeGreaterThan 메서드는 주어진 int 값보다 큰 값을 가진 엔티티가 있는지 필터링하는 동적 쿼리를 반환함

```java
public class UserSpecification {

  public static Specification<User> hasLastName(String lastName) {
    return (root, query, builder) ->
            builder.equal(root.get("lastName"), lastName);
  }

  public static Specification<User> hasAgeGreaterThan(int age) {
    return (root, query, builder) ->
            builder.greaterThan(root.get("age"), age);
  }
} 
```

리포지토리 인터페이스 정의

JpaSpecificationExecutor 확장 필요

```java
public interface SpecUserRepository extends JpaRepository<SpecUser, Long>, JpaSpecificationExecutor<SpecUser> {
}
```

서비스 객체의 비즈니스 로직에서 정의한 Specifiaction 사용

```java
public List<SpecUser> findUsers(String lastName, int age) {
  Specification<SpecUser> spec = Specification
          .where(SpecUserSpecification.hasLastName(lastName))
          .and(SpecUserSpecification.hasAgeGreaterThan(age));

  return specUserRepository.findAll(spec);
}
```

## Criteria API

## QueryDSL

## QBE vs Specification vs @Query vs QueryDSL 비교

QBE, Specification, QueryDSL: 동적 쿼리 생성

@Query: 정적 쿼리

동적 쿼리는 애플리케이션 실행 시점에 조건이나 파라미터에 따라 **쿼리의 구조가 동적으로 생성**되는 쿼리를 말함

미리 정의된 쿼리가 아니라 사용자 입력, 비즈니스 로직, 애플리케이션 상태 등과 같은 요소에 따라 **쿼리의 구조와 내용을 동적으로 변경**할 수 있음
- 조건에 따른 쿼리 변경
    - 쿼리 조건이 동적으로 변경됨
    - 검색 조건으로 전달된 값이 존재할 때만 특정 필드를 쿼리에 포함시키거나, 여러 필터 조건을 결합시킴
- 동적 조합
    - 여러 조건이 AND, OR 같은 논리 연산자로 결합되거나 조건이 생략될 수 있음
    - 사용자가 선택한 여러 필터 조건을 조합하여 하나의 SQL 쿼리를 동적으로 생성
- 실행 시점에 생성
    - 컴파일 타임이 아닌 런타임에 쿼리가 생성됨

일반 쿼리와의 비교

| 특징            | 동적 쿼리                          | 일반 쿼리                 |
|---------------|--------------------------------|-----------------------|
| 구조 및 쿼리 생성 시점 | 실행 시점에 따라 동적으로 변경 및 생성         | 고정된 쿼리 구조, 컴파일 시점에 생성 |
|유연성| 조건에 따라 여러 쿼리를 조합               | 조건 변경 불가              |
|용도| 조건이 동적으로 변경되거나 다중 조건 조합이 필요할 때 | 단순한 조건 처리             |

### 1. QBE

엔티티 인스턴스를 기반으로 동적 쿼리를 생성하는 방식

장점
- 단순 필드 기반 검색, 일치 여부 검색 같이 간단한 조건을 필요로 하는 동적 쿼리를 생성할 때 유용함

단점
- 조인, 집계 함수, 그룹핑 같이 복잡한 쿼리를 작성하는 데 적합하지 않음
- 직관적인 쿼리 표현이 아님 (엔티티 인스턴스를 사용하여 조건을 작성하기 때문에 가독성이 떨어짐)

### 2. Specification

JPA Criteria API를 사용하여 동적으로 쿼리를 생성하는 방식

장점
- 복잡한 검색 조건이 필요한 경우에 QBE보다 적합함
- 타입 안전성을 보장하므로 컴파일 시점에 오류를 잡을 수 있고, 쿼리 로직을 재사용할 수 있음

단점
- 사용법이 복잡하고, 코드가 장황함
- 복잡한 쿼리일수록 코드가 난해해져서 유지보수가 어려움
- 다수의 조건이 결합되는 경우 JPA가 비효율적인 SQL을 생성할 수 있음

### 3. @Query

JPA 리포지토리 메서드에 직접 JPQL이나 네이티브 SQL을 정의하는 방식

쿼리의 구조가 고정되어 있으며 컴파일 시점에 결정됨

장점
- 쿼리를 직접 작성하므로 직관적이고, 명확함
- 성능을 최적화한 SQL을 작성할 수 있음
- 복잡한 쿼리(조인, 집계 함수, 그룹핑 등)를 쉽게 작성함

단점
- 리포지토리 메서드에 하드코딩됨 (재사용성이 떨어짐)
- 쿼리가 길수록 코드가 장황해짐

### 4. QueryDSL

타입 안전한 쿼리를 작성할 수 있는 DSL(Domain-Specific Language)로 자바 코드로 SQL과 비슷한 구문을 사용해서 동적 쿼리를 작성할 수 있음

장점
- 다양한 데이터 소스(JPA, SQL, MongoDB 등)에서 사용할 수 있음
- 타입 안전성을 보장하므로 컴파일 시점에 쿼리 오류를 감지할 수 있음
- 복잡한 쿼리, 동적 조건, 다중 조인 등을 쉽게 작성할 수 있음
- 자바 코드로 쿼리를 작성하므로, 쿼리와 비즈니스 로직이 동일한 언어로 통합되어 유지보수가 쉬워짐

단점
- 초기 설정 필요 (빌드툴 플러그인 설정, Q클래스 생성기 추가)
- 복잡성 증가 (간단한 CRUD 작업에는 과한 기술스택임)

### 선택 가이드

단순한 CRUD: @Query 또는 스프링 데이터 JPA의 기본 메서드(findById, findAll 등)

복잡한 검색 조건 또는 동적 쿼리 및 타입 안전성: QueryDSL

성능 최적화: @Query, QueryDSL

재사용 가능한 동적 쿼리가 필요한 경우: Specification