package hansanhha;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class VerificationTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("mock 객체의 메서드가 일정 횟수만큼 호출된다")
    void invocationCountVerificationTest() {

        int invocationCount = 3;

        for (int i = 0; i < invocationCount; i++) {
            productService.create("test product" + i, 10, 10_000);
        }

        Mockito.verify(productRepository, Mockito.times(invocationCount)).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("mock 객체의 메서드가 단 한번만 호출된다")
    void onlyInvocationOnceVerificationTest() {

        productService.create("test product", 10, 10_000);

        Mockito.verify(productRepository, Mockito.only()).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("mock 객체의 메서드가 단 한번도 호출되지 않는다 ")
    void neverInvocationVerificationTest() {

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("mock 객체의 메서드가 일정 시간 안에 수행된다")
    void timeOutVerificationTest() {

        productService.create("test product", 10, 10_000);

        Mockito.verify(productRepository, Mockito.timeout(10)).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("mock 객체의 메서드 호출 검증이 일정 시간 이후 수행된다")
    void afterInvocationVerificationTest() {

        int invocationCount = 3;

        for (int i = 0; i < invocationCount; i++) {
            productService.create("test product" + i, 10, 10_000);
        }

        Mockito.verify(productRepository, Mockito.after(100).times(invocationCount)).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("mock 객체의 메서드 실제 호출 횟수가 최대 호출 횟수를 넘기지 않는다")
    void atMostVerificationTest() {

        int invocationCount = 3;
        int maxInvocationCount = 5;

        for (int i = 0; i < invocationCount; i++) {
            productService.create("test product" + i, 10, 10_000);
        }

        Mockito.verify(productRepository, Mockito.atMost(maxInvocationCount)).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("mock 객체의 메서드 실제 호출 횟수가 최소 호출 횟수보다 더 많이 호출된다")
    void atLeastVerificationTest() {

        int invocationCount = 5;
        int minInvocationCount = 3;

        for (int i = 0; i < invocationCount; i++) {
            productService.create("test product" + i, 10, 10_000);
        }

        Mockito.verify(productRepository, Mockito.atLeast(minInvocationCount)).save(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("mock 객체 메서드의 호출 순서를 검증한다")
    void inOrderVerificationTest() {

        productService.create("test product-1", 10, 10_000);
        productService.create("test product-2", 10, 10_000);
        productService.create("test product-3", 10, 10_000);

        InOrder inOrder = Mockito.inOrder(productRepository);

        inOrder.verify(productRepository).save(new Product("test product-1", 10, 10_000));
        inOrder.verify(productRepository).save(new Product("test product-2", 10, 10_000));
        inOrder.verify(productRepository).save(new Product("test product-3", 10, 10_000));
    }

    @Test
    @DisplayName("여러 mock 객체 메서드의 호출 순서를 검증한다")
    void inOrderSeveralMockVerificationTest() {

        List<String> list = Mockito.mock(List.class);

        productService.create("test product-1", 10, 10_000);
        list.add("1");
        productService.create("test product-2", 10, 10_000);
        list.add("2");
        productService.create("test product-3", 10, 10_000);
        list.add("3");

        InOrder inOrder = Mockito.inOrder(productRepository, list);

        inOrder.verify(productRepository).save(new Product("test product-1", 10, 10_000));
        inOrder.verify(list).add("1");
        inOrder.verify(productRepository).save(new Product("test product-2", 10, 10_000));
        inOrder.verify(list).add("2");
        inOrder.verify(productRepository).save(new Product("test product-3", 10, 10_000));
        inOrder.verify(list).add("3");
    }

    @Test
    @DisplayName("mock 객체 메서드의 호출 순서와 파라미터를 검증한다")
    void callsVerificationTest() {

        productService.create("test product-1", 10, 10_000);
        productService.create("test product-1", 10, 10_000);
        productService.create("test product-1", 10, 10_000);
        productService.create("test product-2", 10, 10_000);
        productService.create("test product-3", 10, 10_000);

        InOrder inOrder = Mockito.inOrder(productRepository);

        // calls 메서드는 inOrder와 함께 사용될수만 있다
        // 다른 호출 메서드와 다르게 실제 호출 횟수와 달라도 주어진 호출 횟수만 만족하면 검증된 것으로 판단한다
        // test product-1에 대한 save 메서드 호출은 실제로 3번했지만 call(2)는 참으로 평가한다
        inOrder.verify(productRepository, Mockito.calls(2)).save(new Product("test product-1", 10, 10_000));
        inOrder.verify(productRepository, Mockito.calls(1)).save(new Product("test product-2", 10, 10_000));
        inOrder.verify(productRepository, Mockito.calls(1)).save(new Product("test product-3", 10, 10_000));
    }

    @Test
    @DisplayName("mock 객체와 상호작용하지 않는다 - verifyNoInteractions()")
    void noInteractionVerificationTest() {

        // ProductRepository와 아예 상호작용 자체를 하지 않는지 검증한다
        Mockito.verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("mock 객체와 더이상 상호작용하지 않는다 - verifyNoMoreInteractions()")
    void noMoreInteractionVerificationTest() {

        productService.create("test product", 10, 10_000);
        productService.create("test product", 10, 10_000);

        // ProductRepository.save() 메서드를 두 번 호출한 후 더 이상 상호작용을 하지 않는지 검증한다
        // 만약 save() 메서드를 더 호출하거나 다른 메서드와 상호작용하면 검증에 실패한다
        Mockito.verify(productRepository, Mockito.times(2)).save(Mockito.any(Product.class));
        Mockito.verifyNoMoreInteractions(productRepository);
    }

}
