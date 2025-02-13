package hansanhha.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SimplePropertyCondition implements Condition {

    // 프로퍼티 정보가 있는 경우 true 반환
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().containsProperty("spring.datasource.url");
    }
}
