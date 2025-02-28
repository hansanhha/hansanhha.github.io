package hansanhha.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingService {

    private static final String TRACE_ID = "traceId";
    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final String REQUEST_PROCESSING_DURATION = "duration";

    public void LogRequest(String requestId) {

        MDC.put(TRACE_ID, UUID.randomUUID().toString());
        MDC.put(REQUEST_START_TIME, String.valueOf(System.currentTimeMillis()));

        try {
            Thread.sleep(100);
            throw new IllegalStateException();
        }
        catch (Throwable e) {
        }
        finally {
            String traceId = MDC.get(TRACE_ID);
            long duration = System.currentTimeMillis() - Long.parseLong(MDC.get(REQUEST_START_TIME));
            MDC.put(REQUEST_PROCESSING_DURATION, String.valueOf(duration));
            log.info("request handling");
        }
    }
}
