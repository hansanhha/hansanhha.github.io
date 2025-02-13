package hansanhha.spring_extension;

import hansanhha.Product;
import hansanhha.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = ProductService.class)
public class TestBeanAnnotationTest {

    @TestBean(enforceOverride = true, methodName = "createFakeProductService")
    ProductService productService;

    @Test
    @DisplayName("@TestBean을 이용하면 원래 객체를 덮어쓴 테스트용 빈을 사용할 수 있다")
    void testBeanUsingTest() {

        Product product = productService.create("test product", 10, 10_000);

        assertThat(product).isNull();
    }

    static ProductService createFakeProductService() {
        return new FakeProductService();
    }

    static class FakeProductService extends ProductService {

        public FakeProductService() {
            super(null);
        }

        @Override
        public Product create(String name, int quantity, int amount) {
            System.out.println("가짜 ProductService 객체 create 메서드 호출");
            return null;
        }
    }
}
