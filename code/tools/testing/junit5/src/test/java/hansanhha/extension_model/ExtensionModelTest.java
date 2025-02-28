package hansanhha.extension_model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Greeting
@ExtendWith(GreetingTestInstancePostProcessor.class)
public class ExtensionModelTest {

    @Test
    void postProcessTest() {

    }
}
