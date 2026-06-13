---
layout: default
title:
---

[Collector](#collector)

[Collectors](#collectors)

## Collector

스트림 데이터 수집 방법을 정의한 인터페이스 

T: 스트림 요소 타입

A: 중간 결과를 저장할 누적자 타입

R: 최종 반환 타입

```java
public interface Collector<T, A, R> {

    /*
        결과 컨테이너를 생성하는 메서드
        () -> new ArrayList<>()
     */
    Supplier<A> supplier();

    /*
        스트림 요소를 결과 컨테이너에 누적하는 메서드
        (list, item) -> list.add(item) 
     */
    BiConsumer<A, T> accumulator();

    /*
        병렬 스트림에서 부분 결과를 병합하는 메서드
        스트림에서 combiner 단어는 모두 병렬 스트림과 관련됨
        
        (list1, list2) -> list1.addAll(list2)  
     */
    BinaryOperator<A> combiner();

    /*
        누적 결과를 최종 결과로 반환하는 메서드
        list -> Collections.unmodifiableList(list)
     */
    Function<A, R> finisher();

    /*
        collector의 동작 특성을 정의
        Characteristics: CONCURRENT, UNORDERED, IDENTITY_FINISH
     */
    Set<Characteristics> characteristics();
}
```

## Collectors

사전 정의 Collector 및 쉽게 생성하기 위한 정적 팩토리 메서드를 제공하는 유틸리티 클래스임

#### 컬렉션 변환 정적 팩토리 메서드


`<T> Collector<T, ?, List<T>> toList()`: 스트림 요소를 List에 담아 반환

`<T> Collector<T, ?, Set<T>> toSet()`: 스트림 요소를 Set에 담아 반환

`<T, C extends Collection<T>> Collector<T, ?, C> toCollection(Supplier<C> collectionFactory) `: 스트림 요소를 특정 컬렉션에 담아 반환

#### 맵 변환 정적 팩토리 메서드

```java
<T, K, U> Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> keyMapper, 
                                           Function<? super T, ? extends U> valueMapper)
```

스트림 요소를 주어진 keyMapper와 valueMapper 람다식을 통해 맵에 담아 반환함

제네릭 타입
- T: 스트림 요소 타입
- K: 맵 키 타입
- U: 맵 값 타입
- ?: 중간 누적자 타입

keyMapper: 스트림 요소가 주어지면 맵의 키로 반환함

valueMapper: 스트림 요소가 주어지면 맵의 값으로 반환함