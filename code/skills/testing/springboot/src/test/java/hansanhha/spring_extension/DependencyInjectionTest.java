package hansanhha.spring_extension;

import hansanhha.OrderService;
import hansanhha.Product;
import hansanhha.ProductRepository;
import hansanhha.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class DependencyInjectionTest {

    /*
        SpringExtension이 생성자 또는 메서드에 의존성 주입을 수행할 수 있는 어노테이션
        - @BeforeTransaction, @AfterTransaction (spring annotation)
        - @BeforeAll, @AfterAll, @BeforeEach, @AfterEach, @Test, @RepeatedTest, @ParameterizedTest 등
     */

    private final ProductService productService;
    private final ProductRepository productRepository;

    // 필드 주입
    @Autowired
    private OrderService orderService;

    // 생성자 주입
    @Autowired
    public DependencyInjectionTest(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Test
    @DisplayName("SpringExtension은 테스트 클래스의 생성자에 선언된 @Autowired를 인식하여 의존성 주입한다")
    void constructorInjectionTest() {
        Product product = productService.create("test product", 10, 10_000);

        Assertions.assertThat(product).isNotNull();
    }

    // 라이프사이클 메서드 @BeforeEach 파라미터 주입
    @BeforeEach
    void setUp(@Autowired ProductService lifecycleMethodInjection) {
        if (lifecycleMethodInjection == null) {
            throw new TestInstantiationException("라이프사이클 메서드 의존성 주입 실패");
        }

        System.out.println("라이프사이클 메서드(@BeforeEach) 의존성 주입 성공");
    }

    // 라이프사이클 메서드 @BeforeAll 파라미터 주입
    @BeforeAll
    static void init(@Autowired ProductService lifecycleMethodInjection) {
        if (lifecycleMethodInjection == null) {
            throw new TestInstantiationException("라이프사이클 메서드 의존성 주입 실패");
        }

        System.out.println("라이프사이클 메서드(@BeforeAll) 의존성 주입 성공");
    }

    // 메서드 파라미터 주입
    @Test
    @DisplayName("SpringExtension은 테스트 메서드의 각 파라미터에 선언된 @Autowired를 인식하여 의존성 주입한다")
    void methodParameterInjectionTest(@Autowired ProductService paramProductService) {
        Product product = paramProductService.create("test product", 10, 10_000);

        Assertions.assertThat(product).isNotNull();
    }

}
