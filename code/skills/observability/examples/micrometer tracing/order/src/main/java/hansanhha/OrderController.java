package hansanhha;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class OrderController {

    private final Tracer tracer;
    private final PaymentClient paymentClient;
    private final CartClient cartClient;

    public OrderController(Tracer tracer, PaymentClient paymentClient, CartClient cartClient) {
        this.tracer = tracer;
        this.paymentClient = paymentClient;
        this.cartClient = cartClient;
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout() {

        Random random = new Random();

        int productId = random.nextInt(0, 100);
        int quantity = random.nextInt(0, 10);
        int amount = random.nextInt(10_000, 100_000);

        Span orderSpan = tracer.nextSpan()
                .name("order controller - checkout order")
                .tag("service", "order")
                .tag("productId", productId)
                .tag("quantity", quantity)
                .tag("amount", amount)
                .start();

        try (Tracer.SpanInScope orderSpanInScope = tracer.withSpan(orderSpan)) {
            paymentClient.processPayment(amount);

            cartClient.decreaseQuantity(productId, quantity);

            orderSpan.event("order completed");
            return ResponseEntity.ok("order checkout successfully");
        }
    }

    @GetMapping("/span")
    public ResponseEntity<String> getCurrentSpan() {
        Span currentSpan = tracer.currentSpan();

        String spanInfo = "no active span";

        if (currentSpan != null) {
            TraceContext context = currentSpan.context();

            spanInfo =
                    """
                    current span info
                    - trace id: %s
                    - span id: %s
                    - parent id: %s
                    """.formatted(context.traceId(), context.spanId(), context.parentId());
        }

        return ResponseEntity.ok(spanInfo);
    }
}
