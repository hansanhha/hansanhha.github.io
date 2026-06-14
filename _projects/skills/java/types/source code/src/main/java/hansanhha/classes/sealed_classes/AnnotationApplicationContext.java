package hansanhha.classes.sealed_classes;

public final class AnnotationApplicationContext implements ApplicationContext {

    @Override
    public void run() {
        System.out.println("application context started");
    }

    @Override
    public void registerBean() {
        System.out.println("registered bean in container");
    }
}
