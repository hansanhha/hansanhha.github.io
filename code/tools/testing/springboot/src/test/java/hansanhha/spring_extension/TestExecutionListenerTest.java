package hansanhha.spring_extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@TestExecutionListeners(LoggingTestExecutionListener.class)
@ExtendWith(SpringExtension.class)
public class TestExecutionListenerTest {

    @Test
    void contextLoads() {
    }
}
