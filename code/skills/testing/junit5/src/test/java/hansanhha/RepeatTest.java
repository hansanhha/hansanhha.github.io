package hansanhha;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.RepeatedTest.CURRENT_REPETITION_PLACEHOLDER;

public class RepeatTest {

    // 반복 테스트 수행, @DisplayName으로 전체 테스트의 이름을 표시하고
    // @RepeatedTest의 name 속성으로 각 반복 테스트의 이름을 지정한다
    // 반복 테스트 메서드 간의 실행은 모두 독립적이다
    @RepeatedTest(value = 5, name = CURRENT_REPETITION_PLACEHOLDER + "번째 테스트")
    @DisplayName("반복 테스트")
    void repeatedTest() {
        System.out.println("5번 반복하는 테스트");
    }

}
