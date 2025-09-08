package hansanhha;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class EndpointConfig {

    @Bean
    public OrderInfoEndpoint orderInfoEndpoint() {
        return new OrderInfoEndpoint();
    }

    @Bean
    public SimpleEndpoint simpleEndpoint() {
        return new SimpleEndpoint();
    }

    @WebEndpoint(id = "simple")
    public static class SimpleEndpoint {

        @ReadOperation
        public String get() {
            return "hello simple endpoint";
        }

        // 중복된 @XXXOperation 메서드를 선언할 수 없다
        // @Selector를 사용하여 경로를 구분하면 여러 개를 선언할 수 있다
//        @ReadOperation
//        public String duplicatedReadOperation() {
//            return "duplicated @ReadOperation";
//        }

        @ReadOperation
        public String usingSelector(@Selector String id) {
            return "hello simple endpoint, id: " + id;
        }
    }

    // 주문 정보에 대한 커스텀 엔드포인트 설정
    @WebEndpoint(id = "order-info")
    public static class OrderInfoEndpoint {

        private final Map<Long, Order> orders = new HashMap<>();

        @ReadOperation
        public Order get(@Selector Long id) {
            return orders.get(id);
        }

        @WriteOperation
        public void save(@Selector Long id) {
            orders.put(id, Order.create(id));
        }

        @DeleteOperation
        public void delete(@Selector Long id) {
            orders.remove(id);
        }
    }

    public record Order(
            String name,
            int amount) {

        private static Order create(Long id) {
            return new Order(UUID.randomUUID().toString(), (int) (id * 10_000));
        }

    }

}
