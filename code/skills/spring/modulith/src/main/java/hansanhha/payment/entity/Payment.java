package hansanhha.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;

import java.util.UUID;

import static hansanhha.payment.entity.Payment.PaymentIdentifier;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment implements AggregateRoot<Payment, PaymentIdentifier> {

    public static Payment create(String orderId, Integer amount) {
        return new Payment(orderId, amount);
    }

    private Payment(String orderId, Integer amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentIdentifier = new PaymentIdentifier(UUID.randomUUID().toString());
        this.status = PaymentStatus.PENDING;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String orderId;

    @Embedded
    private PaymentIdentifier paymentIdentifier;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;


    @Embeddable
    public static record PaymentIdentifier(String paymentRequestId) implements Identifier {}

    @Override
    public PaymentIdentifier getId() {
        return paymentIdentifier;
    }

    public Payment requested() {
        status = PaymentStatus.REQUESTED;
        return this;
    }

    public Payment approved() {
        status = PaymentStatus.APPROVED;
        return this;
    }

    public Payment failed() {
        status = PaymentStatus.FAILED;
        return this;
    }

}
