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
public class StubbingTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("when().thenReturn()을 활용한 특정 값 반환 스텁 테스트")
    void stub_toReturnSpecificValue_usingWhenThenReturn() {

        String originalProductName = "test product";
        String stubbedProductName = "stubbed product";

        // when().thenReturn() stubbing
        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenReturn(new Product(stubbedProductName, 10, 10_000));

        Product product = productService.create(originalProductName, 10, 10_000);

        // productService.create 메서드에 전달한 값과 상관없이 productRepository에 스텁된 Product 인스턴스를 반환한다
        Assertions.assertNotEquals(originalProductName, product.getName());
        Assertions.assertEquals(stubbedProductName, product.getName());
    }

    @Test
    @DisplayName("doReturn().when()을 활용한 특정 값 반환 스텁 테스트")
    void stub_toReturnSpecificValue_usingDoReturnWhen() {

        String originalProductName = "test product";
        String stubbedProductName = "stubbed product";

        // doReturn().when().save() stubbing
        Mockito.doReturn(new Product(stubbedProductName, 10, 10_000))
                .when(productRepository)
                .save(Mockito.any(Product.class));

        Product product = productService.create(originalProductName, 10, 10_000);

        // productService.create 메서드에 전달한 값과 상관없이 productRepository에 스텁된 Product 인스턴스를 반환한다
        Assertions.assertNotEquals(originalProductName, product.getName());
        Assertions.assertEquals(stubbedProductName, product.getName());
    }

    @Test
    @DisplayName("when().thenThrow()을 활용한 특정 예외 발생 스텁 테스트")
    void stub_toReturnSpecificValue_usingWhenThenThrow() {

        // when().thenThrow() stubbing
        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenThrow(new RuntimeException("Product 저장 중 예외 발생"));

        Assertions.assertThrowsExactly(RuntimeException.class, () -> productService.create("test product", 10, 10_1000));
    }

    @Test
    @DisplayName("doThrow().when()을 활용한 특정 예외 발생 스텁 테스트")
    void stub_toReturnSpecificValue_usingDoThrowWhen() {

        // doThrow().when().save() stubbing
        Mockito.doThrow(new RuntimeException("Product 저장 중 예외 발생"))
                .when(productRepository)
                .save(Mockito.any(Product.class));

        Assertions.assertThrowsExactly(RuntimeException.class, () -> productService.create("test product", 10, 10_1000));
    }

}
