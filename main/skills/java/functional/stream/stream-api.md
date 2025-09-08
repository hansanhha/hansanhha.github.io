---
layout: default
title:
---

[terminal operation](#terminal-operation)
- [reduce](#terminal-operation---reduce)
- [collect](#terminal-operation---collect)

## terminal operation

스트림 최종 연산은 스트림 내부에 존재하는 데이터(stream data source)를 소모(consumption)하여 결과를 생성하는 단계임

최종 연산 이후 해당 스트림을 더 이상 사용할 수 없으므로, 필요하면 새로운 스트림을 생성해야 됨

#### lazy evaluation

스트림 파이프라인 연산은 최종 연산이 호출되기 전까지 실제 데이터 처리가 발생하지 않는 지연 실행 방식을 채택함

아래와 같이 중간 연산인 map 메서드만 호출한 경우 originalDataSoure에 접근하지 않고, 데이터 변환 작업도 실행하지 않는 파이프라인만 설정된 상태로 남음
```java
String[] originalDataSource = new String[]{"1", "2", "3", "4", "5"};
Stream<Integer> toIntegerStream = Stream.of(originalDataSource).map(Integer::valueOf);
``` 

#### short-circuit evaluation

일부 최종 연산은 모든 스트림 데이터를 소모하지 않고 조기 종료 할 수 있음

findFirst 최종 연산 메서드는 첫 번째 요소만 찾으면 스트림 처리를 중단함 
```java
Optional<Integer> first = Stream.of(originalDataSource).map(Integer::valueOf).findFirst();
```

### terminal operation - reduce

reduce 메서드는 스트림의 모든 요소를 하나로 축약(reduce)하는 최종 연산임

스트림 요소를 반복적으로 결합하여 원하는 결과를 만들고 싶을 때 사용함 (합계, 최소/최대값 등)

#### reduce method signature

`T reduce(T identity, BinaryOperator<T> accumulator)`

identity
- 연산 초기값
- 결합 연산에 영향을 미치지 않는 중립적인 값이어야 함
- e.g 합계 연산의 초기값: 0, 곱셈의 초기값: 1

accumulator
- 스트림의 요소를 결합하는 함수로, 람다식 형태로 두 개의 입력을 받아 하나의 결과를 반환함
- `(a, b) -> a + b`

`Optional<T> reduce(BinaryOperator<T> accumulator)`

초기값이 없음, 스트림이 비어있는 경우 Optional.empty 반환

`<U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner)`

병렬 스트림에서 사용되는 메서드로, 병렬 스트림 전용 결합 연산자(combiner)가 추가됨

#### how it works

1. 스트림 첫 번째 요소와 초기값(identity) 결합
2. 이후 스트림의 각 요소를 이전 결과와 결합
3. 최종적으로 하나의 값을 반환

### terminal operation - collect

collect 메서드는 스트림 요소를 컬렉션이나 다른 데이터 구조로 축약(reduce)하거나 결합(aggregate)하는 최종 연산임

#### collect method signature

`<R, A> R collect(Collector<? super T, A, R> collector)`

[collector](./collector.md)
- 스트림 데이터 수집 방법을 정의한 인터페이스로
- T: 스트림 요소 타입
- A: 중간 결과를 저장할 누적자 타입
- R: 최종 반환 타입

스트림 요소를 특정 데이터 구조로 변환하거나 요소 통계 계산, 집계, 그룹화/분할, 문자열 연결 등의 다양한 기능을 제공함 

#### how it works

1. 스트림 요소를 파라미터로 전달받은 Collector를 통해 작업 수행
2. 초기 누적자 생성(supplier)
3. 스트림의 각 요소를 처리하여 누적자에 저장(accumulator)
4. 병렬 스트림의 경우, 각 서브 스트림의 결과를 병합(combiner)
5. 최종 결과 반환(finisher)