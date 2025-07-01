---
layout: default
title:
---

## CopyOnWriteArrayList

thread-safe한 ArrayList로 add, set 등의 수정 작업이 발생하면 원본 배열을 새 배열로 복사하는 방식으로 동작함

이 방식은 비용이 많이 들긴 하지만 다음의 경우에 유용함

- 순회 연산(traversal operation)이 수정 연산보다 많은 경우 (수정 연산이 많은 경우엔 성능 저하 발생)
- 순회 연산을 동기화할 수 없는 경우
- 동시 스레드 간의 간섭을 방지하고 싶은 경우

CopyOnWriteArrayList의 iterator 메서드는 "snapshot" 개념처럼 iterator 생성된 시점의 배열 상태를 참조함

iterator가 동작하는 동안에는 배열이 변경되지 않고 간섭되지 않게 함으로써 iterator가 `ConcurrentModificationException`를 던지지 않도록 보장함

또한 iterator에서 수정 작업을 지원하지 않으므로 호출하면 `UnsupportedOperationException`를 터뜨림

```java
final transient Object lock = new Object();

public E set(int index, E element) {
        synchronized (lock) {
            ...
        }
    }
    
public boolean add(E e) {
    synchronized (lock) {
        ...
    }
}

public E remove(int index) {
    synchronized (lock) {
        ...
    }
}

static final class COWIterator<E> implements ListIterator<E> {
    
    private final Object[] snapshot;
    private int cursor;
    
    public void add(E e) {
        throw new UnsupportedOperationException();
    }
    
}
```

```java
var list = new CopyOnWriteArrayList<Integer>();

list.add(1);
list.add(2);
```