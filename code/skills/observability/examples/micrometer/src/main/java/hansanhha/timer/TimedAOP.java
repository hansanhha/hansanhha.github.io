package hansanhha.timer;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.aop.MeterTag;

public class TimedAOP {

    // @Timed 어노테이션을 사용하여 메서드 실행 전후로 타이머를 자동으로 시작/정지하여 실행 시간을 측정한다
    @Timed(value = "timer.timed", description = "time taken for a example task")
    public void executeTask() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // @MeterTag를 통해 파라미터의 값을 태그에 추가한다
    @Timed(value = "timer.timed.metertag")
    public void executeTask(@MeterTag(key = "name") String name) {
    }

    // longTask 속성을 활성화하면 Timer 대신 Long Task Timer가 사용된다
    @Timed(value = "timer.timed.long", longTask = true, description = "long running task execution time")
    public void executeLongTask() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
