package hansanhha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SpyingTest {

    @Spy
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("@Spy와 @InjectMocks 어노테이션을 사용한 스파이 테스트")
    void annotationSpyInjectionTest() {
        Product product = productService.create("test product", 10, 10_000);

        // productService는 productRepository에서 반환한 값을 그대로 반환하는데
        // productRepository는 spy 객체이므로 원본 객체의 동작을 유지하기 때문에 로직이 정상적으로 실행되므로 반환된 product는 null이 아니다
        Assertions.assertNotNull(product);
    }

    @Test
    @DisplayName("@Spy와 @InjectMocks 어노테이션을 사용한 스파이 테스트")
    void programmaticSpyInjectionTest() {
        ProductRepository spy = Mockito.spy(ProductRepository.class);
        ProductService productService_ = new ProductService(spy);

        Product product = productService_.create("test product", 10, 10_000);

        // productService는 productRepository에서 반환한 값을 그대로 반환하는데
        // productRepository는 spy 객체이므로 원본 객체의 동작을 유지하기 때문에 로직이 정상적으로 실행되므로 반환된 product는 null이 아니다
        Assertions.assertNotNull(product);
    }
}
