package hansanhha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Variance {

    // 불공변
    public static class Invariance {
        public static void main(String[] args) {
            // 자바의 제네릭은 기본적으로 불공변이다
            // 따라서 List<Number> 참조변수는 List<Integer>, List<Long> 등을 허용하지 않는다
//        List<Number> list1 = new ArrayList<Integer>();
//        List<Number> list2 = new ArrayList<Long>();
//        List<Number> list3 = new ArrayList<Double>();

            // 타입이 완전히 동일해야만 추가(producer), 접근(consumer)이 가능해진다
            List<Integer> integerList = new ArrayList<>();
            integerList.add(1);
            System.out.println(integerList.getFirst());
        }
    }

    // 공변, ? extends upperbound
    public static class UpperBound_Covariance {
        public static void main(String[] args) {
            // 일반적인 타입 매개변수 사용 (불공변)
            List<Integer> normalGenericsList = new ArrayList<>();
            normalGenericsList.add(10);
            normalGenericsList.add(20);

            // extends 키워드를 사용하여 upper bound와 그 하위 타입으로 타입 매개변수를 제한한다
            // upper bound 지정(Integer 포함), upper bound는 데이터 접근이 가능하다(consumer)
            List<? extends Number> upperBoundList = normalGenericsList;
            Number n = upperBoundList.getFirst();

            // 하위 타입으로 접근하는 경우 명시적인 캐스팅이 필요하다
            Integer i = (Integer) upperBoundList.getFirst();
            System.out.println(n);

            // upper bound는 어느 하위 타입으로나 삽입할 수 있기 때문에
            // 타입 안정성을 보장할 수 없게 되므로 데이터 추가를 금지한다
//            upperBoundList.add(1L);
//            upperBoundList.add(1.1);
//            upperBoundList.add(1);
        }
    }

    // 반공변, ? super lowerbound
    public static class LowerBound_Contravariance {
        public static void main(String[] args) {
            // lower bound 지정
            List<? super Integer> lowerBoundList = new ArrayList<Number>();

            // lower bound는 데이터를 추가할 수 있다(consumer)
            lowerBoundList.add(10);
            lowerBoundList.add(20);
            lowerBoundList.add(30);

            // 대신 데이터에 접근할 수 없다
//            Number n = lowerBoundList.get(0);
//            Integer i = lowerBoundList.get(0);

            // 명시적인 타입 캐스팅으로 특정 타입으로 접근할 수 있다
            // 하지만 상황에 따라 런타임 예외가 발생할 수 있다 (부모가 지원하지 않는 기능을 호출하는 등의 문제)
            Number n = (Number) lowerBoundList.getFirst();
            Integer i = (Integer) lowerBoundList.get(1);

            System.out.println(n);
            System.out.println(i);
        }
    }
}
