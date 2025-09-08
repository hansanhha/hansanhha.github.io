package hansanhha;

import java.util.ArrayList;
import java.util.List;

public class GenericTypeCasting {

    public static void main(String[] args) {

        // String 타입 매개변수 지정
        List<String> genericsList = new ArrayList<>();
        genericsList.add("Hello");

        // 타입 매개변수로 String을 지정했으므로 다른 타입을 허용하지 않음
//        genericsList.add(1234); // 컴파일 오류 발생

        // 제네릭으로 인해 컴파일러가 타입을 알고 있기 때문에
        // 자동 타입 캐스팅이 지원됨
        String first = genericsList.get(0);

        // 컴파일러의 타입 캐스팅 코드 자동 삽입
        // String first = (String) genericsList.get(0);
    }
}
