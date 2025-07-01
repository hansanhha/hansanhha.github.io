package hansanhha.order.events;

import hansanhha.order.Order.OrderIdentifier;

public record OrderRequestEvent(OrderIdentifier orderId) {
}
