package hansanhha.spring_extension;

import hansanhha.Product;
import hansanhha.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "classpath:product-data.sql")
@DisplayName("SpringExtension의 @Sql 스크립트 실행 테스트")
public class SqlScriptTest {

    @Test
    @Transactional
    @DisplayName("테스트 실행 전 sql 스크립트가 실행되어 데이터를 준비한다")
    void dataQueryTest(@Autowired ProductRepository productRepository) {
        Product testProduct1 = productRepository.findByName("test product-100").orElse(null);
        Product testProduct2 = productRepository.findByName("test product-200").orElse(null);
        Product testProduct3 = productRepository.findByName("test product-300").orElse(null);

        assertThat(testProduct1).isNotNull();
        assertThat(testProduct2).isNotNull();
        assertThat(testProduct3).isNotNull();
    }

}
