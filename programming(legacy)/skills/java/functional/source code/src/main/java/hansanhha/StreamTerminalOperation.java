package hansanhha;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamTerminalOperation {

    public static void main(String[] args) {

        String[] originalDataSource = new String[]{"1", "2", "3", "4", "5"};

        // 스트림 최종 연산 특징
        // 1. lazy evaluation: originalDataSoure에 접근하지 않고, 데이터 변환 작업도 실행하지 않는 파이프라인만 설정된 상태
        Stream<Integer> toIntegerStream = Stream.of(originalDataSource).map(Integer::valueOf);

        // 2. short-circuit evaluation: 모든 스트림 데이터를 소모하지 않고, 연산 조기 종료
        Optional<Integer> first = Stream.of(originalDataSource).map(Integer::valueOf).findFirst();

        // 스트림 최종 연산 reduce
        Integer reduceResult = Stream.of(originalDataSource)
                .map(Integer::valueOf)
//                 reduce(0, Integer::sum);
                .reduce(0, (a, b) -> a + b);

        System.out.println(reduceResult);
    }
}
