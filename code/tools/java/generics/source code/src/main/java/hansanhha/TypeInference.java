package hansanhha;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TypeInference {

    public static void main(String[] args) {

        // 1. 다이아몬드 연산자 타입 추론
        // 좌변의 List<String>을 기반으로 타입 추론에 의해 타입 매개변수를 <String>으로 결정한다
        List<String> diamondOperator = new ArrayList<>();


        // 2. 제네릭 메서드 타입 추론
        // 전달된 인자 값의 타입을 기반으로 제네릭 메서드의 타입 매개변수를 추론한다
        String str = genericMethodTypeInference("hello"); // 타입 매개변수 <String>으로 추론
        Integer integer = genericMethodTypeInference(10); // 타입 매개변수 <Integer>로 추론
        boolean bool = genericMethodTypeInference(false); // 타입 매개변수 <Boolean>으로 추론


        // 3. 타겟 타입
        // 타겟 타입은 표현식의 결과가 사용되는 문맥을 기반으로 타입을 추론한다
        /*
            3.1 변수 할당
            - 컴파일러는 참조 변수의 제네릭 타입 매개변수를 타겟 타입으로 설정해서 타입을 추론한다
            - 변수 Comparator<String>을 통해 람다 표현식 (s1, s2)의 매개변수 타입을 String으로 추론한다
         */
        Comparator<String> stringComparator = (s1, s2) -> s1.length() - s2.length();

        /*
            3.2 메서드 호출 인자 (제네릭 메서드 X)
            - 메서드의 파라미터 타입이 제네릭이거나, 함수형 인터페이스를 사용하는 경우 메서드의 파라미터 타입을 타겟 타입으로 설정해서 인자의 타입을 추론한다
            - List<String>의 sort 메서드는 Comparator<String>을 요구하므로, (s1, s2)의 타입은 String으로 추론한다
         */
        List<String> list = Arrays.asList("one", "two", "three");
        list.sort((s1, s2) -> s1.compareToIgnoreCase(s2));

        /*
            3.3 반환 타입
            - 제네릭 메서드에서 반환 타입이 명확하지 않을 때 메서드 호출 시점에서 전달된 반환 타입을 기반으로 적절한 타입을 추론한다

            Stream.of() 메서드는 Stream<T>를 반환하는 메서드이다
            반환 타입이 Stream<String>이므로 컴파일러는 T를 String으로 추론한다
         */
        Stream<String> stream = Stream.of("A", "B", "C");

        // 3.4 삼항 연산자
        Integer value = true ? 1 : 2;


        /*
            4. 람다 표현식과 타겟 타입
            - 함수형 인터페이스의 함수 디스크립터(메서드 시그니처)를 확인한다
            - 람다 표현식의 매개변수 타입과 리턴 타입을 타겟 타입에 맞게 추론한다
         */
        Runnable run = () -> System.out.println("Runnable Interface");
        Predicate<Integer> p = i -> i < 100;

    }

    public static <T> T genericMethodTypeInference(T a) {
        return a;
    }
}
