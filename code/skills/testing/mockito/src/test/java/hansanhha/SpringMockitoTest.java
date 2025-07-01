package hansanhha;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

public class SpringMockitoTest {

    @Nested
    @SpringBootTest
    @DisplayName("스프링 빈 모킹 테스트")
    class SpringBeanMockingTest {

        // 스프링 부트 3.4부터 deprecated된 어노테이션, 3.6 삭제 예정
    //    @MockBean
    //    private SpringProductRepository productRepository;

        @MockitoBean
        private SpringProductRepository productRepository;

        @Autowired
        private SpringProductService productService;

        @Test
        @DisplayName("스프링 목 빈 stub 테스트")
        void springMockBeanStubTest() {

            Mockito.doReturn(new Product("stubbed product", 10, 10_100))
                    .when(productRepository)
                    .save(Mockito.any(Product.class));

            Product product = productService.create("test product", 10, 10_000);

            Assertions.assertThat(product.getName()).isNotEqualTo("test product");
            Assertions.assertThat(product.getName()).isEqualTo("stubbed product");
        }

    }


    @Nested
    @SpringBootTest
    @DisplayName("스프링 스파이 빈 테스트")
    class SpringBeanSpyingTest {

        // 스프링 부트 3.4부터 deprecated된 어노테이션, 3.6 삭제 예정
//        @SpyBean
//        private SpringProductRepository productRepository;

        @MockitoSpyBean
        private SpringProductRepository productRepository;

        @Autowired
        private SpringProductService productService;

        @Test
        @DisplayName("스프링 스파이 빈 stub 테스트")
        void springSpyBeanStubTest() {

            Mockito.doReturn(new Product("stubbed product", 10, 10_100))
                    .when(productRepository)
                    .save(Mockito.any(Product.class));

            Product product = productService.create("test product", 10, 10_000);

            Assertions.assertThat(product.getName()).isNotEqualTo("test product");
            Assertions.assertThat(product.getName()).isEqualTo("stubbed product");
        }

        @Test
        @DisplayName("스프링 스파이 빈 실제 로직 수행 테스트")
        void springSpyBeanActualMethodTest() {
            Product product = productService.create("test product", 10, 10_000);

            Assertions.assertThat(product.getName()).isEqualTo("test product");
            Assertions.assertThat(product.getName()).isNotEqualTo("stubbed product");
        }

    }

}
