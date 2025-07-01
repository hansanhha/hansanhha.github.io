package hansanhha.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;

// UserContext에 대한 로깅 작업을 수행하는 ObservationHandler 구현체
public class UserObservationHandler implements ObservationHandler<UserContext> {

    @Override
    public void onStart(UserContext context) {
        System.out.println("handler[START] " + "data: " + context);
    }

    @Override
    public void onError(UserContext context) {
        System.out.println("handler[ERROR] " + "data: " + context + ", error: " + context.getError());
    }

    @Override
    public void onEvent(Observation.Event event, UserContext context) {
        System.out.println("handler[EVENT] " + "event: " + event + ", data: " + context);
    }

    @Override
    public void onStop(UserContext context) {
        System.out.println("handler[STOP]  " + "data: " + context);
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return context instanceof UserContext;
    }
}
