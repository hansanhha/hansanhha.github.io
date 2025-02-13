package hansanhha.order.events;

import hansanhha.order.Order.OrderIdentifier;
import org.jmolecules.event.types.DomainEvent;

public record OrderCompletedEvent(OrderIdentifier orderId) implements DomainEvent {
}
