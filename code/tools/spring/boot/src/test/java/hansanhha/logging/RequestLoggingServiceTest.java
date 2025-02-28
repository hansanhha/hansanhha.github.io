package hansanhha.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = RequestLoggingService.class)
class RequestLoggingServiceTest {

    @Autowired
    private RequestLoggingService requestLoggingService;

    @Test
    @DisplayName("로그 데이터 활용 예시")
    void errorLog() {
        requestLoggingService.LogRequest("123");
    }
}