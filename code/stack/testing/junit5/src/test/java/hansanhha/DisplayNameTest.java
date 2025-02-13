package hansanhha;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.condition.EnabledOnOs;

@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
public class DisplayNameTest {

    @Test
    @DisplayName("더하기 테스트")
    void addPositiveNumber() {
        int result = 1 + 2;
        Assertions.assertEquals(3, result);
    }

    @Nested
    @IndicativeSentencesGeneration(separator = "->", generator = ReplaceUnderscores.class)
    class the_weeknd {

        @Test
        void after_hours() {
        }

        @Test
        void blinding_lights() {

        }
    }
}
