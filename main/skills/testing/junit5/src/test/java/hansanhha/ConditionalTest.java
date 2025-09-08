package hansanhha;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.*;
import org.w3c.dom.ls.LSOutput;

public class ConditionalTest {

    @Test
    @EnabledOnOs(value = OS.MAC, disabledReason = "맥os에서만 실행하는 테스트")
    void runOnlyOnMacOS() {
        System.out.println("맥os에서만 실행되는 테스트");
    }

    @Test
    @EnabledOnOs(value = OS.WINDOWS, disabledReason = "윈도우에서만 실행하는 테스트")
    void runOnlyOnWindows() {
        System.out.println("윈도우에서만 실행되는 테스트");
    }

    @Test
    @DisabledOnOs(OS.MAC)
    void runExceptMacOS() {
        System.out.println("맥os만 빼고 실행되는 테스트");
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void runExceptWindows() {
        System.out.println("윈도우만 빼고 실행되는 테스트");
    }

    @Test
    @EnabledOnJre(JRE.JAVA_21)
    void runOnlyJava21() {
        System.out.println("자바 21에서만 실행되는 테스트");
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    void runExceptJava8() {
        System.out.println("자바 8만 빼고 실행되는 테스트");
    }

    @Test
    @EnabledIfSystemProperty(named = "os.name", matches = ".*Windows.*")
    void runOnlyWindowsByProperty() {
        System.out.println("os.name의 프로퍼티에 윈도우가 포함된 경우에만 실행되는 테스트");
    }

    @Test
    @DisabledIfSystemProperty(named = "os.name", matches = ".*Windows.*")
    void runExceptWindowsByProperty() {
        System.out.println("os.name의 프로퍼티에 윈도우가 포함되지 않은 경우에만 실행되는 테스트");
    }

}
