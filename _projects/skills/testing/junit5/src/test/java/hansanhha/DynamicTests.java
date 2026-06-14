package hansanhha;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicTests {

    @TestFactory
    Stream<DynamicTest> createTestInRuntime() {

        // DynamicTest.stream(동적 입력 값, displayNameGenerator, 테스트 실행 로직)
        return DynamicTest.stream(
                Stream.iterate(0, i -> i <= 10, i -> i + 1),
                this::getDisplayName,
                this::doAssertion);
    }

    private String getDisplayName(int number) {
        return number % 2 == 0
                ? number + "는 짝수이다"
                : number + "는 홀수이다";
    }

    private void doAssertion(int number) {
        if (number % 2 == 0) {
            assertEquals(0, number % 2);
            return;
        }

        assertEquals(1, number % 2);
    }
}
