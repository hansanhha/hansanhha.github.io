package hansanhha.spring_extension;

import hansanhha.Product;
import hansanhha.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TestConstructorAnnotationTest {

    private final ProductService productService;

    public TestConstructorAnnotationTest(ProductService productService) {
        this.productService = productService;
    }

    @Test
    @DisplayName("@TestConstructor를 활용하여 @Autowired를 명시하지 않고 의존성 주입을 받을 수 있다")
    void constructorInjectionByTestConstructor() {
        Product product = productService.create("test product", 10, 10_000);

        assertThat(product).isNotNull();
    }

}
