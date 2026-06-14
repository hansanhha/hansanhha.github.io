package hansanhha.payment.service;

import hansanhha.payment.entity.Payment;
import hansanhha.payment.repository.PaymentRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final @NonNull PaymentRepository paymentRepository;

    public void request(Payment payment) {
        LOG.info("request ready payment for paymentRequestId : {}, order id :{}, amount : {}, status : {}.",
                payment.getPaymentIdentifier(), payment.getOrderId(), payment.getAmount(), payment.getStatus().name());

        Payment requested = payment.requested();

        paymentRepository.save(requested);

        LOG.info("complete ready payment for paymentRequestId : {}, order id :{}, amount : {}, status : {}.",
                payment.getPaymentIdentifier(), payment.getOrderId(), payment.getAmount(), payment.getStatus().name());
    }

    public void pay(Payment payment) {
        LOG.info("request approve payment for paymentRequestId : {}, order id :{}, amount : {}, status : {}.",
                payment.getPaymentIdentifier(), payment.getOrderId(), payment.getAmount(), payment.getStatus().name());

        Payment approved = payment.approved();

        paymentRepository.save(approved);

        LOG.info("complete approve payment for paymentRequestId : {}, order id :{}, amount : {}, status : {}.",
                payment.getPaymentIdentifier(), payment.getOrderId(), payment.getAmount(), payment.getStatus().name());
    }
}
