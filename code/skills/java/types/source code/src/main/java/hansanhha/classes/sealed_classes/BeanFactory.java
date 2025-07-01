package hansanhha.classes.sealed_classes;

public sealed interface BeanFactory permits ApplicationContext {

    void registerBean();
}
