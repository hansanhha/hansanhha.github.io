package hansanhha;

import java.util.UUID;

public record Orders(
        Long id,
        String orderNumber,
        OrderStatus status,
        Long productId,
        int quantity,
        int amount) {

    public static Orders order(Long id, Long productId, int quantity, int amount) {
        return new Orders(id, UUID.randomUUID().toString(), OrderStatus.ORDERED, productId, quantity, amount);
    }

    public Orders shipping() {
        return new Orders(id, orderNumber, OrderStatus.SHIPPING, productId, quantity, amount);
    }

    public Orders shipped() {
        return new Orders(id, orderNumber, OrderStatus.SHIPPED, productId, quantity, amount);
    }


    enum OrderStatus {

        ORDERED,
        SHIPPING,
        SHIPPED
    }

}
