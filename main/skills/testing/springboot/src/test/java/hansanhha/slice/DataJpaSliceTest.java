package hansanhha.slice;

import hansanhha.Product;
import hansanhha.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// 실제 데이터베이스에 대해 테스트를 실행하는 경우
// @AutoConfigureTestDatabase(replace = Replace.NONE)
public class DataJpaSliceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Product 엔티티를 저장하고 조회한다")
    void testRepository() {
        entityManager.persist(new Product("test product", 10, 10_000));

        Product product = productRepository.findByName("test product").orElse(null);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("test product");
    }
}
