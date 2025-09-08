package hansanhha.timer;

import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.TimeUnit;

public class FunctionTimers {

    private final OperationStats stats;

    /*
        FunctionTimer는 빌더를 이용하여 생성할 수 있다
        첫 번째 인자: 메트릭 이름
        두 번째 인자: 통계 값 제공 객체
        세 번째 인자: count function
        네 번째 인자: total time function
        다섯 번째 인자: 시간 단위
     */
    public FunctionTimers(MeterRegistry registry) {
        stats = new OperationStats();

        FunctionTimer.builder("timer.function", stats, OperationStats::getCount, OperationStats::getTotalTime, TimeUnit.NANOSECONDS)
                .description("a function-tracking timer that measures operation durations")
                .register(registry);
    }

    public void executeTask() {
        stats.record(10);
    }

    public void executeOther() {
        stats.record(100);
    }

    // 상태 객체는 내부적으로 작업 횟수(count)와 작업에 걸린 총 시간(totalTimeInNanos)을 업데이트한다
    private static class OperationStats {
        private long count;
        private long totalTimeInNanos;

        public void record(long durationInNanos) {
            count++;
            totalTimeInNanos += durationInNanos;
        }

        public long getCount() {
            return count;
        }

        public double getTotalTime() {
            return totalTimeInNanos;
        }
    }
}
