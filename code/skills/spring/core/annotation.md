---
layout: default
title:
---

[MergedAnnotation](#mergedannotation)

[MergedAnnotations](#mergedannotations)

스프링은 메타 어노테이션을 동적으로 안전하게 처리하기 위해, MergedAnnotation과 MergedAnnotations를 사용함

## MergedAnnotation

특정 어노테이션의 병합된 메타데이터를 표현하는 단일 객체으로, 어노테이션 속성 값이나 관련 정보를 읽고 **병합된 상태**를 제공함

**MergedAnnotation 인터페이스**
```java
// 자바의 모든 어노테이션 인터페이스(@interface)는 Annotation 인터페이스를 자동으로 구현함
public interface MetaAnnotation<A extends Annotation> {

    // 특정 속성 값을 String으로 반환
    String getString(String attributeName) throws NoSuchElementException;

    // 속성이 디폴트 값 외의 값을 가지는지 확인
    boolean hasNonDefaultValue(String attributeName);
    
    // etc methods
}
```

## MergedAnnotations

여러 어노테이션의 메타데이터를 한꺼번에 관리하는 컨테이너 객체로, 특정 클래스나 메서드, 필드 등에 적용된 모든 어노테이션을 관리하는 역할을 함

[MergedAnnotation](#mergedannotation)은 그 중 특정 어노테이션을 나타내는 개별 요소임

**MergedAnnotations와 MergedAnnotation의 필요성**
- 메타 어노테이션 처리: 어노테이션에 선언된 어노테이션을 메타 어노테이션이라고 하는데, 이를 자동으로 병합해서 처리함
- 동적 접근: 어노테이션 속성에 동적으로 안전하게 접근할 수 있음
- 병합 및 캐시를 통한 효율적인 어노테이션 관리

**MergedAnnotations 인터페이스**
```java
// 자바의 모든 어노테이션 인터페이스(@interface)는 Annotation 인터페이스를 자동으로 구현함 
public interface MergedAnnotations extends Iterable<MergedAnnotations<Annotation>> {

    
    // 특정 어노테이션이 존재하는지 확인
    <A extends Annotation> boolean isPresent(Class<A> annotationType);
    boolean isPresent(String annotationType);
    
    // 직접적으로 적용된 어노테이션인지 확인
    <A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType);
    boolean isDirectlyPresent(String annotationType);

    // 특정 어노테이션을 가져옴
    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType);
    <A extends Annotation> MergedAnnotation<A> get(String annotationType);
    
    // static, etc methods
}
```

