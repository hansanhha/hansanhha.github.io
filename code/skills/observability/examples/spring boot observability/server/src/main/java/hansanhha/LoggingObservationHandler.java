package hansanhha;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

@Component
public class LoggingObservationHandler implements ObservationHandler<Observation.Context> {

    private static final Logger log = LoggerFactory.getLogger(LoggingObservationHandler.class);

    @Override
    public void onStart(Observation.Context context) {
        log.info("before running the observation for context [{}], service [{}]", context.getName(), getServiceNameFromContext(context));
    }

    @Override
    public void onStop(Observation.Context context) {
        log.info("after running the observation for context [{}], service [{}]", context.getName(), getServiceNameFromContext(context));
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    private String getServiceNameFromContext(Observation.Context context) {
        return StreamSupport.stream(context.getLowCardinalityKeyValues().stream().spliterator(), false)
                .filter(keyValue -> "service".equals(keyValue.getKey()))
                .map(KeyValue::getValue)
                .findFirst()
                .orElse("unknown");
    }
}
