package hansanhha.annoations;

import hansanhha.annotations.A;
import hansanhha.annotations.B;
import hansanhha.annotations.C;
import org.junit.jupiter.api.Test;

public class AnnotationTest {

    @Test
    void declarationAnnotation_And_TypeAnnotation() {
        @A int @B [] @C [] f;
    }
}
