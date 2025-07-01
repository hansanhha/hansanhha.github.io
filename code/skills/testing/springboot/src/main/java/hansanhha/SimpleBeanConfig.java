package hansanhha;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleBeanConfig {

    @Bean
    Book book() {
        return new Book();
    }

    @Bean
    Calculator calculator() {
        return new Calculator();
    }

    public static class Book {
    }

    public static class Calculator {
    }
}
