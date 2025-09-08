package hansanhha.spring_extension;

import hansanhha.SimpleBeanConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static hansanhha.SimpleBeanConfig.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SpringExtensionTest {

    @Nested
    @ExtendWith(SpringExtension.class)
    @DisplayName("SpringExtension을 통해 junit과 스프링 테스트 컨텍스트 프레임워크를 통합한다")
    class SimpleSpringExtensionUsingTest {

        @Test
        @DisplayName("스프링 ApplicationContext를 주입받는다")
        void contextLoads(@Autowired ApplicationContext context) {
            assertThat(context).isNotNull();
        }

    }


    @Nested
    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = SimpleBeanConfig.class)
    @DisplayName("SpringExtension과 @ContextConfiguration을 통해 특정 부분만 애플리케이션 컨텍스트에 로드한다")
    class ApplicationContextConfigurationTest {

        @Test
        @DisplayName("@ContextConfiguration에 명시한 SimpleBeanConfig만 컨텍스트에 등록한다")
        void contextLoads(@Autowired ApplicationContext context) {
            Book book = (Book) context.getBean("book");
            Calculator calculator = (Calculator) context.getBean("calculator");

            assertThat(book).isNotNull();
            assertThat(calculator).isNotNull();

            // SimpleBeanConfig에서 구성하지 않은 다른 빈은 컨텍스트에 로드되지 않는다
            assertThatThrownBy(() -> context.getBean("orderService")).isInstanceOf(BeansException.class);
        }
    }

    @Nested
    @SpringJUnitConfig(classes = SimpleBeanConfig.class)
    @DisplayName("@SpringJUnitConfig를 통해 애플리케이션 컨텍스트를 간단하게 설정할 수 있다")
    class SpringJUnitConfigTest {

        @Test
        @DisplayName("@SpringJUnitConfig에 명시한 SimpleBeanConfig만 컨텍스트에 등록한다")
        void contextLoads(@Autowired ApplicationContext context) {
            Book book = (Book) context.getBean("book");
            Calculator calculator = (Calculator) context.getBean("calculator");

            assertThat(book).isNotNull();
            assertThat(calculator).isNotNull();

            // SimpleBeanConfig에서 구성하지 않은 다른 빈은 컨텍스트에 로드되지 않는다
            assertThatThrownBy(() -> context.getBean("orderService")).isInstanceOf(BeansException.class);
        }
    }

}
