package hansanhha;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.SpanCustomizer;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class PaymentClient {

    private static final URI PAYMENT_REQUEST_URI = URI.create("http://localhost:8083/payment");

    private final RestTemplate rest;
    private final Tracer tracer;
    private final RestTemplate restTemplate;
    private final SpanCustomizer spanCustomizer;

    public PaymentClient(RestTemplate rest, Tracer tracer, RestTemplate restTemplate, SpanCustomizer spanCustomizer) {
        this.rest = rest;
        this.tracer = tracer;
        this.restTemplate = restTemplate;
        this.spanCustomizer = spanCustomizer;
    }

    public void processPayment(int amount) {
        Span paymentClientSpan = tracer.nextSpan().name("call payment service")
                .tag("service", "order")
                .tag("amount", amount)
                .start();

        try (Tracer.SpanInScope pamentClientSpanInScope = tracer.withSpan(paymentClientSpan)) {
            String paymentRequestUri = UriComponentsBuilder.fromUri(PAYMENT_REQUEST_URI)
                    .queryParam("amount", amount)
                    .toUriString();

            String response = restTemplate.postForObject(paymentRequestUri, null, String.class);
            paymentClientSpan.event("payment service response: " + response);
        } catch (Exception e) {
            paymentClientSpan.error(e);
            throw e;
        }
    }
}
