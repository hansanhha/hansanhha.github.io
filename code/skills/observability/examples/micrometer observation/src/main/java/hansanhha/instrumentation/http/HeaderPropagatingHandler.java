package hansanhha.instrumentation.http;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.transport.SenderContext;

public class HeaderPropagatingHandler implements ObservationHandler<SenderContext<Object>> {

    @Override
    public void onStart(SenderContext<Object> context) {
        context.getSetter().set(context.getCarrier(), "foo", "bar");
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return context instanceof SenderContext;
    }
}
