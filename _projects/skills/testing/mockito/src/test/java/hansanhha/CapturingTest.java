package hansanhha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CapturingTest {


    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    // mockito로부터 해당 제네릭 타입에 대한 ArgumentCaptor 주입받기
    @Captor
    ArgumentCaptor<Product> injectedProductArgumentCaptor;


    @Test
    @DisplayName("@Captor로 ArgumentCaptor 주입받아서 메서드 인자 캡처하기")
    void annotationArgumentCapturingTest() {

        productService.create("test product", 10, 10_000);

        // 메서드 인자 캡처
        Mockito.verify(productRepository).save(injectedProductArgumentCaptor.capture());

        // 캡처된 메서드 인자 추출 및 검증
        Product captorValue = injectedProductArgumentCaptor.getValue();
        Assertions.assertEquals("test product", captorValue.getName());
    }

    @Test
    @DisplayName("클래스 타입으로 메서드 인자 캡처하기")
    void classTypeArgumentCapturingTest() {

        // Product 클래스 타입으로 ArgumentCaptor 생성
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

        productService.create("test product", 10, 10_000);

        // 메서드 인자 캡처
        Mockito.verify(productRepository).save(productArgumentCaptor.capture());

        // 캡처된 메서드 인자 추출 및 검증
        Product captorValue = productArgumentCaptor.getValue();
        Assertions.assertEquals("test product", captorValue.getName());
    }

    @Test
    @DisplayName("제네릭 타입으로 메서드 인자 캡처하기")
    void genericTypeArgumentCapturingTest() {

        // 제네릭 타입으로 ArgumentCaptor 생성
        // captor 메서드에 인자를 전달하지 않아야 한다
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.captor();

        productService.create("test product", 10, 10_000);

        // 메서드 인자 캡처
        Mockito.verify(productRepository).save(productArgumentCaptor.capture());

        // 캡처된 메서드 인자 추출 및 검증
        Product captorValue = productArgumentCaptor.getValue();
        Assertions.assertEquals("test product", captorValue.getName());
    }

    @Test
    @DisplayName("stub 설정과 함께 메서드 인자 캡처하기")
    void argumentCaptureWithStubTest() {

        // 제네릭 타입으로 ArgumentCaptor 생성
        // captor 메서드에 인자를 전달하지 않아야 한다
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.captor();

        Mockito.doReturn(new Product("test product", 10, 10_000))
                .when(productRepository)
                .save(Mockito.any(Product.class));

        // stub과 함께 메서드 인자 캡처
        Mockito.doNothing()
                .when(productRepository)
                .update(Mockito.any(Product.class), productArgumentCaptor.capture());

        Product product = productService.create("test product", 10, 10_000);
        productService.updateName(product, "new product name");

        // 캡처된 메서드 인자 추출 및 검증
        Product captorValue = productArgumentCaptor.getValue();
        Assertions.assertEquals("new product name", captorValue.getName());
    }


}
