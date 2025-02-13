## Object

## equals(), hashCode()

객체 비교와 해시 기반 컬렉션(HashMap, HashSet) 등에서 필수적인 메서드

equals와 hashCode는 항상 함께 오버라이딩해야 된다

equals만 재정의한 경우: 해시 기반 컬렉션에서 객체를 제대로 찾지 못한다

hashCode만 재정의한 경우: 논리적으로 동일한 객체로 간주되지 않는다

### boolean equals()

```java
public boolean equals(Object obj) {
    return (this == obj);
} 
```

Object 클래스의 equals 메서드는 기본적으로 참조(메모리 주소)를 비교한다

두 객체가 같은 메모리 주소를 가리키고 있을 때만 true를 반환한다

#### equals 오버라이딩

객체의 내용(값)을 기준으로 비교하고 싶을 때 오버라이딩한다 (사용자 정의 클래스에서 두 객체의 특정 필드 값을 기준으로 비교하고자 할 때)

#### equals 오버라이딩 규약

- 반사성(reflexive): x.equals(x)는 항상 true
- 대칭성(symmetric): x.equals(y)가 true라면 y.equals(x)도 true
- 추이성(transitive): x.equals(x)가 true이고 y.equals(z)가 true이면 x.equals(z)도 true
- 일관성(consistent): 객체 상태가 변하지 않는 한 x.equals(y)는 동일한 결과를 반환
- null과의 비교: x.equals(null)은 항상 false

### int hashCode()

```java
@IntrinsicCandidate
public native int hashCode();
```

Object 클래스의 hashCode 메서드는 객체의 메모리 주소를 해싱한 값(고유 식별 번호)을 반환한다

#### hashCode 오버라이딩

해시 기반 컬렉션에서 객체를 올바르게 저장하고 검색하려면 equals와 hashCode를 함께 오버라이딩해야 한다

#### hashCode 오버라이딩 규약

- 두 객체의 equals()가 true라면 반드시 같은 hashCode()를 반환해야 한다
- 두 객체의 equals()가 false라면 같은 hashCode를 반환할 수도 있고 아닐 수도 있다
- 동일한 객체에 hashCode()는 일관성있게 같은 값을 반환해야 한다




