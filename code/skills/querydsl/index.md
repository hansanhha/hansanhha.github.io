---
layout: index
title:
---

[Querydsl-JPA](#querydsl-jpa)

[Querydsl-JPA 설정](#querydsl-jpa-설정)

[Q 클래스 생성](#q-클래스-생성)

[쿼리 작성](#쿼리-작성)
- [select 절](#select-절)
- [from 절](#from-절)
- [where 절](#where-절)
- [join 절](#join-절)
- [limit-offset, limit-keyset](#limit-offset-limit-keyset)
- [order by 절](#order-by-절)
- [group by 절](#group-by-절)
- [having 절](#having-절)
- [fetch](#fetch)
- [subquery](#subquery)
- [case](#case)
- [집계 함수](#집계-함수)
- [projection](#projection)

[스프링 데이터에서 제공하는 Querydsl 기능](#스프링-데이터에서-제공하는-querydsl-기능)

querydsl 5.1.0 기준

## Querydsl-JPA

JPA 2.0 스펙부터 추가된 Specification를 통해 동적 쿼리를 만드려면 JPA Static Metamodel을 만들어야 한다

메타모델은 엔티티의 각 속성을 정적 필드로 제공하며, 이를 통해 타입 안전한 방식으로 동적 쿼리를 만들 수 있다

```java
public class BookSpecifications {
    
    public static Specification<Book> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                category == null ? null : criteriaBuilder.equal(root.get("mainCategory"), category);
    }
}

public class BookService {

    public List<Book> findBooks(String category) {
        Specification<Book> spec = Specification
                .where(BookSpecifications.hasCategory(category));

        return bookRepository.findAll(spec);
    }
}
```

JPA Specification은 쿼리를 정의하기 위해 CriteriaBuilder를 사용해야 하므로 코드가 다소 복잡하고 읽기 어렵다

또한 문자열로 컬럼 이름을 다룰 때 컴파일 타임에 오류를 검출하기 어렵다

querydsl은 이러한 문제를 해결하는 라이브러리로 JPA Static Metamodel의 기능을 발전시켜 간결하고 읽기 쉬운 코드로 동적 쿼리를 작성할 수 있게 도와준다

querydsl JPAQueryFactory: JPA CriteriaBuilder

querydsl Q Class: JPA Static Metamodel

## Querydsl-JPA 설정

#### querydsl 의존성 추가 (build.gradle.kts)

querydsl apt(annotation processing tool)를 통해 컴파일 과정에서 jakarta 어노테이션을 인식하여 Q 클래스 메타모델을 자동 생성한다

스프링 부트 3.0 버전부터 java ee 의존성을 jakarta 패키지로 변경함에 따라 querydsl도 그에 맞춰서 jakarta로 설정해야 한다

```kotlin
implementation("com.querydsl:querydsl-jpa:${version}:jakarta")
annotationProcessor("com.querydsl:querydsl-apt:${version}:jakarta")
annotationProcessor("jakarta.annotation:jakarta.annotation-api")
annotationProcessor("jakarta.persistence:jakarta.persistence-api")
```

#### gradle configurations 설정

어노테이션 처리기가 컴파일 시점에 동작할 수 있도록 설정한다

```kotlin
configurations {
    compileOnly {
        extendsFrom(
            configurations.annotationProcessor.get()
        )
    }
}
```

#### JPAQueryFactory 빈 설정

JPA Specification의 CriteriaBuilder처럼 JPAQueryFactory은 querydsl의 동적 쿼리 빌더로, querydsl을 사용하려면 스프링 빈으로 등록해줘야 한다 (내부적으로 EntityManager 사용)

CriteriaBuilder은 컴파일 타임에 쿼리 문자열을 검증하지 않기 때문에 런타임 오류 가능성이 있는 반면, JPAQueryFactory는 타입 안정성을 제공하고 훨씬 더 나은 코드 가독성을 가진다

```java
@Configuration
public class QuerydslConfig {
    
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
```

## Q 클래스 생성

#### 엔티티 작성

```java
@Entity
public class Book extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private UUID isbn;

    private String title;

    private String author;
}
```

#### 컴파일

gradle의 compileJava 또는 mvn compile 명령을 통해 클래스를 컴파일하면 빌드 디렉토리에 Q 클래스가 생성된다

```shell
./gradlew compileJava
```

![q class](./assets/querydsl%20qclass.png)

## 쿼리 작성

querydsl를 이용하여 동적 쿼리를 만드려면 스프링 빈으로 등록한 [JPAQueryFactory](#jpaqueryfactory-빈-설정)와 [querydsl-aot](#querydsl-의존성-추가-buildgradlekts)를 통해 생성된 Q 클래스가 필요하다 

```java
public class QuerydslRepository {
    
    private final JPAQueryFactory queryFactory; // 스프링 설정 필요
    private final QUser user = QUser.user; // 생성된 Q 클래스의 정적 필드 참조

    public QuerydslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
}
```

### select 절

select 절은 조회할 데이터를 선택하는 부분으로 엔티티(테이블)나 엔티티의 특정 필드만 가져오거나 중복 제거, 서브 쿼리, 프로젝션을 같은 기법을 사용할 수도 있다  

#### select

쿼리에서 조회할 대상을 선택하는 메서드

단일, 복합 필드 또는 엔티티 지정 가능

```java
List<User> user = queryFactory
                        .select(user)
                        .from(user)
                        .fetch();
```

#### selectOne

특정한 값 없이 1을 반환하는 쿼리를 생성하는 메서드

조건 유효성 검사, 카운트, 특정 조건의 존재 여부를 확인할 때 사용한다

```java
Boolean exist = queryFactory
                    .selectOne()
                    .from(user)
                    .where(user.id.eq(1))
                    .fetchFirst() != null;
```

#### selectZero

[selectOne](#selectone)과 비슷하며, 대신 0을 반환함

```java
Boolean exist = !queryFactory
                    .selectZero()
                    .from(user)
                    .where(user.id.eq(1))
                    .fetchFirst() == null;
```

#### selectDistinct

조회 결과에서 중복된 데이터를 제거한다 (SQL DISTINCT 키워드와 동일)

```java
List<String) distinctUsernames = queryFactory
                                    .selectDistinct(user.username)
                                    .from(user)
                                    .fetch();
```

#### selectFrom

특정 테이블 또는 엔티티의 모든 데이터를 조회할 때 사용하는 메서드

```java
User user = queryFactory
                .selectFrom(user)
                .where(user.id.eq(1))
                .fetchOne();
```

### from 절

from 메서드는 SQL 쿼리의 데이터 시작점을 정의한다

#### 데이터 소스 지정

```java
User user = queryFactory
                .select(user.username)
                .from(user) // user 엔티티 지정
                .fetchOne();
```

### where 절

where 절은 BooleanExpression을 기반으로 다양한 조건을 사용하여 데이터를 필터링한다

#### 비교 조건

eq(equal): 동등 비교

ne(not equal): not equal

gt, goe, lt, loe: 크기 비교

```java
queryFactory
    .selectFrom(user)
    .where(user.gender.eq(MAN))
    .where(user.residence.ne(SEOUL))
    .where(user.age.gt(30))
```

#### 논리 연산 

and, or

```java
queryFactory
    .selectFrom(user)
    .where(user.gender.eq(MAN).and(user.age.gt(30)))
    .where(user.gender.eq(MAN).or(user.age.lt(30)))
```

#### Null 체크

isNull, isNotNull

```java
queryFactory
    .selectFrom(user)
    .where(user.username.isNotNull())
    .where(user.username.isNull())
```

#### 문자열 검색

like, notLike: 매칭 조건 선택

startsWith: `%string`

endsWith: `string%`

contains: `%string%`

```java
queryFactory
    .selectFrom(user)
    .where(user.username.like("%hansanhha%")) 
    .where(user.username.notLike("hansanhha%")) 
    .where(user.username.startsWith("hansanhha")) // %hansanhha
    .where(user.username.endsWith("hansanhha")) // hansanhha%
    .where(user.username.contains("hansanhha")) // %hansanhha%
```

#### 컬렉션 조건

in, notIn

```java
queryFactory
    .selectFrom(user)
    .where(user.username.in("hansanhha", "java", "querydsl"))
    .where(user.username.notIn("hansanhha", "jpa", "spring-data-jpa"))
```

#### Boolean 조건

isTrue, isFalse

```java
queryFactory
    .selectFrom(user)
    .where(user.isAwesome.isTrue())
    .where(user.isAwesome.isFalse())
```

#### 날짜 비교

before, after, between

```java
queryFactory
    .selectFrom(user)
    // 최근 7일 동안 회원가입한 사용자
    .where(user.createdAt.after(LocalDateTime.now().minusDays(7)))
        
    // 최근 한 달 동안 회원가입한 사용자
    .where(user.createdAt.between(LocalDateTime.now().minusMonth(1), LocalDateTime.now()))
```

#### 범위 지정 조건

between

```java
queryFactory
    .selectFrom(user)
    .where(user.age.between(10, 20))
```

#### 동적 조건 (BooleanBuilder)

BooleanBuilder를 사용한 조건 생성

```java
// BooleanBuilder 생성
BooleanBuilder builder = new BooleanBuilder();

// 조건 추가 (사전에 필드 null 검증 필요)
builder.and(user.username.eq(username))
builder.or(user.age.gt(30))

// builder 적용
queryFactory
    .selectFrom(user)
    .where(builder)
```

#### 커스텀 BooleanExpression

```java
public BooleanExpression isAdult(QUser user) {
    return user.age.gt(20);
}

queryFactory
    .selectFrom(user)
    .where(isAdult(user))
```

### join 절

#### inner join

두 테이블 간 공통된 데이터를 반환하는 조인, 조건을 만하는 경우에만 데이터가 선택된다

```java
List<Tuple> results = queryFactory
            .select(user, order)
            .from(user)
            // user와 order inner join
            // 두 번째 파라미터 order는 alias로 사용됨
            .join(user.orders, order) 
            .fetch();
```

#### left join

연관된 데이터를 포함하지 않아도 결과를 가져옴

```java
List<Tuple> results = queryFactory
            .select(user, order)
            .from(user)
            // user와 order left join
            .leftJoin(user.orders, order) 
            .fetch();
```

#### fetch join

엔티티를 조회할 때 연관 엔티티를 즉시 조회(eager loading)한다

```java
List<Tuple> results = queryFactory
            .select(user, order)
            .from(user)
            // user와 order left fetch join
            .leftJoin(user.orders, order).fetchJoin()
            .fetch();
```

#### on 절

on 절을 이용하여 추가 조건을 지정할 수 있다

```java
List<Tuple> results = queryFactory
            .select(user, order)
            .from(user)
            .leftJoin(user.orders, order).fetchJoin()
            // 추가 조건: 주문이 완료된 상태만 join 
            .on(orders.status.eq(OrderStatus.COMPLETED))
            .fetch();
```

#### unrelated join

JPA 연관 관계가 없는 엔티티 간 조인

```java
List<Tuple> results = queryFactory
            .select(user, order)
            .from(user)
            .join(orders, order)
            .on(user.id.eq(order.userId))
            .fetch();
```

### limit-offset, limit-keyset

limit: 한 번에 가져올 데이터 최대 개수 제한
- limit 10: 한 번에 최대 10개의 데이터 반환

offset: 반환할 데이터의 시작 위치 지정
- offset 10: 11번째 데이터부터 반환

```java
Pageable pageable = Pageable.ofSize(20).withPage(2);

// pageable.getOffset: page * size (시작 위치 계산)
// pageable.getPageSize: size (한 페이지에 표시할 데이터 수)

List<User> users = queryFactory
            .selectFrom(user)
            .where(user.username.eq(username))
            .offset(pageable.getOffset()) // 시작 위치
            .limit(pageable.getPageSize()) // 개수 제한
            .fetch();
```

#### count 쿼리

```java
long count = queryFactory
                .select(user.count())
                .from(user)
                .where(user.username.eq(username))
                .fetchOne();
```

#### keyset pagination

데이터를 반환할 시작 위치를 결정하는 offset의 값이 큰 경우 DB의 성능 저하를 유발시킨다

성능 최적화를 위해 offset 대신 특정 키를 기준으로 페이지네이션을 하는 keyset pagination이 고안되었다

```java
List<User> users = queryFactory
            .selectFrom(user)
            // userId보다 큰 id 값을 가진 user 엔티티 필터링
            .where(user.id.gt(userId))
            .limit(limit) // 개수 제한
            .fetch();
```

### order by 절

#### 기본 정렬

field.asc, field.desc: 오름차순, 내림차순

orderBy 메서드를 통해 쉼표로 구분하여 여러 정렬 조건을 지정할 수 있다

```java
List<User> orderedUsers = queryFactory
            .selectFrom(user)
            // age 내림차순 정렬 -> username 오름차순 정렬
            .orderBy(user.age.desc(), user.username.asc())

            // phoneNumber 필드가 NULL인 데이터 먼저 정렬
            .orderBy(user.phoneNumber.asc().nullsFirst())
            .fetch();
```

#### 동적 정렬

정렬 필드를 String 값으로 받아 엔티티 필드와 매칭하여 정렬 조건을 동적으로 생성한다

```java
public List<User> getUsers(String sortField, boolean isAsc) {
    
    OrderSpecifier<?> sortCondition = getOrderSpecifier(user, sortField, isAsc);
    
    return queryFactory
            .selectFrom(user)
            .orderBy(sortCondition)
            .fetch();
}

private OrderSpecifier<?> getOrderSpecifier(QUser user, String sortField, boolean isAsc) {

    ComparableExpressionBase<?> targetColumn;

    // 정렬할 필드 선택
    switch (sortField) {
        case "username":
            targetColumn = user.username;
            break;
        case "age":
            targetColumn = user.age;
            break;
        default:
            throw new IllegalArgumentException("유효하지 않은 정렬 필드: " + sortField);
    }

    // 정렬 방향 결정
    Order order = isAsc ? Order.ASC : Order.DESC;
    // Null인 데이터 맨 뒤에 정렬
    OrderSpecifier.NullHandling nullHandling = OrderSpecifier.NullHandling.NullsLast;
    return new OrderSpecifier<>(order, targetColumn, nullHandling);
}
```

### group by 절

데이터를 특정 필드 또는 필드의 값으로 그룹화한다

```java
// 부서별 사용자 그룹화
List<Tuple> results = queryFactory
            .selectFrom(user)
            .groupBy(user.department)
            .fetch();

// 평균 연봉 별 사용자 그룹화
List<Tuple> results = queryFactory
            .selectFrom(user.salary.avg(), user.count())
            .groupBy(user.salary.avg())
            .fetch();
```

### having 절

group by로 그룹화된 결과에 조건을 적용할 때 having 절을 사용한다

where 절은 개별 행에 대해 조건을 적용하고, having 절은 그룹화된 데이터에 대해 조건을 적용한다 

```java
// 부서별 평균 연봉이 5000 이상인 부서 필터링
List<Tuple> results = queryFactory
            .selectFrom(user)
            .groupBy(user.department)
            .having(user.salary.avg().at(5000))
            .fetch();
```

### fetch

querydsl에서 쿼리 결과를 반환받으려면 fetch 메서드를 사용해야 된다

값이 없는 경우 null을 반환한다

fetch: `List<T>` 타입으로 결과 반환

fetchFirst: 쿼리 결과 중 첫 번째 항목 `T` 반환 (`limit(1).fetchOne()`와 동일) 

fetchOne: 쿼리 결과가 하나인 `T` 반환 (두 개 이상이면 `NonUniqueResultException` 발생)

fetchCount(depreated): 쿼리의 결과로 반환되는 행의 개수 반환

fetchResults(deprecated): 전체 결과 수까지 반환하는 fetch 메서드, `QueryResult<T>` 타입

```java
// 성별이 남자인 사용자 목록 조회
List<User> users = queryFactory
            .selectFrom(user)
            .where(user.gender.eq(MAN))
            .fetch();

// 사용자의 id를 기준으로 특정 사용자 조회
User user = queryFactory
            .selectFrom(user)
            .where(user.id.eq(userId))
            .fetchOne();

// 최근 한 달 사이에 회원가입한 사용자 중 첫 번째 사용자 조회
User user = queryFactory
            .selectFrom(user)
            .where(user.createdAt.between(LocalDateTime.minusMonth(1), LocalDateTime.now()))
            .fetchFirst();
```

### subquery

서브 쿼리는 SQL에서 쿼리 내에 포함된 다른 쿼리를 의미하는 것으로 주로 select, where 절에서 메인 쿼리의 조건을 지정하거나 데이터를 필터링하는 데 사용된다

서브 쿼리 유형
- 스칼라 서브 쿼리: 단일 값을 반환하는 서브 쿼리
- 컬럼 서브 쿼리: 여러 값을 반환할 수 있는 서브 쿼리

주요 서브 쿼리 종류
- 단일 서브 쿼리: 하나의 값을 반환하는 서브 쿼리
- IN 서브 쿼리: 여러 값을 반환하는 서브 쿼리(IN 조건으로 사용됨)
- EXISTS 서브 쿼리: 조건에 맞는 데이터가 존재하는지 여부를 확인하는 서브 쿼리
- FROM 절 서브 쿼리: FROM 절에서 서브 쿼리를 사용하여 임시 테이블처럼 데이터를 가져오는 서브 쿼리

**주의점**: JPA와 querydsl은 from 절의 서브 쿼리(인라인 뷰)를 지원하지 않기 때문에 from 절 서브 쿼리가 필요한 경우 join, native sql, hibernate 구현체, 쿼리 분리 실행 등의 방법으로 우회해야 한다

#### querydsl 서브 쿼리 설정

querydsl의 `JPAExpressions`를 이용해 서브 쿼리를 작성한다

서브 쿼리에서 데이터 소스를 지정하기 위해 엔티티에 별칭을 지정하여 새로운 참조를 생성한다

서브 쿼리의 절은 바깥 쿼리와 독립적인 형태로 동작한다

#### 단일 서브 쿼리

```java
QUser subUser = new QUser("subUser");

// 평균 나이보다 많은 사용자 조회
List<User> users =  queryFactory
            .selectFrom(user)
            .where(user.age.gt(
                    JPAExpressions
                        .select(subUser.age.avg())
                        .from(subUser))
                    )
            .fetch();
```

#### IN 서브 쿼리

```java
QUser subUser = new QUser("subUser");
QJob job = new QJob("job");

// 직업이 개발자인 사용자 조회 (frontendDeveloper, backendDeveloper 등)
List<User> developers = queryFactory
            .selectFrom(user)
            .where(user.job.id.in(
                        JPAExpressions
                            .select(job.id)
                            .from(job)
                            .where(job.name.contains("developer")))
                    )
            .fetch();
```

#### EXISTS 서브 쿼리

```java
QJob job = new QJob("job");

// 서브 쿼리에서 직업이 개발자인 사용자를 조회하고 (frontendDeveloper, backendDeveloper 등) 
// 바깥 쿼리에서 해당 사용자가 있는 경우 결과를 반환함
List<User> developers = queryFactory
            .selectFrom(user)
            .where(JPAExpressions
                    .selectFrom(subUser)
                    .where(job.name.contains("developer"))
                    .exists()
               )
            .fetch();
```

### case

case는 조건에 따라 다른 값을 반환하는 조건문(if-else)으로 주로 select 절에서 사용된다

querydsl에서는 select 절에서 엔티티 필드를 이용하거나 CaseBuilder를 통해 지정할 수 있다

#### 엔티티 필드 case

엔티티 필드를 이용해서 case를 작성하는 경우엔 해당 필드에 대한 조건만 지정할 수 있다

```java
List<Tuple> = queryFactory
    .select(user.age
                // 조건 정의
                .when(user.age.gt(60).then("중장년"))
                .when(user.age.gt(20).then("청년"))
                .otherwise("청소년") // 조건에 만족하지 않는 경우
                .as("age_group") // 결과 컬럼 이름
    )
    .from(user)
    .fetch();

for (Tuple tuple : results) {
    int age = tuple.get(user.age);
    String ageGroup = tuple.get("age_group", String.class);
    System.out.println(String.valueOf(age) + " - " + ageGroup);
}
```

#### CaseBuilder

CaseBuilder를 사용하면 엔티티 필드와 상관없이 조건을 작성할 수 있다

아래의 쿼리에서 user.name을 가져올 데이터로 선택하고, 조건은 user.age를 통해 지정한다

```java
List<Tuple> = queryFactory
    .select(user.name,
            new CaseBuilder()
                // 조건 정의
                .when(user.age.gt(60).then("중장년"))
                .when(user.age.gt(20).then("청년"))
                .otherwise("청소년") // 조건에 만족하지 않는 경우
                .as("age_group") // 결과 컬럼 이름
    )
    .from(user)
    .fetch();

for (Tuple tuple : results) {
    String name = tuple.get(user.name);
    String ageGroup = tuple.get("age_group", String.class);
    System.out.println(name + " - " + ageGroup);
}
```

### 집계 함수

```java
queryFactory
    .select(user.count())
    .select(user.salary.sum())
    .select(user.salary.avg())
    .select(user.salary.max())
    .select(user.salary.min())
        
    // 부서별 인원 수대로 정렬 (내림차순)
    .groupBy(user.department)
    .orderBy(user.count().desc())

    // 부서별 평균 연봉이 5000 이상인 부서 필터링
    .groupBy(user.department)
    .having(user.salary.avg.gt(5000))
```

### projection

프로젝션은 데이터베이스 또는 ORM에서 특정 열이나 필드만 선택하여 결과를 반환하는 작업을 의미한다 

전체 엔티티 대신 특정 필드를 선택하거나 DTO 객체를 결과로 반환받을 수도 있다

querydsl 프로젝션 유형: 기본형, 엔티티, DTO, 튜플 

#### 단일 필드 프로젝션

하나의 필드만 반환하는 프로젝션 

```java
// User 엔티티의 username 필드만 조회한다
List<String> usernames = queryFactory
                .select(user.username)
                .from(user)
                .fetch();
```

#### 튜플 프로젝션

Tuple은 querydsl에서 제공하는 구조로, 여러 필드를 묶어서 반환하기 위해 사용된다

특정 테이블/엔티티에 종속되지 않고 선택한 필드를 결과로 포함한다

반환된 Tuple 객체에서 각 필드 값을 개별적으로 추출할 수 있다

**단점**
- 튜플은 타입 안정성이 낮다 (잘못된 필드명을 사용하거나 타입 변환 오류가 발생할 가능성이 있음)
- 복잡한 쿼리에서 코드 가독성이 떨어진다

```java
// 서로 다른 엔티티의 필드 값과 서브 쿼리의 결과 값을 Tuple로 추출
List<Tuple> results = queryFactory
                .select(user.username, 
                        order.totalPrice,
                        JPAExressions
                            .select(order.count())
                            .from(order)
                            .where(order.user.id.eq(user.id))
                )
                .from(user)
                .leftJoin(user.orders, order).fetchJoin()
                .fetch();

for (Tuple result : List<Tuple> results) {
    String username = result.get(user.username);
    Integer totalPrice = result.get(order.totalPrice);
    Integer orderCount = result.get(JPAExpressions.select(order.count()));
    
    System.out.println(username + ": " + totalPrice + " (Orders: " + orderCount + ")");
}
```

#### DTO 프로젝션

프로젝션용 DTO를 정의한 뒤 querydsl의 Projections를 이용하여 DTO 프로젝션 형태로 결과를 반환받을 수 있다

Projections는 세 가지 방법으로 DTO 프로젝션을 생성할 수 있다
- Projections.bean: 프로퍼티 setter 접근
- Projections.fields: 필드 접근
- Projections.constructor: 생성자 접근

**주의점**
- 생성자 접근 시 필드 순서가 일치해야 한다
- 필드 타입 변환이 정확히 일치해야 한다

**DTO 프로젝션 필드와 쿼리 필드를 매칭시켜야 할 때**
- ExpressionUtils.as 메서드를 통해 DTO 프로젝션 필드와 쿼리 결과 필드의 이름을 맞출 수 있다 

```java
// DTO 정의
public record UserOrderInfo(
        String username,
        Integer orderTotalPrice,
        Integer orderCount) {}

// Projections.constructor: DTO 생성자를 통해 DTO 프로젝션 생성
queryFactory
        .select(Projections.constructor(UserOrderInfo.class,
                    user.username,
                    order.totalPrice,
                    ExpressionsUtils.as(JPAExressions
                        .select(order.count())
                        .from(order)
                        .where(order.user.id.eq(user.id)), "orderCount")
                )
        )
```

#### @QueryProjection

DTO 프로젝션 클래스를 Q 클래스로 만드는 생성자 전용 querydsl 어노테이션으로, 컴파일러로 타입 체크를 할 수 있게 한다

**단점**
- DTO가 많아질수록 Q 클래스 파일도 증가한다
- DTO에 querydsl 의존성이 생긴다

```java
// 생성자에 @QueryProjection 적용
public class UserOrderInfo {
    
    private final String username;
    private final Integer orderTotalPrice;
    private final Integer orderCount;
    
    @QueryProjection
    pubilc UserOrderInfo(String username,
                         Integer orderTotalPrice,
                         Integer orderCount) {
        this.username = username;
        this.orderTotalPrice = orderTotalPrice;
        this.orderCount = orderCount;
    }
}

// @QueryProjection DTO 프로젝션 사용
// 일반 DTO 대신 QUserOrderInfo를 사용한다 
queryFactory
        .select(new QUserOrderInfo(
                user.username,
                order.totalPrice,
                ExpressionsUtils.as(JPAExressions
                    .select(order.count())
                    .from(order)
                    .where(order.user.id.eq(user.id)), "orderCount"))
        )
```

## 스프링 데이터에서 제공하는 Querydsl 기능
