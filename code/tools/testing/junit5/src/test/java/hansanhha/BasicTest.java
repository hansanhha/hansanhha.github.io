package hansanhha;

import org.junit.jupiter.api.*;

public class BasicTest {

    OrderService orderService = new OrderService();
    ProductService productService = new ProductService();

    Product product;

    // 테스트 클래스의 모든 테스트 메서드를 실행하기 전 호출되는 메서드
    @BeforeAll
    static void init() {
        System.out.println("order test started");
    }

    // 테스트 클래스의 모든 테스트 메서드를 실행한 후 호출되는 메서드
    @AfterAll
    static void cleanUp() {
        System.out.println("order test completed");
    }

    // 각 테스트 메서드 실행 전 호출되는 메서드
    @BeforeEach
    void productSetup() {
        product = productService.create("test product", 10, 10_000);
    }

    // 각 테스트 메서드 실행 후 호출되는 메서드
    @AfterEach
    void tearDown() {
        productService.delete(product.getId());
    }

    // 테스트 메서드
    @Test
    // 테스트 메서드의 이름 지정
    @DisplayName("주문이 완료되면 상품의 재고 수량을 차감한다")
    void orderTest() {
        int originalQuantity = product.getQuantity();

        Orders orders = orderService.order(product, 5);

        Assertions.assertEquals(originalQuantity - orders.quantity(), product.getQuantity());
    }

    @Test
    @Order(Integer.MIN_VALUE)
    void orderFirstTest() {
        System.out.println("order test: first");
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void orderLastTest() {
        System.out.println("order test: last");
    }

    @Test
    // @Timeout(초)
    @Timeout(1)
    void timeoutTest() {
    }

    @Test
    @Disabled("실행 제외 테스트")
    void disabledTest() {
        System.out.println("@Disabled에 의해 실행되지 않는 테스트");
    }

}
