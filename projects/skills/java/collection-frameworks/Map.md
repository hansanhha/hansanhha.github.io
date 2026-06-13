---
layout: default
title:
---

[Map](#map)

[Entry](#entry)

[SequencedMap](#sequencedmap)

[SortedMap](#sortedmap)

[NavigableMap](#navigablemap)

[ConcurrentMap](#concurrentmap)

[SynchronizedMap](#synchronizedmap)

## Map

자바 컬렉션 프레임워크에 속하는 최상위 인터페이스로, Map은 해시 테이블과 같은 키와 값이 매핑된 데이터를 저장하는 자료구조임

**특징**

중복된 키를 포함하지 않음

각각의 키들은 하나의 값에 매핑됨

세 가지 컬렉션 뷰를 제공함
- `keySet`
- `values`
- `entrySet`

Map이 보관하고 있는 데이터의 순서는 컬렉션 뷰의 이터레이터가 반환하는 순서에 따라 결정됨

다만 TreeMap이나 SequencedMap 인터페이스 구현체의 경우엔 데이터의 순서가 보장됨

## Entry

맵에 저장된 키-값 쌍을 표현하는 역할을 함

맵의 요소 하나를 나타내는 Entry 인터페이스를 통해 키와 값을 개별적으로 다룰 수 있음

일반적으로 맵의 요소를 순회할 때 사용되며, 맵이 제공하는 뷰 중 entrySet() 메서드가 Entry를 반환함

## SequencedMap

자바 21에서 도입된 인터페이스로, Map 인터페이스를 상속받아 맵의 기본 기능을 모두 포함하며, 순차적 순서를 다루는 추가 메서드를 정의함

입력 순서 또는 순차 순서를 보장하며, 입력된 순서와 반대 순서(reverse shallowOrder)로 요소에 접근할 수 있는 기능을 제공함

**순서 보장**

맵에 추가된 요소의 순서를 보장함

기본적으로 요소가 추가된 순서를 유지하거나, 제거된 순서에 대한 추적을 할 수 있음

**순차 접근 메서드**

Map의 컬렉션 뷰(keySet, values, entrySet)처럼 순차적으로 요소에 접근할 수 있는 default 메서드 제공
- `sequencedKeySet`
- `sequencedValues`
- `sequencedEntrySet`

#### 대표 구현체

LinkedHashMap

## SortedMap

SequencedMap을 확장한 인터페이스로, 키를 정렬된 순서로 유지하는 특성을 가짐

키의 Comparable에 의한 자연 순서(natural ordering) 또는 Comparator에 따라 자동으로 정렬되도록 보장함

**범위 뷰 메서드**

특정 범위의 키-값 쌍을 뷰로 제공하는 메서드를 통해 맵의 일부분을 쉽게 접근할 수 있음

`subMap(K, fromKey, K to Key)` `headMap(K toKey)`, `tailMap(K fromKey)`

**최소/최대 키 접근 메서드**

가장 작은 키와 가장 큰 키에 접근할 수 있는 메서드를 제공함

`firstKey()`, `lastKey()`

**특징**

자동 정렬 지원, 탐색 기능(최댓값, 최솟값, 범위 검색 등)

특정 순서로 정렬된 데이터가 필요하거나, 범위 검색이 중요한 경우에 유용함 

## NavigableMap

SortedMap을 확장한 인터페이스로, 추가적인 내비게이션 기능을 제공하는 맵임

키의 정렬된 순서에 따라 맵을 탐색하고, 특정 키와의 관계를 기반으로 더 높은 키 또는 더 낮은 키에 대한 조회 기능을 제공함

**내비게이션 메서드**

지정된 키 대비 정렬 순서에 따른 엔트리를 반환함

`lowerEntry(K key)`, `floorEntry(K key)`, `ceilingEntry(K key)`, `higherEntry(K key)`

**내림차순 뷰**

맵의 데이터를 내림차순으로 보는 뷰를 제공함

`descendingMap()`

**서브맵 조회**

특정 범위에 속하는 키와 그에 대응하는 엔트리를 조회하는 서브맵 제공

`subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)`

`headMap(K toKey, boolean inclusive)`

`tailMap(K fromKey, boolean inclusive)`

**폴링 연산**

맵에서 첫 번째 또는 마지막 엔트리를 가져오면서 제거하는 연산 

`pollFirstEntry()`, `pollLastEntry()`

#### 대표 구현체 

TreeMap

## ConcurrentMap

Map의 하위 인터페이스로, 동시성 처리에 중점을 둔 맵 구현을 위한 메서드를 정의함

구현체는 여러 스레드가 동시에 접근해도 안전하게 동작하도록, 모든 메서드 호출이 스레드 간에 동기화되어야 함을 보장해야 함

일반적으로 락을 사용하는 대신 Compare-and-Swap (CAS)와 같은 저수준 동시성 제어 메커니즘을 활용해 성능 저하를 최소화함

더 강력한 동시성 보장이 필요한 경우 SyncrhronizedMap을 사용할 수 있음

**동시성 메서드**

`V putIfAbsent(K key, V value)`

`boolean remove(Object key, Object value)`

`boolean replace(K key, V oldValue, V newValue)`

`V replace(K key, V value)`

#### 대표 구현체

ConcurrentHashMap

## SynchronizedMap

Collections 클래스의 중첩 클래스로, Map 인터페이스를 구현하는 객체를 Thread-Safe하게 만들어줌

`Collections.synchronizedMap(Map<K, V> m)` 메서드는 주어진 맵 객체를 감싼 SynchronizedMap 클래스를 반환함 

**동기화 래퍼**

동시성 문제 방지를 위해 내부적으로 모든 메서드 호출을 동기화 처리함 

모든 메서드들이 synchronized 키워드로 감싸져 있어 스레드 간의 경합을 방지함

간단하게 동기화를 적용할 수 있지만 모든 메서드 호출이 동기화되기 때문에 높은 빈도로 쓰기 작업이 발생하는 경우 성능이 낮아질 수 있음

동기화된 맵의 `keySet()`, `entrySet()`, `values()`를 통해 이터레이터를 사용하는 동안 맵을 수정하면 `ConcurrentModificationException`이 발생함

이 문제를 피하려면 이터레이터를 사용하는 동안 전체 맵을 동기화해야 됨

```java
Map<K, V> syncMap = Collections.synchronizedMap(new HashMap<>());
synchronized(syncMap) {
    for (K key : syncMap.keySet()) {
    }
}
```

