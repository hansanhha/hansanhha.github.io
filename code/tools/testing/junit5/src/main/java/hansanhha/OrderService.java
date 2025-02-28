package hansanhha;

import java.util.HashMap;
import java.util.Map;

public class OrderService {

    private final Map<Long, Orders> orderRepository = new HashMap<>();

    public Orders order(Product product, int quantity) {
        product.decrease(quantity);
        Orders orders = Orders.order((long) orderRepository.size() + 1, product.getId(), quantity, product.getAmount() * quantity);
        orderRepository.put(orders.id(), orders);
        return orders;
    }

    public Orders shipping(Long id) {
        Orders orders = orderRepository.get(id);
        Orders shipping = orders.shipping();

        orderRepository.replace(id, orders, shipping);
        return shipping;
    }

    public Orders shipped(Long id) {
        Orders orders = orderRepository.get(id);
        Orders shipped = orders.shipped();

        orderRepository.replace(id, orders, shipped);
        return shipped;
    }

    public Orders get(Long id) {
        return orderRepository.get(id);
    }
}
