package hansanhha.condition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleConfiguration {

    @Bean
    @Conditional(SimpleClassPathCondition.class)
    public Apple apple() {
        return new Apple();
    }

    @Bean
    @Conditional(SimplePropertyCondition.class)
    public Peach peach() {
        return new Peach();
    }

    private class Apple {
    }

    private class Peach {
    }
}
