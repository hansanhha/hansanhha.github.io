package hansanhha;

import java.util.ArrayList;
import java.util.List;

public class RowTypeCasting {

    public static void main(String[] args) {

        // 타입 매개변수 지정 X
        List rawList = new ArrayList();

        // String, Integer 타입 등 서로 다른 타입의 객체 혼합 가능
        rawList.add("Hello");
        rawList.add(1234);

        // List에 어느 타입이 들어간지 런타임에 알 수 없기 때문에 요소를 꺼낼 때 타입 캐스팅이 명시적으로 필요하다
        String first = (String) rawList.get(0);
        System.out.println(first);

        // 두 번째 넣은 요소는 Integer 타입이기 때문에 런타임에 ClassCastException 발생한다
        String second = (String)rawList.get(1);
    }
}
