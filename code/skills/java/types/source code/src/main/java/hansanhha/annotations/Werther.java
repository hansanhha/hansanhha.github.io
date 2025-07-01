package hansanhha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// @Sorrow 어노테이션을 메타 어노테이션으로 활용
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Sorrow(age = "young")
public @interface Werther {
    String author() default "Johann Wolfgang von Goethe";
}
