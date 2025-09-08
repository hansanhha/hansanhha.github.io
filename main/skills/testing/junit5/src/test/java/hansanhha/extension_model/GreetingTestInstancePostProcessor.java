package hansanhha.extension_model;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class GreetingTestInstancePostProcessor implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        if (testInstance.getClass().isAnnotationPresent(Greeting.class)) {
            System.out.println("detected @Greeting annotation by GreetingTestInstancePostProcessor");
        }
    }
}
