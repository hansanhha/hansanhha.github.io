package hansanhha.payment.events;

import hansanhha.order.Order.OrderIdentifier;
import hansanhha.payment.entity.Payment.PaymentIdentifier;

public record PaymentCompletedEvent(PaymentIdentifier paymentId, OrderIdentifier orderId) {
}
