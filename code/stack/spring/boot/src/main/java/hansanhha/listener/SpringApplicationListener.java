package hansanhha.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class SpringApplicationListener implements ApplicationListener<SpringApplicationEvent> {

    /**
     * SpringApplicationEvent : Springboot의 SpringApplication 생명주기에 따라 미리 정의한 ApplicationEvent
     * ApplicationContext가 생성되기 전에 발생하는 이벤트의 경우, Bean을 통해 이벤트를 처리할 수 없으므로 SpringApplicationEvent를 사용
     */
    @Override
    public void onApplicationEvent(SpringApplicationEvent event) {
        log.info("Detected Event by SpringApplicationListener : SpringApplicationEvent");
        log.info("event : {}", event.getClass().getSimpleName());
        log.info("event source : {}", event.getSource());
    }
}
