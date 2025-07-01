package hansanhha.payment.events;

import hansanhha.payment.entity.Payment.PaymentIdentifier;

public record PaymentRequestEvent(PaymentIdentifier paymentId) {
}
