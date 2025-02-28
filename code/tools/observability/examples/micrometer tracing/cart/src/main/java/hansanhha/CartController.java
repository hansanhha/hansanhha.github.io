package hansanhha;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController {

    private final Tracer tracer;

    public CartController(Tracer tracer) {
        this.tracer = tracer;
    }

    @NewSpan
    @PostMapping("/cart")
    public ResponseEntity<String> updateCartQuantity(@SpanTag("cart.productId") @RequestParam("id") int productId,
                                                     @SpanTag("cart.quantity") @RequestParam("quantity") int quantity) {
        return ResponseEntity.ok("decreased " + quantity + " quantity of product id: " + productId);
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
