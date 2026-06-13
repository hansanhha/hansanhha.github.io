---
layout: default
title:
---

[HashMap](#hashmap)

[트리화](#트리화)

[코드 분석](#코드-분석)

- [주요 필드](#주요-필드)
- [생성자](#생성자)
- [크기 조정](#크기-조정)
- [트리화, 역트리화](#트리화-역트리화)
- [해시](#해시)
- [삽입](#삽입)
- [삭제](#삭제)
- [조회](#조회)
- [업데이트](#업데이트)
- [forEach](#foreachbiconsumer-super-k--super-v)
- [Iterator](#iterator)
- [View](#view)

## HashMap

자바의 해시맵은 키-값 쌍(엔트리)을 저장하고 검색할 수 있는 해시 테이블 기반의 컬렉션임

`Map<K, V>` 인터페이스를 구현하고 `AbstractMap<K,V>` 로부터 상속받고 있음

### 특징

#### 키와 값의 매핑

키를 기준으로 값을 저장하는 구조임

각 키는 해시 함수를 통해 해시 코드로 변환되고, 이 값을 기반으로 데이터를 저장할 위치를 결정함

#### 중복 허용

키는 중복을 허용하지 않음. 고유해야 됨

동일한 키로 다시 값을 추가하면 기존 값이 덮어씌워짐

값은 중복을 허용함

#### 순서 보장

해시맵은 내부적으로 데이터의 순서를 유지하지 않음

따라서 데이터를 삽입한 순서와 검색할 때의 순서가 다를 수 있음

순서 보장이 필요한 경우 `LinkedHashMap`을 사용할 수 있음

#### null 허용

키와 값 모두에 대해 허용됨

단 키는 고유값을 가져야 하므로, null 키 역시 단 하나만 허용됨

#### 비동기적

동기적으로 동작하지 않기 때문에 멀티 스레드 환경에서 취약함

여러 스레드에서 접근할 수 있도록 안전하게 사용하려면 Collections.synchronizedMap 또는 ConcurrentMap 같은 구현체를 사용해야 됨

#### vs HashTable

자바엔 해시맵과 더불어서 해시 테이블 구현체도 존재하는데, 키와 값에 null을 허용하고 동기화를 지원하지 않는 해시맵과 달리

자바의 해시 테이블은 키와 값에 null 허용하지 않고 synchronized 키워드를 사용해서 동기화를 지원함

## 트리화

HashMap은 기본적으로 버킷에 해시 충돌을 처리하기 위한 단일 연결 리스트를 사용하고 있음

해시 충돌이 발생한 경우 동일한 해시 코드를 가진 여러 키-값 쌍을 저장함

만약 버킷(리스트)이 너무 커질 경우 해시맵은 연결 리스트를 트리 구조로 변환함

TreeMap과 유사한 구조로, 각 노드가 TreeNode로 변환됨

트리화된 버킷은 이진 트리로 변환되어 최악의 경우에도 `O(log n)`의 탐색 시간을 보장함

#### 임계값

TreeNode는 일반 노드보다 약 2배 정도 크기 때문에, `TREEIFY_THRESHOLD`라는 임계값을 넘는 경우에만 트리화가 발생함

만약 버킷의 크기가 작아지면 다시 연결 리스트로 변환됨

#### 정렬 및 비교

트리화된 버킷의 노드는 주로 hashCode를 기준으로 정렬됨

동일한 해시 코드를 가진 두 키가 `Comparable` 인터페이스를 구현한 경우, compareTo 메서드를 사용해 추가적인 정렬을 수행함

## 코드 분석

### 주요 필드

#### 정적 필드

```java
// 기본 초기 용량
// 왼쪽 시프트 연산은 곱셈 연산과 동일하게 작동함
// x << n은 x를 2의 n승만큼 곱하는 것과 동일함
// 반대로 오른쪽 시프트 연산은 x를 2의 n승만큼 나누는것과 동일함
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

// 최대 용량 (약 10억개개 정도의 키-값 쌍을 가질 수 있음)
static final int MAXIMUM_CAPACITY = 1 << 30;

// 기본 로드 팩터
static final float DEFAULT_LOAD_FACTOR = 0.75f;

/*
    트리화 임계값
    동일한 인덱스를 가진 노드들은 연결 리스트에 저장되는데, 충돌이 많이 발생해서 연결 리스트의 길이가 길어진만큼 성능이 떨어질 수 있음 
    만약 연결 리스트에 저장된 노드 수가 이 값을 초과할 때 해시맵은 성능을 유지하고자 연결 리스트 구조에서 트리 구조로 변환함 
    이 값은 트리 구조로 변환되기 위한 임계값으로, 연결 리스트에 저장된 노드 수가 이 값보다 많은 경우 트리 구조로 변환함
 */
static final int TREEIFY_THRESHOLD = 8;

/*
    트리 구조로 변환된 버킷을 다시 연결 리스트로 변환되는 임계값
    트리 구조의 버킷에 요소가 제거되어 노드의 수가 이 값 미만으로 감소하면 트리는 다시 연결 리스트로 변환됨
    연결 리스트 구조보다 트리 구조가 메모리를 더 사용하기 때문에 불필요한 메모리 사용을 줄이기 위함
 */
static final int UNTREEIFY_THRESHOLD = 6;

/*
    트리화가 되기 위한 최소 용량
    해시맵이 충분히 큰 경우에만 버킷 구조를 트리로 변환할 수 있음
    이 값만큼 용량이 크지 않으면 트리화 임계값을 넘는다고 해도 트리로 변환되지 않음
 */
static final int MIN_TREEIFY_CAPACITY = 64;
```

#### 일반 필드

```java
/*
    실제 데이터인 키-값 쌍을 담고 있는 엔트리 배열 (각 인덱스에는 연결 리스트 또는 트리 형태로 노드들이 저장됨)
    처음 사용할 때 초기화되고 필요에 따라 크기를 조정함
    새로 할당되는 길이는 두 배씩 증가함
 */
transient Node<K, V>[] tables;

// entrySet() 캐시
transient Set<Map.Entry<K, V>> entrySet;

/*
    현재 맵이 가지고 있는 키-값 쌍의 개수
    해시 맵의 크기를 결정하는 역할을 하며, 용량 초과 여부를 판단하는 데 사용됨
 */
transient int size;

// 이터레이터 fail-fast용 필드
transient int modCount;

/*
    리사이즈 되기 전 최대 키-값 쌍의 수를 나타냄
    capacity * loadFactor로 계산된 값
    해시맵의 사이즈가 이 값을 초과하면 table 배열의 크기를 두 배로 늘리고, 모든 항목을 새로운 배열에 재배치함(리사이징)
    해시 테이블에서 리사이징 과정은 모든 항목에 대해 해시 값을 재계산해야 되므로 오버헤드를 발생시킴
 */
int threshold;

/*
    해시 테이블 특성 상 엔트리의 개수가 많아지면 해시 충돌이 빈번하게 발생하기 때문에
    일정 범위만큼 채워지면 해시 테이블 크기를 늘려야되는데, 로드 팩터가 그 기준점을 잡아줌
    즉, table 배열에 키-값 쌍이 얼마만큼의 일정 비율로 채워졌을 때 리사이즈를 일으킬지 결정함
    loadFactory의 값이 0.75라면 배열의 75%가 채워졌을 때 리사이징이 일어남 
 */
final float loadFactor;
```

### 생성자

```java
/*
    초기 용량과 loadFactor를 매개변수로 받는 생성자
    초기 용량은 최대 MAXIMUM_CAPACITY까지만 정할 수 있음
    해시 테이블의 초기 크기(threshold)를 계산하기 위해 tableSizeFor() 메서드를 사용함
    해시 테이블의 크기는 항상 2의 거듭제곱으로 커지기 때문에 매개변수로 받은 값에 크거나 같은 2의 거듭 제곱을 초기 용량으로 설정함 
 */
public HashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " +
                initialCapacity);
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " +
                loadFactor);
    this.loadFactor = loadFactor;
    this.threshold = tableSizeFor(initialCapacity);
}

/*
    이 메서드는 주어진 용량(cap)에 대해 2의 거듭제곱인 가장 작은 값을 찾아 반환함
    해시 테이블의 크기가 항상 2의 거듭제곱이 되도록 하기 위함
 */
static final int tableSizeFor(int cap) {
    /*
        Integer.numberOfLeadingZeros() 메서드는 주어진 정수의 앞 쪽에 있는 연속된 0의 개수를 반환함
        cap -1을 하는 이유는 cap이 이미 2의 거듭제곱인 경우, 그 값을 동일하게 유지하기 위함
        
        cap의 값이 10인 경우 Integer.numberOfLeadingZeros()가 반환하는 값은 28
        0000 0000 0000 0000 0000 0000 0000 1001
        cap의 값이 20인 경우 반환되는 값은 27
        0000 0000 0000 0000 0000 0000 0001 0011
        
        >>> 논리적 오른쪽 시프트 연산자는 부호를 무시하고 비트를 오른쪽으로 이동시키며 왼쪽에 빈자리는 항상 0으로 채움
        -1의 이진 표현은 모든 비트가 1로 설정된 값임 (1111 1111 1111 1111 1111 1111 1111 1111)
        Integer.numberOfLeadingZeors()에서 반환된 값 만큼 오른쪽 시프트 연산을 수행함
        
        반환 값이 28인 경우(cap=10): 15 (0000 0000 0000 0000 0000 0000 0000 1111)
        반환 값이 27인 경우(cap=20): 31 (0000 0000 0000 0000 0000 0000 0001 1111)
     */
    int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```

```java
// 초기 용량만 받는 생성자
public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}

// 모두 기본값으로 설정
public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
}

// 다른 맵 구현체를 받는 생성자
public HashMap(Map<? extends K, ? extends V> m) {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
    putMapEntries(m, false);
}
```

### 크기 조정

```java
/*
    해시맵이 가득차거나 새로운 데이터를 추가할 때 테이블 크기를 초기화하거나 두 배로 늘림
    기존 table이 null인 경우 threshold값에 따른 initial capacity로 설정
    해시 테이블의 크기는 무조건 2의 거듭제곱으로 커지기 때문에 
    각 요소가 동일한 인덱스에 유지되거나 새 테이블에서 2의 거듭제곱 오프셋으로 이동해야 함
 */
final Node<K, V>[] resize() {
    Node<K, V>[] oldTab = table;
    // 기존 테이블의 길이가 기존 용량을 의미함
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    // 용량이 0보다 큰 경우
    if (oldCap > 0) {
        /* 
            이미 최대 용량에 도달한 경우, 더 이상 크기를 늘릴 수 없음
            treshold의 값을 최대치로 설정하고 리턴
        */
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        /*
            기존 용량에 두 배를 곱한 값이 최대 용량보다 크지 않으면서
            기존 용량이 기본 용량보다 크거나 같은 경우
         */
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                oldCap >= DEFAULT_INITIAL_CAPACITY)
            // 새 threshold의 값에도 두 배를 곱해줌
            newThr = oldThr << 1; // double threshold
    }
    /*
        기존 table 배열의 길이가 0이지만 threshold의 값이 0보다 큰 경우
        새 배열의 크기를 기존 threshold만큼 늘림
     */
    else if (oldThr > 0) // initial capacity was placed in threshold
        newCap = oldThr;
    /*
        기존 table 배열의 길이가 0이면서 threshold의 값도 0인 경우
        capacity와 threshold의 값을 기본값으로 초기화 
     */
    else {               // zero initial threshold signifies using defaults
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    
    /*
        새 threshold의 값이 0인 경우 (위의 else if 문에서 로직을 처리한 경우만 newThr의 값이 0으로 유지된 상태임)
        새 capacity와 loadFactor의 값을 통해 새 threshold 값을 계산함 (최대 값을 넘지 않게 설정)
     */
    if (newThr == 0) {
        float ft = (float) newCap * loadFactor;
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
                (int) ft : Integer.MAX_VALUE);
    }
    threshold = newThr;
    @SuppressWarnings({"rawtypes", "unchecked"})
    /*
        새로 할당된 capacity, threshold에 따라 새 테이블을 생성하고 기존 요소를 재배치하는 로직
     */
            Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
    table = newTab;
    if (oldTab != null) {
        // 기존 테이블의 모든 슬롯을 순회
        for (int j = 0; j < oldCap; ++j) {
            Node<K, V> e;
            if ((e = oldTab[j]) != null) {
                oldTab[j] = null;
                // 첫 번째 노드가 단일 노드인 경우
                if (e.next == null)
                    newTab[e.hash & (newCap - 1)] = e;
                    // 노드가 TreeNode (트리 구조)인 경우
                else if (e instanceof TreeNode)
                    ((TreeNode<K, V>) e).split(this, newTab, j, oldCap);
                    // 연결 리스트인 경우
                else { // preserve shallowOrder
                    Node<K, V> loHead = null, loTail = null;
                    Node<K, V> hiHead = null, hiTail = null;
                    Node<K, V> next;
                    do {
                        next = e.next;
                        // 기존 해시 코드가 하위 인덱스 그룹에 속하는 경우
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                        // 상위 인덱스 그룹에 속하는 경우
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    // 하위 인덱스 그룹을 새 테이블에 할당
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    // 상위 인덱스 그룹을 새 테이블에 할당
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```

### 트리화, 역트리화

#### 트리화

```java
final void treeifyBin(Node<K, V>[] tab, int hash) {
    int n, index;
    Node<K, V> e;
    // 트리화 조건에 충족되지 않는 경우 리사이징
    if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
        resize();
        // 매개변수로 주어진 hash 값에 대응되는 인덱스에 노드가 있는 경우
    else if ((e = tab[index = (n - 1) & hash]) != null) {
        TreeNode<K, V> hd = null, tl = null;
        // 해당 연결 리스트의 노드들을 트리 노드로 변환
        do {
            TreeNode<K, V> p = replacementTreeNode(e, null);
            if (tl == null)
                hd = p;
            else {
                p.prev = tl;
                tl.next = p;
            }
            tl = p;
        } while ((e = e.next) != null);
        // 트리화
        if ((tab[index] = hd) != null)
            hd.treeify(tab);
    }
}

TreeNode<K, V> replacementTreeNode(Node<K, V> p, Node<K, V> next) {
    return new TreeNode<>(p.hash, p.key, p.value, next);
}

// TreeNode 메서드
final void treeify(Node<K, V>[] tab) {
    TreeNode<K, V> root = null;
    /*
        루프문 초기값의 this는 위의 hd.treeify(tab)을 호출한 hd 인스턴스임
        트리화를 진행할 연결 리스트만 루프문을 돌아서 연결 리스트 구조에서 Red-Black Tree 구조로 변환함
        
        Red-Black Tree는 자기 균형 이진 탐색 트리(self-balancing binary search tree)의 일종으로,
        데이터가 삽입되거나 삭제될 때 트리의 높이가 균형을 유지함
        이 덕분에 탐색, 삽입, 삭제 연산의 최악의 시간 복잡도를 O(log n)으로 유지할 수 있음
        또 다른 자기 균형 이진 탐색 트리로는 AVL 트리가 있음
     */
    for (TreeNode<K, V> x = this, next; x != null; x = next) {
        next = (TreeNode<K, V>) x.next;
        x.left = x.right = null;
        if (root == null) {
            x.parent = null;
            x.red = false;
            root = x;
        } else {
            K k = x.key;
            int h = x.hash;
            Class<?> kc = null;
            for (TreeNode<K, V> p = root; ; ) {
                int dir, ph;
                K pk = p.key;
                if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((kc == null &&
                        (kc = comparableClassFor(k)) == null) ||
                        (dir = compareComparables(kc, k, pk)) == 0)
                    dir = tieBreakOrder(k, pk);

                TreeNode<K, V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    x.parent = xp;
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    root = balanceInsertion(root, x);
                    break;
                }
            }
        }
    }
    moveRootToFront(tab, root);
}
```

#### 역트리화

```java
final Node<K, V> untreeify(HashMap<K, V> map) {
    Node<K, V> hd = null, tl = null;
    // 루프문을 돌면서 연결 리스트 구조로 변환
    for (Node<K, V> q = this; q != null; q = q.next) {
        Node<K, V> p = map.replacementNode(q, null);
        if (tl == null)
            hd = p;
        else
            tl.next = p;
        tl = p;
    }
    return hd;
}
```

### 해시

```java
/*
    해시 코드를 계산하는 메서드
    주어진 키 객체의 해시코드를 기반으로 해시 테이블의 인덱스를 계산함
 */
static final int hash(Object key) {
    int h;
    /*
        ^ 연산자는 XOR(exclusive OR) 배타적 논리합으로, 두 비트가 다를 때 1, 같을 때는 0을 반환함
        key가 null인 경우 해시 값을 0으로 반환함 (해시맵은 단 한 개의 null키를 허용하며, 이는 항상 인덱스 0에 저장됨)
        null이 아닌 경우 key의 해시코드 값을 가져온 뒤, 해시코드와 해시코드를 오른쪽으로 16비트 시프트한 값의 XOR을 계산함
        
        해시코드는 해시 테이블의 인덱스를 결정하는 데 사용됨
        해시코드를 테이블의 크기로 나눈 나머지를 인덱스로 사용하는데, 해시코드가 잘 분포되어야 모든 버킷이 고르게 사용될 가능성이 높음
        따라서 해시 충돌을 줄이기 위해 해시코드의 상위 16비트를 하위비트로 이동시켜 원래의 해시 코드와 XOR 연산을 함   
     */
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

### 삽입

#### 공통 메서드

##### putVal(int, K, V, boolean, boolean)

```java
/*
    특정 키-값 쌍을 tables 배열에 삽입하는 함수
    hash: key의 해시값
    key: 키
    value: 값
    onlyIfAbsent: true인 경우, 기존 노드의 값을 바꾸지 않음(동일한 키와 해시값을 노드가 있는 경우)
    evict: LinkedHashMap 콜백 메서드에 전달하는 값으로, 동일한 키와 해시값을 가진 노드 중 가장 오래된 노드를 삭제함
 */
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node<K, V>[] tab;
    Node<K, V> p;
    int n, i;
    // table이 null이거나 크기가 0인 경우 리사이징
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    // hash에 대응되는 인덱스에 노드가 없는 경우엔 새 노드를 삽입
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
        // 인덱스에 이미 노드가 있는 경우
    else {
        Node<K, V> e;
        K k;
        // key 값과 해시 값이 같은 경우
        if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
            // 트리 구조인 경우
        else if (p instanceof TreeNode)
            e = ((TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
            // 연결 리스트에 저장해야 되는 경우(key값과 해시 값이 다르면서 트리 구조가 아닌 경우)
        else {
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // 연결 리스트의 크기가 트리화 임계값보다 크거나 같은 경우 트리 구조로 변환
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                // 이미 기존에 동일한 키와 해시값을 가진 노드가 있다면 break
                if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        /*
            동일한 키와 해시 값을 가진 노드가 있는 경우
            onlyIfAbsent의 값이 false이거나 기존 노드의 값이 null인 경우(해시 맵은 null값을 허용함)
            새 값을 삽입함
         */
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            // LinkedHashMap 콜백 메서드
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    // 해시맵의 테이블에 존재하는 노드의 개수가 임계값보다 크다면 리사이징
    if (++size > threshold)
        resize();
    // LinkedHashMap 콜백 메서드
    afterNodeInsertion(evict);
    return null;
}
```

##### putMapEntries(Map, boolean)

```java
final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
    int s = m.size();
    if (s > 0) {
        // 테이블 배열이 null인 경우 매개변수로 받은 맵의 크기를 기반으로 테이블 용량 설정
        if (table == null) { // pre-size
            double dt = Math.ceil(s / (double) loadFactor);
            int t = ((dt < (double) MAXIMUM_CAPACITY) ?
                    (int) dt : MAXIMUM_CAPACITY);
            if (t > threshold)
                threshold = tableSizeFor(t);
        } else {
            // Because of linked-list bucket constraints, we cannot
            // expand all at once, but can reduce total resize
            // effort by repeated doubling now vs later
            while (s > threshold && table.length < MAXIMUM_CAPACITY)
                resize();
        }

        // 루프문을 돌면서 맵의 요소를 삽입
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            K key = e.getKey();
            V value = e.getValue();
            putVal(hash(key), key, value, false, evict);
        }
    }
}
```

#### put(K, V)

```java
public V put(K key, V value) {
    /*
        키-값 쌍을 삽입하는 메서드
        onlyIfAbsent의 값을 false로 전달하면 중복된 키가 있는 경우 그 값을 대체함
     */
    return putVal(hash(key), key, value, false, true);
}
```

```java

@Override
public V putIfAbsent(K key, V value) {
    /*
        키-값 쌍을 삽입하는 메서드
        onlyIfAbsent의 값을 true로 전달하면 중복된 키가 없는 경우에만 값이 삽입됨
     */
    return putVal(hash(key), key, value, true, true);
}
```

#### putAll(Map)

```java
public void putAll(Map<? extends K, ? extends V> m) {
    putMapEntries(m, true);
}
```

### 삭제

#### 공통 메서드

##### removeNode(int, Object, Object, boolean, boolean)

```java
/*
    hash: 해시값
    key: 키 값
    value: 값
    matchValue: 해시 충돌로 인해 동일한 키를 가진 노드들이 있을 수 있는데, 값이 동등한 경우에만 삭제하는 플래그 
    movable: 삭제할 노드가 트리 노드인 경우 삭제에 따른 다른 노드들의 이동 여부 플래그
 */
final Node<K, V> removeNode(int hash, Object key, Object value,
                            boolean matchValue, boolean movable) {
    Node<K, V>[] tab;
    Node<K, V> p;
    int n, index;
    // 매개변수로 주어진 hash에 대응되는 인덱스에 노드가 있는 경우에만 삭제
    if ((tab = table) != null && (n = tab.length) > 0 &&
            (p = tab[index = (n - 1) & hash]) != null) {
        Node<K, V> node = null, e;
        K k;
        V v;
        /*
            해시값과 키값을 통해 실제로 삭제할 노드를 찾는 과정
            
            해시값과 키값이 같은 경우
         */
        if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
            node = p;
        /*
            해시값은 같지만, 키 값이 다른 경우
            트리 노드인 경우엔 트리 구조에서 노드를 찾고, 
            아닌 경우 연결 리스트에서 루프문을 돌아서 노드를 찾음
         */
        else if ((e = p.next) != null) {
            if (p instanceof TreeNode)
                node = ((TreeNode<K, V>) p).getTreeNode(hash, key);
            else {
                do {
                    if (e.hash == hash &&
                            ((k = e.key) == key ||
                                    (key != null && key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }
        }
        // 삭제할 노드를 찾았고, matchValue가 true인 경우엔 값까지 동일한 경우
        if (node != null && (!matchValue || (v = node.value) == value ||
                (value != null && value.equals(v)))) {
            // 삭제할 노드가 트리 노드인 경우 TreeNode의 삭제 메서드로 삭제
            if (node instanceof TreeNode)
                ((TreeNode<K, V>) node).removeTreeNode(this, tab, movable);
            /*
                삭제할 노드가 인덱스의 처음에 위치하는 경우
                해당 인덱스가 연결 리스트라면 다음 노드를 인덱스의 처음으로 옮기고
                삭제할 노드만 있다면 node.next는 null이므로 해당 인덱스에 null 처리를 함
             */
            else if (node == p)
                tab[index] = node.next;
            /*
                삭제할 노드가 연결 리스트 중간에 있는 경우
                이전 노드의 next를 삭제할 노드의 next로 수정
             */
            else
                p.next = node.next;
            ++modCount;
            --size;
            // LinkedHashMap 콜백 메서드
            afterNodeRemoval(node);
            return node;
        }
    }
    return null;
}
```

#### remove(Object)

```java
public V remove(Object key) {
    Node<K, V> e;
    // matchValue false 지정, 키가 같으면 제거
    return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
}
```

#### remove(Object, Object)

```java

@Override
public boolean remove(Object key, Object value) {
    // matchValue true 지정, 키와 값이 같으면 제거
    return removeNode(hash(key), key, value, true, true) != null;
}
```

#### clear()

```java
public void clear() {
    Node<K, V>[] tab;
    modCount++;
    // 싹다 null 처리
    if ((tab = table) != null && size > 0) {
        size = 0;
        for (int i = 0; i < tab.length; ++i)
            tab[i] = null;
    }
}
```

### 조회

#### 공통 메서드

##### getNode(Object)

```java
final Node<K, V> getNode(Object key) {
    Node<K, V>[] tab;
    Node<K, V> first, e;
    int n, hash;
    K k;
    // hash(key)에 대응되는 인덱스에 노드가 있는 경우만 노드 검색 시도
    if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & (hash = hash(key))]) != null) {
        /*
            해시 충돌로 인해 키 값이 다르더라도 동일한 인덱스에 노드들이 위치할 수 있음
            해당 인덱스에 위치한 첫 노드의 키 값과 매개변수로 주어진 키 값이 동일한 경우
         */
        if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        /*
            아닌 경우 찾고 있는 노드는 연결 리스트 또는 트리 구조에 속한 노드임
            트리 노드인 경우 TreeNode의 검색 메서드를 호출해서 찾고
            연결 리스트인 경우 루프문을 돌면서 키 값과 동일한 노드가 있는지 확인함
         */
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                return ((TreeNode<K, V>) first).getTreeNode(hash, key);
            do {
                if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```

#### get(Object)

```java
public V get(Object key) {
    Node<K, V> e;
    // getNode() 메서드에서 null을 반환한 경우 null 반환, 아니라면 노드의 값 반환
    return (e = getNode(key)) == null ? null : e.value;
}
```

#### getOrDefault(Object, V)

```java

@Override
public V getOrDefault(Object key, V defaultValue) {
    Node<K, V> e;
    // getNode() 메서드에서 null을 반환한 경우 매개변수로 주어진 기본 값 반환, 아니라면 노드의 값 반환
    return (e = getNode(key)) == null ? defaultValue : e.value;
}
```

#### containsKey(Object)

```java
public boolean containsKey(Object key) {
    return getNode(key) != null;
}
```

#### containsValue(Object)

```java
public boolean containsValue(Object value) {
    Node<K, V>[] tab;
    V v;
    // 이중 루프문을 돌면서 값이 있는지 확인
    if ((tab = table) != null && size > 0) {
        for (Node<K, V> e : tab) {
            for (; e != null; e = e.next) {
                if ((v = e.value) == value ||
                        (value != null && value.equals(v)))
                    return true;
            }
        }
    }
    return false;
}
```

### 업데이트

#### compute(K, BiFunction<? super K, ? super V, ? extends V>)

지정된 키에 대해 BiFunction을 사용하여 새로운 값을 계산하고 업데이트하는 메서드

주어진 키가 존재하지 않거나 null인 경우: 새로 계산된 값이 해시맵에 저장됨

값이 있는 경우: 새로 계산된 값으로 대체함, 만약 계산된 값이 null이면 해시맵에서 항목이 제거됨

```java

@Override
public V compute(K key,
                 BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    if (remappingFunction == null)
        throw new NullPointerException();
    // 해시
    int hash = hash(key);
    Node<K, V>[] tab;
    Node<K, V> first;
    int n, i;
    int binCount = 0;
    TreeNode<K, V> t = null;
    Node<K, V> old = null;
    /*
        경우에 따라 항목을 새로 삽입해야 될 수 있기 때문에
        리사이징이 필요한 경우 미리 수행
     */
    if (size > threshold || (tab = table) == null ||
            (n = tab.length) == 0)
        n = (tab = resize()).length;
    /*
        해시값에 대응되는 인덱스에 노드가 있는 경우
        매개변수로 주어진 키값과 동일한 노드를 찾으려고 시도
        해시충돌로 인해 동일한 인덱스에 여러 노드들이 위치해도 키 값에 대응되는 노드가 없을 수 있음 
     */
    if ((first = tab[i = (n - 1) & hash]) != null) {
        if (first instanceof TreeNode)
            old = (t = (TreeNode<K, V>) first).getTreeNode(hash, key);
        else {
            Node<K, V> e = first;
            K k;
            do {
                if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                    old = e;
                    break;
                }
                ++binCount;
            } while ((e = e.next) != null);
        }
    }
    V oldValue = (old == null) ? null : old.value;
    int mc = modCount;
    // BiFunction 새로운 값 계산 
    V v = remappingFunction.apply(key, oldValue);
    /*
        compute 메서드는 HashMap의 동작 중 동기화되지 않음
        만약 주어진 BiFunction에서 삽입, 삭제같은 구조적 수정을 수행한 경우 예외 처리
     */
    if (mc != modCount) {
        throw new ConcurrentModificationException();
    }
    // 매개변수로 주어진 키 값을 가진 노드가 있는 경우
    if (old != null) {
        // 새로 계산된 값이 null아닌 경우 기존의 값을 대체함
        if (v != null) {
            old.value = v;
            afterNodeAccess(old);
        } else // null인 경우 해당 노드를 삭제함
            removeNode(hash, key, null, false, true);
    } else if (v != null) {
        if (t != null) // 트리 노드인 경우
            t.putTreeVal(this, tab, hash, key, v);
        else { // 매개변수로 주어진 키 값을 가진 노드가 없는 경우, 새 노드 생성
            tab[i] = newNode(hash, key, v, first);
            if (binCount >= TREEIFY_THRESHOLD - 1) // 트리화 임계값을 넘어서면 트리화 진행
                treeifyBin(tab, hash);
        }
        modCount = mc + 1;
        ++size;
        afterNodeInsertion(true);
    }
    return v;
}
```

#### computeIfAbsent(K, BiFunction<? super K, ? super V, ? extends V>)

새로 계산된 값이 null인 경우 compute() 메서드와 달리 기존 항목을 삭제하지 않음

```java

@Override
public V computeIfAbsent(K key,
                         Function<? super K, ? extends V> mappingFunction) {
    // compute와 동일한 로직 ...

    // 새로 계산된 값이 null인 경우 리턴
    if (v == null) {
        return null;
    } else if (old != null) {
        old.value = v;
        afterNodeAccess(old);
        return v;
    } else if (t != null)
        t.putTreeVal(this, tab, hash, key, v);
    else {
        tab[i] = newNode(hash, key, v, first);
        if (binCount >= TREEIFY_THRESHOLD - 1)
            treeifyBin(tab, hash);
    }
    modCount = mc + 1;
    ++size;
    afterNodeInsertion(true);
    return v;
}
```

#### computeIfPresent(K, BiFunction<? super K, ? super V, ? extends V>)

주어진 키에 대한 노드가 있는 경우(값도 null이 아닌 경우)에만 새로 계산된 값으로 대체하는 메서드

새 값이 null인 경우 해당 항목 삭제

```java

@Override
public V computeIfPresent(K key,
                          BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    if (remappingFunction == null)
        throw new NullPointerException();
    Node<K, V> e;
    V oldValue;
    if ((e = getNode(key)) != null &&
            (oldValue = e.value) != null) {
        int mc = modCount;
        V v = remappingFunction.apply(key, oldValue);
        /*
            compute 메서드는 HashMap의 동작 중 동기화되지 않음
            만약 주어진 BiFunction에서 삽입, 삭제같은 구조적 수정을 수행한 경우 예외 처리
        */
        if (mc != modCount) {
            throw new ConcurrentModificationException();
        }
        if (v != null) {
            e.value = v;
            afterNodeAccess(e);
            return v;
        } else {
            int hash = hash(key);
            removeNode(hash, key, null, false, true);
        }
    }
    return null;
}
```

#### merge(K, V, BiFunction<? super V, ? super V, ? extends V>)

키-값, 값을 재계산하는 람다식을 매개변수로 받는 메서드

compute의 람다식은 키와 값을 기반으로 재계산을 하고, merge의 람다식은 해당 키에 대응되는 값과 새로운 값을 기반으로 재계산을 수행함

특징

- 키에 매핑된 노드의 값이 null인 경우 매개변수로 주어진 value로 값을 대체함
- 재계산된 값이 null인 경우 매핑된 항목을 삭제함
- 키에 대응되는 항목을 찾지 못한 경우 새로운 노드를 생성함

```java

@Override
public V merge(K key, V value,
               BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    if (value == null || remappingFunction == null)
        throw new NullPointerException();
    int hash = hash(key);
    Node<K, V>[] tab;
    Node<K, V> first;
    int n, i;
    int binCount = 0;
    TreeNode<K, V> t = null;
    Node<K, V> old = null;
    /*
        경우에 따라 항목을 새로 삽입해야 될 수 있기 때문에
        리사이징이 필요한 경우 미리 수행
     */
    if (size > threshold || (tab = table) == null ||
            (n = tab.length) == 0)
        n = (tab = resize()).length;
    // 키와 해시값이 동일한 노드 찾기
    if ((first = tab[i = (n - 1) & hash]) != null) {
        if (first instanceof TreeNode)
            old = (t = (TreeNode<K, V>) first).getTreeNode(hash, key);
        else {
            Node<K, V> e = first;
            K k;
            do {
                if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                    old = e;
                    break;
                }
                ++binCount;
            } while ((e = e.next) != null);
        }
    }
    if (old != null) { // 노드를 찾은 경우
        V v;
        if (old.value != null) { // 찾은 노드의 값이 null이 아닌 경우(해시맵은 null 값을 허용함)
            int mc = modCount;
            v = remappingFunction.apply(old.value, value); // 기존의 값과 주어진 값을 기반으로 재계산 수행
            if (mc != modCount) {
                throw new ConcurrentModificationException(); // 재계산 도중 구조적 수정을 하면 예외 터뜨림
            }
        } else {
            v = value; // 찾은 노드의 값이 null인 경우
        }
        if (v != null) { // 재계산 결과가 null이 아닌 경우, 찾은 노드의 값 수정
            old.value = v;
            afterNodeAccess(old);
        } else
            removeNode(hash, key, null, false, true); // 재계산 결과가 nul인 경우 찾은 노드 삭제
        return v;
    } else { // 노드를 찾지 못한 경우, 새 노드 생성
        if (t != null)
            t.putTreeVal(this, tab, hash, key, value);
        else {
            tab[i] = newNode(hash, key, value, first);
            if (binCount >= TREEIFY_THRESHOLD - 1)
                treeifyBin(tab, hash);
        }
        ++modCount;
        ++size;
        afterNodeInsertion(true);
        return value;
    }
}
```

### forEach(BiConsumer<? super K, ? super V>)

모든 노드들을 순회해서 키-값에 대한 로직을 수행하는 메서드

주어진 action 메서드에서 구조적 수정을 수행한 경우 예외 처리

```java

@Override
public void forEach(BiConsumer<? super K, ? super V> action) {
    Node<K, V>[] tab;
    if (action == null)
        throw new NullPointerException();
    if (size > 0 && (tab = table) != null) {
        int mc = modCount;
        for (Node<K, V> e : tab) { // 이중 루프문을 돌면서 action 메서드 실행
            for (; e != null; e = e.next)
                action.accept(e.key, e.value);
        }
        if (modCount != mc) // action 메서드에서 구조적 수정을 수행하면 예외를 터뜨림
            throw new ConcurrentModificationException();
    }
}
```

### Iterator

#### HashIterator

해시맵의 모든 이터레이터의 추상 클래스

hasNext, nextNode, remove 메서드를 구현해놓음

```java
abstract class HashIterator {
    Node<K, V> next;        // next entry to return
    Node<K, V> current;     // current entry
    int expectedModCount;  // for fast-fail
    int index;             // current slot

    HashIterator() {
        expectedModCount = modCount; // 이터레이터를 사용하는 중에 modCount의 값이 달라지면 예외 처리를 하기 위해
        Node<K, V>[] t = table;      // modCount 값을 캡처해놓음
        current = next = null;
        index = 0;
        if (t != null && size > 0) { // 노드가 위치한 인덱스 찾기
            do {
            } while (index < t.length && (next = t[index++]) == null);
        }
    }

    public final boolean hasNext() { // 다음 노드가 있는지 확인
        return next != null;
    }

    final Node<K, V> nextNode() { // 다음 노드 반환
        Node<K, V>[] t;
        Node<K, V> e = next;
        if (modCount != expectedModCount) // 이터레이터를 사용하는 중에 다른 곳에서 구조적 수정을 한 경우 예외 처리
            throw new ConcurrentModificationException();
        if (e == null) // next 노드가 없는 경우 예외 처리, 즉 nextNode를 호출하기 전에 hasNext()로 먼저 값이 있는지 확인이 필요함
            throw new NoSuchElementException();
        /*
            해시맵은 각 버킷을 연결 리스트 또는 트리 구조로 관리하기 때문에
            해당 버킷의 모든 노드를 순회하면 다음 인덱스로 이동함
         */
        if ((next = (current = e).next) == null && (t = table) != null) {
            do {
            } while (index < t.length && (next = t[index++]) == null);
        }
        return e;
    }

    public final void remove() { // 이터레이터의 현재 노드 삭제
        Node<K, V> p = current;
        if (p == null)
            throw new IllegalStateException();
        if (modCount != expectedModCount) // 이터레이터를 사용하는 중에 다른 곳에서 구조적 수정을 한 경우 예외 처리
            throw new ConcurrentModificationException();
        // 삭제 후 modCount 캡처 값 수정
        current = null;
        removeNode(p.hash, p.key, null, false, false);
        expectedModCount = modCount;
    }
}
```

#### KeyIterator, ValueIterator, EntryIterator

해시맵은 키, 값, 키-값으로 순회를 돌 수 있는데, 그 부분을 실질적으로 담당하는 클래스

모두 똑같이 HashIterator를 상속받아서 부모를 통해 순회를 도는데, Iterator의 next 메서드로 반환하는 값만 다름

```java
final class KeyIterator extends HashIterator
        implements Iterator<K> {
    public final K next() {
        return nextNode().key; // 키 값 반환
    }
}

final class ValueIterator extends HashIterator
        implements Iterator<V> {
    public final V next() {
        return nextNode().value; // 값 반환
    }
}

final class EntryIterator extends HashIterator
        implements Iterator<Map.Entry<K, V>> {
    public final Map.Entry<K, V> next() {
        return nextNode(); // 노드 반환
    }
}
```

### View

#### KeySet View

현재 맵에 포함된 키의 Set View와 이를 반환하는 메서드

맵 <-> 키 셋 간에 변경사항이 반영되며, 맵이 수정된 후로 keySet을 통해 순회를 돌면 예외가 발생함

```java
// HashMap은 AbstractMap으로부터 상속받은 keySet을 사용함
transient Set<K> keySet;
```

```java
// keySet을 반환하는 메서드, keySet이 null인 경우에만 새로운 KeySet을 생성함
public Set<K> keySet() {
    Set<K> ks = keySet;
    if (ks == null) {
        ks = new KeySet();
        keySet = ks;
    }
    return ks;
}
```

```java
/*
    Key Set View, AbstractSet으로부터 상속받음
    AbstractSet은 AbstractCollection을 상속받는 추상 클래스임
    따라서 remove, removeAll, retainAll 등 삭제 관련 메서드를 사용할 수 있음(삽입은 불가)
 */
final class KeySet extends AbstractSet<K> {
    public final int size() {
        return size;
    }

    public final void clear() {
        HashMap.this.clear();
    }

    // KeyIterator를 통해 키 값을 순회함
    public final Iterator<K> iterator() {
        return new KeyIterator();
    }

    /*
        키에 매핑되는 노드가 있는 경우 true 반환
        public boolean containsKey(Object key) {
            return getNode(key) != null;
        }
     */
    public final boolean contains(Object o) {
        return containsKey(o);
    }

    public final boolean remove(Object key) {
        return removeNode(hash(key), key, null, false, true) != null;
    }

    public final Spliterator<K> spliterator() {
        return new KeySpliterator<>(HashMap.this, 0, -1, 0, 0);
    }

    public Object[] toArray() {
        return keysToArray(new Object[size]);
    }

    public <T> T[] toArray(T[] a) {
        return keysToArray(prepareArray(a));
    }

    // 모든 노드를 순회하면서 key값을 기반으로 로직 수행
    public final void forEach(Consumer<? super K> action) {
        Node<K, V>[] tab;
        if (action == null)
            throw new NullPointerException();
        if (size > 0 && (tab = table) != null) {
            int mc = modCount;
            for (Node<K, V> e : tab) { // 이중 루프문을 돌면서 모든 노드를 순회함
                for (; e != null; e = e.next)
                    action.accept(e.key); // 키 값만 전달
            }
            if (modCount != mc) // 람다식 내부에서 구조적 수정이 발생한 경우 예외를 터뜨림
                throw new ConcurrentModificationException();
        }
    }
}
```

#### Values View

현재 맵에 포함된 값의 Collection View와 이를 반환하는 메서드

set과 일부분의 차이를 제외하고 큰 차이는 없어서 코드는 생략함

- AbstractMap으로부터 상속받은 `Collection<V> values` 필드 사용
- values() 메서드는 Collection 타입으로 value 뷰를 반환함
- Values는 KeySet과 달리 AbstractCollection을 상속받아서 구현함

#### EntrySet View

현재 맵에 포함된 노드의 Set View와 이를 반환하는 메서드

keySet, values와 동일하게 맵 <-> 엔트리 셋 간에 변경사항이 반영되며, 맵이 수정된 후로 entrySet을 통해 순회를 돌면 예외가 발생함

```java
// entrySet은 해시맵에서 관리함
transient Set<Map.Entry<K, V>> entrySet;
```

```java
// entrySet이 null인 경우에만 새 EntrySet 생성
public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> es;
    return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
}
```

```java
final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    public final int size() {
        return size;
    }

    public final void clear() {
        HashMap.this.clear();
    }

    public final Iterator<Map.Entry<K, V>> iterator() {
        return new EntryIterator();
    }

    public final boolean contains(Object o) {
        if (!(o instanceof Map.Entry<?, ?> e))
            return false;
        Object key = e.getKey();
        Node<K, V> candidate = getNode(key);
        return candidate != null && candidate.equals(e);
    }

    public final boolean remove(Object o) {
        if (o instanceof Map.Entry<?, ?> e) {
            Object key = e.getKey();
            Object value = e.getValue();
            return removeNode(hash(key), key, value, true, true) != null;
        }
        return false;
    }

    public final Spliterator<Map.Entry<K, V>> spliterator() {
        return new EntrySpliterator<>(HashMap.this, 0, -1, 0, 0);
    }

    // 모든 노드를 순회하면서 키-값 쌍을 기반으로 로직 수행
    public final void forEach(Consumer<? super Map.Entry<K, V>> action) {
        Node<K, V>[] tab;
        if (action == null)
            throw new NullPointerException();
        if (size > 0 && (tab = table) != null) {
            int mc = modCount;
            for (Node<K, V> e : tab) { // 이중 루프문을 돌면서 모든 노드를 순회함
                for (; e != null; e = e.next)
                    action.accept(e);
            }
            if (modCount != mc) // 람다식 내부에서 구조적 수정이 발생한 경우 예외를 터뜨림
                throw new ConcurrentModificationException();
        }
    }
}
```