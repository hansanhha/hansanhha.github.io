package hansanhha.logging;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NormalLoggingService {

    private static final Logger log = LoggerFactory.getLogger(NormalLoggingService.class);

    @PostConstruct
    void logging() {
        log.info("normal logging service constructed");
        log.debug("normal logging service constructed");
    }
}
