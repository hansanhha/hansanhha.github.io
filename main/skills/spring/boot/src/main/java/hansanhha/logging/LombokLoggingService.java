package hansanhha.logging;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LombokLoggingService {

    @PostConstruct
    void logging() {
        log.info("lombok logging service constructed");
        log.debug("lombok logging service constructed");
    }
}
