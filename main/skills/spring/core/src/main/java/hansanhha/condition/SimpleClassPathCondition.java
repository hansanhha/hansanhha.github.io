package hansanhha.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class SimpleClassPathCondition implements Condition {

    // 클래스패스에 라이브러리가 있는 경우 true 반환
    // 특정 클래스가 있으면 라이브러리가 있다고 판단함
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            Class.forName("org.springframework.context.annotation.Bean");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
