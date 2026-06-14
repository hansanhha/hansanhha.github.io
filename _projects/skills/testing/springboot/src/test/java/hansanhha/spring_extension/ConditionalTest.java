package hansanhha.spring_extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ConditionalTest {

    @Test
    @DisplayName("맥OS에서만 실행되는 테스트 - 스프링 @EnabledIf")
    @EnabledIf(expression = "#{systemProperties['os.name'].toLowerCase().contains('mac')}",
            reason = "Enabled on Mac OS")
    void runOnlyOnMacOs() {

    }

    @Test
    @DisplayName("윈도우에서만 실행되는 테스트 - 스프링 @EnabledIf")
    @EnabledIf(expression = "#{systemProperties['os.name'].toLowerCase().contains('window')}",
            reason = "Enabled on Mac OS")
    void runOnlyOnWindows() {

    }

}
