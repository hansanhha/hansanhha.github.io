package hansanhha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// @NotNull과 @Size를 합성한 합성 어노테이션
@Target({ElementType.FIELD, ElementType.PARAMETER,
        ElementType.RECORD_COMPONENT, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
@NotNull
@Size(min = 0)
public @interface PositiveNumber {
}
