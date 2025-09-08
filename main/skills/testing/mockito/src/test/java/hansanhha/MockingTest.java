package hansanhha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MockingTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("@Mock과 @InjectMocks 어노테이션을 사용한 모킹 테스트")
    void annotationMockInjectionTest() {
        Product product = productService.create("test product", 10, 10_000);

        // productService는 productRepository에서 반환한 값을 그대로 반환하는데
        // productRepository는 mock 객체이므로 아무 동작을 수행하지 않기 때문에 null을 반환한다
        Assertions.assertNull(product);
    }

    @Test
    @DisplayName("프로그래밍 방식으로 모킹")
    void programmaticMockingTest() {
        ProductRepository mock = Mockito.mock(ProductRepository.class);
        ProductService productService_ = new ProductService(mock);

        Product product = productService_.create("test product", 10, 10_000);

        // productService는 productRepository에서 반환한 값을 그대로 반환하는데
        // productRepository는 mock 객체이므로 아무 동작을 수행하지 않기 때문에 null을 반환한다
        Assertions.assertNull(product);
    }

}
