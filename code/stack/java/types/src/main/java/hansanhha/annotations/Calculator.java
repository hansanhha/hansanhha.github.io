package hansanhha.annotations;

public @interface Calculator {

    Predicate value() default @Predicate;
}
