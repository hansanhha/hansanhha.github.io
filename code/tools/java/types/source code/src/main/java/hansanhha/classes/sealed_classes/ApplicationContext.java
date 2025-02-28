package hansanhha.classes.sealed_classes;

public sealed interface ApplicationContext
        extends BeanFactory
        permits AnnotationApplicationContext {

    void run();
}
