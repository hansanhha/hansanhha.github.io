---
layout: default
title:
---

## SynchronizedList

동시성 처리를 필요로 할 때 사용하는 List

```java
// Collections.synchronizedList() 호출
var synchronizedList = Collections.synchronizedList(myArrayList);
```

```java
public static <T> List<T> synchronizedList(List<T> list) {
    return (list instanceof RandomAccess ?
            new SynchronizedRandomAccessList<>(list) :
            new SynchronizedList<>(list));
}
```

매개변수로 받은 List를 동시성 처리가 가능한 래핑 클래스로 덮음

랜덤 액세스 구현 여부에 따른 분기 처리 후 `SynchronizedRandomAccessList` 또는 `SynchronizedList`를 반환함(둘의 동작 방식은 크게 차이가 나지 않음)

```java
static class SynchronizedList<E>
        extends SynchronizedCollection<E>
        implements List<E> {

    final List<E> list;
    
    SynchronizedList(List<E> list) {
        super(list);
        this.list = list;
    }

}
```

SynchronizedList는 `List<E>` 타입의 필드를 통해 동작함

## 상속 관계

<img src="./images/synchronized hierarchy.png" alt="synchronized hierarchy" style="width: 35%; height: 35%">

기본적인 List 인터페이스만 구현하고 있음

## 동작 방식

SynchronizedList는 메서드 단위로 동기화를 함

어떤 스레드에서 이 리스트의 메서드를 사용하고 있는 동안에는 다른 스레드에서 리스트를 수정할 수 없음

```java
// Collections.SynchronizedList class
static class SynchronizedList<E>
            extends SynchronizedCollection<E>
            implements List<E> {

    public E get(int index) {
        synchronized (mutex) {
            return list.get(index);
        }
    }
}
```